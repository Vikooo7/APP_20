package com.example.app19_jc_neon_bd_remoto

import com.example.app19_jc_neon_bd_remoto.view.calcularEdad
import com.example.app19_jc_neon_bd_remoto.view.formatearFecha
import com.example.app19_jc_neon_bd_remoto.view.isoAMillis
import com.example.app19_jc_neon_bd_remoto.view.millisAIso
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar

class FormatoFechaTest {

    @Test
    fun la_fecha_iso_se_muestra_como_dia_mes_anio() {
        assertEquals("22/07/2003", formatearFecha("2003-07-22"))
        assertEquals("", formatearFecha(""))
    }

    @Test
    fun ida_y_vuelta_con_el_datepicker_no_corre_el_dia() {
        val millis = isoAMillis("2003-07-22")!!
        assertEquals("2003-07-22", millisAIso(millis))
    }

    @Test
    fun la_edad_cuenta_solo_anios_cumplidos() {
        val hoy = Calendar.getInstance()
        val anio = hoy.get(Calendar.YEAR)
        val mes = hoy.get(Calendar.MONTH) + 1
        val dia = hoy.get(Calendar.DAY_OF_MONTH)
        val hoyIso = "%04d-%02d-%02d"

        // Cumple años hoy: ya los tiene
        assertEquals(20, calcularEdad(hoyIso.format(anio - 20, mes, dia)))
        // Nació hoy: 0 años
        assertEquals(0, calcularEdad(hoyIso.format(anio, mes, dia)))
        assertNull(calcularEdad("no-es-fecha"))
    }
}
