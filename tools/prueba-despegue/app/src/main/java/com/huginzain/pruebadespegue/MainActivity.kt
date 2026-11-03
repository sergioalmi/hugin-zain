package com.huginzain.pruebadespegue

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dji.sdk.keyvalue.key.DJIKey
import dji.sdk.keyvalue.key.FlightControllerKey
import dji.sdk.keyvalue.key.KeyTools
import dji.sdk.keyvalue.value.common.EmptyMsg
import dji.sdk.keyvalue.value.common.LocationCoordinate2D
import dji.sdk.keyvalue.value.flightcontroller.FlightControlAuthorityChangeReason
import dji.sdk.keyvalue.value.flightcontroller.FlightCoordinateSystem
import dji.sdk.keyvalue.value.flightcontroller.FlightMode
import dji.sdk.keyvalue.value.flightcontroller.RollPitchControlMode
import dji.sdk.keyvalue.value.flightcontroller.VerticalControlMode
import dji.sdk.keyvalue.value.flightcontroller.VirtualStickFlightControlParam
import dji.sdk.keyvalue.value.flightcontroller.YawControlMode
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.KeyManager
import dji.v5.manager.aircraft.simulator.InitializationSettings
import dji.v5.manager.aircraft.simulator.SimulatorManager
import dji.v5.manager.aircraft.virtualstick.VirtualStickManager
import dji.v5.manager.aircraft.virtualstick.VirtualStickState
import dji.v5.manager.aircraft.virtualstick.VirtualStickStateListener

/**
 * Pantalla de la prueba: DESPEGAR A 2 m y ATERRIZAR. Traduce las órdenes de [TakeoffController]
 * a llamadas del MSDK en un bucle a 10 Hz.
 */
class MainActivity : Activity() {

    private val controller = TakeoffController()
    private val handler = Handler(Looper.getMainLooper())
    private val keys = KeyManager.getInstance()
    private val sticks = VirtualStickManager.getInstance()

    private val altitudeKey = KeyTools.createKey(FlightControllerKey.KeyAltitude)
    private val isFlyingKey = KeyTools.createKey(FlightControllerKey.KeyIsFlying)
    private val flightModeKey = KeyTools.createKey(FlightControllerKey.KeyFlightMode)
    private val connectionKey = KeyTools.createKey(FlightControllerKey.KeyConnection)
    private val confirmNeededKey = KeyTools.createKey(FlightControllerKey.KeyIsLandingConfirmationNeeded)

    /** true cuando los joysticks virtuales y el modo avanzado están activos y se pueden enviar órdenes. */
    private var sticksReady = false
    private var enablingSticks = false
    private var lastConfirmMs = 0L
    @Volatile private var lastEvent = "" // lo escriben callbacks del MSDK desde otros hilos

    private lateinit var statusText: TextView
    private lateinit var simulatorBox: CheckBox
    private lateinit var takeoffButton: Button

    private val loop = object : Runnable {
        override fun run() {
            tick()
            handler.postDelayed(this, TICK_MS)
        }
    }

