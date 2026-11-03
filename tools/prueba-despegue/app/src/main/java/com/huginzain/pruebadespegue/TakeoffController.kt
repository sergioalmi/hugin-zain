package com.huginzain.pruebadespegue

/** Fases de la prueba de despegue. */
enum class Phase { ON_GROUND, TAKING_OFF, CLIMBING, HOLDING, LANDING, MANUAL }

/** Telemetría mínima que necesita la prueba; `altitudeM` es null si aún no hay dato. */
data class Telemetry(val isFlying: Boolean, val isAutoTakeoff: Boolean, val altitudeM: Double?)

/** Orden que la actividad traduce a llamadas del MSDK. */
sealed interface Command {
    data object None : Command
    data object TakeOff : Command
    /** Mantener esta altura sobre el punto de despegue con joysticks virtuales. */
    data class HoldAltitude(val meters: Double) : Command
    data class Land(val reason: String) : Command
}

/**
 * Lógica pura de la prueba: despegar, subir hasta [targetM] y mantenerse, con abortos.
 * Sin dependencias de DJI ni de Android, para probarla en la JVM. Se llama a 10 Hz con [tick].
 */
class TakeoffController(
    private val targetM: Double = 2.0,
    private val toleranceM: Double = 0.15,
    private val maxAltitudeM: Double = 3.0,
    private val phaseTimeoutMs: Long = 15_000,
    private val telemetryTimeoutMs: Long = 1_000,
) {
    var phase = Phase.ON_GROUND
        private set
    private var phaseStartMs = 0L
    private var lastAltitudeMs = 0L
    private var takeoffRequested = false
    private var landRequested = false

    /** Botón DESPEGAR: solo vale en tierra. */
    fun requestTakeoff() {
        if (phase == Phase.ON_GROUND) takeoffRequested = true
    }

    /** Botón ATERRIZAR: vale siempre (repite la orden si el aterrizaje falló; en tierra solo si vuela). */
    fun requestLanding() {
        landRequested = true
    }

    /** El mando ha recuperado el control: la app deja de enviar órdenes de vuelo. */
    fun pilotTookControl() {
        if (phase != Phase.ON_GROUND && phase != Phase.LANDING) phase = Phase.MANUAL
    }

    fun tick(t: Telemetry, nowMs: Long): Command {
        if (t.altitudeM != null) lastAltitudeMs = nowMs

        if (phase == Phase.MANUAL) {
            if (landRequested) return land("aterrizaje pedido", nowMs)
            if (!t.isFlying) enter(Phase.ON_GROUND, nowMs)
            return Command.None
        }

        if (phase == Phase.ON_GROUND) {
            // Vuela pese a estar «en tierra»: la app se reinició en vuelo. ATERRIZAR debe funcionar.
            if (landRequested) {
                landRequested = false
                if (t.isFlying) return land("aterrizaje pedido", nowMs)
            }
            if (!takeoffRequested) return Command.None
            takeoffRequested = false
            lastAltitudeMs = nowMs
            enter(Phase.TAKING_OFF, nowMs)
            return Command.TakeOff
        }
        if (phase == Phase.LANDING) {
            if (landRequested) {
                landRequested = false
                if (t.isFlying) return land("aterrizaje pedido de nuevo", nowMs)
            }
            if (!t.isFlying) enter(Phase.ON_GROUND, nowMs)
            return Command.None
        }

        abortReason(t, nowMs)?.let { return land(it, nowMs) }

        return when (phase) {
            Phase.TAKING_OFF -> {
                if (t.isFlying && !t.isAutoTakeoff) enter(Phase.CLIMBING, nowMs)
                if (phase == Phase.CLIMBING) Command.HoldAltitude(targetM) else Command.None
            }
            Phase.CLIMBING -> {
                val alt = t.altitudeM
                if (alt != null && Math.abs(alt - targetM) <= toleranceM) enter(Phase.HOLDING, nowMs)
                Command.HoldAltitude(targetM)
            }
            else -> Command.HoldAltitude(targetM) // HOLDING
        }
    }

    private fun abortReason(t: Telemetry, nowMs: Long): String? = when {
        landRequested -> "aterrizaje pedido"
        phase != Phase.TAKING_OFF && !t.isFlying -> "el dron ya no vuela"
        t.altitudeM != null && t.altitudeM > maxAltitudeM -> "altura ${t.altitudeM} m > $maxAltitudeM m"
        nowMs - lastAltitudeMs > telemetryTimeoutMs -> "sin dato de altura"
        phase != Phase.HOLDING && nowMs - phaseStartMs > phaseTimeoutMs -> "tiempo agotado en $phase"
        else -> null
    }

    private fun land(reason: String, nowMs: Long): Command {
        landRequested = false
        enter(Phase.LANDING, nowMs)
        return Command.Land(reason)
    }

    private fun enter(p: Phase, nowMs: Long) {
        phase = p
        phaseStartMs = nowMs
    }
}
