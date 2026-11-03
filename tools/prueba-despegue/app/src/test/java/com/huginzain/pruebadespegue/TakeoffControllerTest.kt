package com.huginzain.pruebadespegue

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TakeoffControllerTest {
    private val ground = Telemetry(isFlying = false, isAutoTakeoff = false, altitudeM = 0.0)
    private val autoTakeoff = Telemetry(isFlying = true, isAutoTakeoff = true, altitudeM = 0.6)
    private fun flying(alt: Double?) = Telemetry(isFlying = true, isAutoTakeoff = false, altitudeM = alt)

    /** Lleva el controlador hasta CLIMBING en t = 200 ms. */
    private fun climbing(): TakeoffController {
        val c = TakeoffController()
        c.requestTakeoff()
        c.tick(ground, 0)
        c.tick(autoTakeoff, 100)
        c.tick(flying(1.2), 200)
        return c
    }

    @Test
    fun `sin pulsar nada no hace nada`() {
        val c = TakeoffController()
        assertEquals(Command.None, c.tick(ground, 0))
        assertEquals(Phase.ON_GROUND, c.phase)
    }

    @Test
    fun `flujo normal despega sube a 2 m y se mantiene`() {
        val c = TakeoffController()
        c.requestTakeoff()
        assertEquals(Command.TakeOff, c.tick(ground, 0))
        assertEquals(Command.None, c.tick(ground, 100)) // motores arrancando
        assertEquals(Command.None, c.tick(autoTakeoff, 2_000)) // despegue automático en curso
        assertEquals(Command.HoldAltitude(2.0), c.tick(flying(1.2), 4_000))
        assertEquals(Phase.CLIMBING, c.phase)
        assertEquals(Command.HoldAltitude(2.0), c.tick(flying(1.86), 5_000)) // justo dentro de 0,15 m
        assertEquals(Phase.HOLDING, c.phase)
        assertEquals(Command.HoldAltitude(2.0), c.tick(flying(2.0), 60_000)) // mantener no caduca
        assertEquals(Phase.HOLDING, c.phase)
    }

    @Test
    fun `justo fuera de la tolerancia sigue subiendo`() {
        val c = climbing()
        c.tick(flying(1.84), 300)
        assertEquals(Phase.CLIMBING, c.phase)
    }

    @Test
    fun `despegar solo vale en tierra`() {
        val c = climbing()
        c.requestTakeoff()
        assertEquals(Command.HoldAltitude(2.0), c.tick(flying(1.5), 300))
    }

    @Test
    fun `aterrizar a peticion y volver a tierra`() {
        val c = climbing()
        c.requestLanding()
        val cmd = c.tick(flying(1.5), 300)
        assertTrue(cmd is Command.Land)
        assertEquals(Phase.LANDING, c.phase)
        assertEquals(Command.None, c.tick(flying(0.5), 400)) // no repite la orden
        c.tick(ground, 500)
        assertEquals(Phase.ON_GROUND, c.phase)
    }

    @Test
    fun `si el aterrizaje falla se puede pedir otra vez`() {
        val c = climbing()
        c.requestLanding()
        c.tick(flying(1.5), 300)
        assertEquals(Command.None, c.tick(flying(1.5), 400)) // la orden falló: sigue volando
        c.requestLanding()
        assertTrue(c.tick(flying(1.5), 500) is Command.Land)
    }

    @Test
    fun `tras aterrizar pulsar aterrizar no repite la orden`() {
        val c = climbing()
        c.requestLanding()
        c.tick(flying(1.5), 300)
        c.requestLanding()
        assertEquals(Command.None, c.tick(ground, 400))
        assertEquals(Phase.ON_GROUND, c.phase)
        assertEquals(Command.None, c.tick(ground, 500)) // la petición no queda pendiente
    }

    @Test
    fun `app reiniciada en vuelo puede aterrizar`() {
        val c = TakeoffController() // empieza en ON_GROUND aunque el dron vuela
        c.requestLanding()
        assertTrue(c.tick(flying(2.0), 0) is Command.Land)
        assertEquals(Phase.LANDING, c.phase)
    }

    @Test
    fun `aterrizar en tierra no hace nada`() {
        val c = TakeoffController()
        c.requestLanding()
        assertEquals(Command.None, c.tick(ground, 0))
        assertEquals(Phase.ON_GROUND, c.phase)
    }

    @Test
    fun `aterrizar durante el despegue automatico`() {
        val c = TakeoffController()
        c.requestTakeoff()
        c.tick(ground, 0)
        c.requestLanding()
        assertTrue(c.tick(autoTakeoff, 100) is Command.Land)
    }

    @Test
    fun `aborta si pasa de la altura maxima`() {
        val c = climbing()
        assertEquals(Command.HoldAltitude(2.0), c.tick(flying(3.0), 300)) // 3,0 m aún vale
        val cmd = c.tick(flying(3.01), 400)
        assertTrue(cmd is Command.Land && cmd.reason.contains("altura"))
    }

    @Test
    fun `tolera huecos de telemetria de hasta 1 s`() {
        val c = climbing()
        assertEquals(Command.HoldAltitude(2.0), c.tick(flying(null), 1_200)) // 1000 ms sin dato
        val cmd = c.tick(flying(null), 1_201)
        assertTrue(cmd is Command.Land && cmd.reason.contains("sin dato"))
    }

    @Test
    fun `aborta si el despegue no termina en 15 s`() {
        val c = TakeoffController()
        c.requestTakeoff()
        c.tick(ground, 0)
        assertEquals(Command.None, c.tick(autoTakeoff, 15_000))
        assertTrue(c.tick(autoTakeoff, 15_001) is Command.Land)
    }

    @Test
    fun `aborta si no llega a 2 m en 15 s`() {
        val c = climbing()
        assertEquals(Command.HoldAltitude(2.0), c.tick(flying(1.2), 15_200))
        assertTrue(c.tick(flying(1.2), 15_201) is Command.Land)
    }

    @Test
    fun `si deja de volar mientras sube pasa por aterrizaje a tierra`() {
        val c = climbing()
        val cmd = c.tick(ground, 300)
        assertTrue(cmd is Command.Land && cmd.reason.contains("ya no vuela"))
        c.tick(ground, 400)
        assertEquals(Phase.ON_GROUND, c.phase)
    }

    @Test
    fun `si el mando toma el control la app deja de mandar`() {
        val c = climbing()
        c.pilotTookControl()
        assertEquals(Phase.MANUAL, c.phase)
        assertEquals(Command.None, c.tick(flying(5.0), 60_000)) // sin abortos: manda el piloto
        c.tick(ground, 60_100)
        assertEquals(Phase.ON_GROUND, c.phase)
    }

    @Test
    fun `en manual el boton aterrizar sigue funcionando`() {
        val c = climbing()
        c.pilotTookControl()
        c.requestLanding()
        assertTrue(c.tick(flying(1.5), 300) is Command.Land)
    }

    @Test
    fun `tomar el control en tierra no cambia nada`() {
        val c = TakeoffController()
        c.pilotTookControl()
        assertEquals(Phase.ON_GROUND, c.phase)
    }

    @Test
    fun `se puede repetir la prueba tras aterrizar`() {
        val c = climbing()
        c.requestLanding()
        c.tick(flying(1.5), 300)
        c.tick(ground, 400)
        c.requestTakeoff()
        assertEquals(Command.TakeOff, c.tick(ground, 500))
        assertEquals(Command.None, c.tick(ground, 1_400)) // el reloj de telemetría se reinicia
    }
}