    private val stickListener = object : VirtualStickStateListener {
        override fun onVirtualStickStateUpdate(state: VirtualStickState) = runOnUiThread {
            // El mando ha recuperado el control (p. ej. botón de pausa): la app deja de mandar.
            if (sticksReady && !state.isVirtualStickEnable) {
                sticksReady = false
                controller.pilotTookControl()
                event("El mando ha tomado el control")
            }
        }

        override fun onChangeReasonUpdate(reason: FlightControlAuthorityChangeReason) {
            Log.i(PruebaApp.TAG, "Cambio de autoridad de vuelo: $reason")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(buildLayout())
        sticks.setVirtualStickStateListener(stickListener)
    }

    override fun onResume() {
        super.onResume()
        handler.post(loop)
    }

    override fun onPause() {
        handler.removeCallbacks(loop)
        // Sin bucle no hay órdenes: el MSDK deja de recibirlas y el dron se queda quieto en el aire.
        if (controller.phase != Phase.ON_GROUND) event("App en segundo plano: pulsa ATERRIZAR al volver")
        super.onPause()
    }

    override fun onDestroy() {
        sticks.removeVirtualStickStateListener(stickListener)
        super.onDestroy()
    }

    private fun tick() {
        val connected = keys.getValue(connectionKey) == true
        val telemetry = Telemetry(
            isFlying = keys.getValue(isFlyingKey) == true,
            isAutoTakeoff = keys.getValue(flightModeKey) == FlightMode.AUTO_TAKE_OFF,
            altitudeM = if (connected) keys.getValue(altitudeKey) else null,
        )
        when (val cmd = controller.tick(telemetry, SystemClock.elapsedRealtime())) {
            Command.None -> Unit
            Command.TakeOff -> action(KeyTools.createKey(FlightControllerKey.KeyStartTakeoff), "Despegue")
            is Command.HoldAltitude -> holdAltitude(cmd.meters)
            is Command.Land -> land(cmd.reason)
        }
        if (controller.phase == Phase.LANDING) confirmLandingIfAsked()
        render(connected, telemetry)
    }

    private fun holdAltitude(meters: Double) {
        if (sticksReady) {
            sticks.sendVirtualStickAdvancedParam(VirtualStickFlightControlParam().apply {
                rollPitchCoordinateSystem = FlightCoordinateSystem.GROUND
                rollPitchControlMode = RollPitchControlMode.VELOCITY
                roll = 0.0
                pitch = 0.0
                yawControlMode = YawControlMode.ANGULAR_VELOCITY
                yaw = 0.0
                verticalControlMode = VerticalControlMode.POSITION // altura sobre el punto de despegue
                verticalThrottle = meters
            })
            return
        }
        if (enablingSticks) return
        // Se activan después del despegue automático: el Mini 3 Pro los pierde al despegar.
        enablingSticks = true
        // El estado se toca solo en el hilo principal, el mismo del bucle de 10 Hz.
        sticks.enableVirtualStick(object : CommonCallbacks.CompletionCallback {
            override fun onSuccess() = runOnUiThread {
                enablingSticks = false
                if (controller.phase != Phase.CLIMBING && controller.phase != Phase.HOLDING) {
                    // Llegó tarde: ya se pidió aterrizar o el mando tomó el control.
                    sticks.disableVirtualStick(logCallback("Desactivar joysticks"))
                    return@runOnUiThread
                }
                sticks.setVirtualStickAdvancedModeEnabled(true)
                sticksReady = true
                event("Joysticks virtuales activos")
            }

            override fun onFailure(error: IDJIError) = runOnUiThread {
                enablingSticks = false
                event("No se pudieron activar los joysticks: ${error.description()}")
                controller.requestLanding()
            }
        })
    }

    private fun land(reason: String) {
        event("Aterrizando ($reason)")
        if (sticksReady) {
            sticksReady = false
            sticks.setVirtualStickAdvancedModeEnabled(false)
            sticks.disableVirtualStick(logCallback("Desactivar joysticks"))
        }
        action(KeyTools.createKey(FlightControllerKey.KeyStartAutoLanding), "Aterrizaje")
    }

    /** El Mini 3 Pro se para a ~0,5 m del suelo y pide confirmación; la damos una vez por segundo. */
    private fun confirmLandingIfAsked() {
        val now = SystemClock.elapsedRealtime()
        if (keys.getValue(confirmNeededKey) == true && now - lastConfirmMs > 1_000) {
            lastConfirmMs = now
            action(KeyTools.createKey(FlightControllerKey.KeyConfirmLanding), "Confirmar aterrizaje")
        }
    }

    private fun setSimulator(enabled: Boolean) {
        val sim = SimulatorManager.getInstance()
        if (enabled == sim.isSimulatorEnabled) return
        if (enabled) {
            // shortcut: posición fija del simulador (Bilbao), da igual para subir en vertical; leerla de config si se usa para rutas.
            val settings = InitializationSettings.createInstance(LocationCoordinate2D(43.2630, -2.9350), 15)
            sim.enableSimulator(settings, logCallback("Simulador activado"))
        } else {
            sim.disableSimulator(logCallback("Simulador desactivado"))
        }
    }

    private fun action(key: DJIKey.ActionKey<EmptyMsg, EmptyMsg>, name: String) {
        keys.performAction(key, object : CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
            override fun onSuccess(result: EmptyMsg?) = event("$name: OK")
            override fun onFailure(error: IDJIError) {
                event("$name falló: ${error.description()}")
                // Si el despegue no arranca, el controlador lo aborta por tiempo; si falla el
                // aterrizaje, el piloto debe aterrizar con el mando.
            }
        })
    }

    private fun logCallback(name: String) = object : CommonCallbacks.CompletionCallback {
        override fun onSuccess() = event("$name: OK")
        override fun onFailure(error: IDJIError) = event("$name falló: ${error.description()}")
    }

    private fun event(msg: String) {
        Log.i(PruebaApp.TAG, msg)
        lastEvent = msg
    }

    private fun render(connected: Boolean, t: Telemetry) {
        val alt = t.altitudeM?.let { "%.2f m".format(it) } ?: "sin dato"
        statusText.text = """
            MSDK: ${PruebaApp.sdkStatus}
            Dron: ${if (connected) "conectado" else "NO conectado"} · Simulador: ${if (SimulatorManager.getInstance().isSimulatorEnabled) "SÍ" else "no"}
            Fase: ${controller.phase} · Altura: $alt · Volando: ${if (t.isFlying) "sí" else "no"}
            Último evento: $lastEvent
        """.trimIndent()
        val onGround = controller.phase == Phase.ON_GROUND
        takeoffButton.isEnabled = onGround && connected && !t.isFlying
        simulatorBox.isEnabled = onGround && connected && !t.isFlying
        takeoffButton.alpha = if (takeoffButton.isEnabled) 1f else 0.4f // desactivado debe verse distinto
    }

    private fun buildLayout(): LinearLayout {
        val pad = dp(16)
        statusText = TextView(this).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(Color.WHITE)
        }
        simulatorBox = CheckBox(this).apply {
            text = "Modo simulador (motores parados)"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(Color.WHITE)
            minHeight = dp(48)
            setOnCheckedChangeListener { _, checked -> setSimulator(checked) }
        }
        takeoffButton = bigButton("DESPEGAR A 2 m", Color.rgb(0, 90, 160)) { controller.requestTakeoff() }
        val landButton = bigButton("ATERRIZAR", Color.rgb(180, 0, 0)) { controller.requestLanding() }
        val buttons = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(takeoffButton, LinearLayout.LayoutParams(0, dp(96), 1f).apply { marginEnd = pad })
            addView(landButton, LinearLayout.LayoutParams(0, dp(96), 1f))
        }
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(20, 20, 20))
            // Desde Android 15 la app ocupa toda la pantalla: sin esto las barras tapan los botones.
            ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
                val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
                v.setPadding(pad + bars.left, pad + bars.top, pad + bars.right, pad + bars.bottom)
                insets
            }
            addView(statusText, LinearLayout.LayoutParams(MATCH_PARENT, 0, 1f))
            addView(simulatorBox, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            addView(buttons, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }
    }

    private fun bigButton(label: String, color: Int, onClick: () -> Unit) = Button(this).apply {
        text = label
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        setTextColor(Color.WHITE)
        setBackgroundColor(color)
        setOnClickListener { onClick() }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private companion object {
        const val TICK_MS = 100L // 10 Hz; DJI recomienda enviar entre 5 y 25 Hz
    }
}
