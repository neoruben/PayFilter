package com.paydedupe.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AmountExtractorTest {

    @Test
    fun `extrae importes tipicos de Google Wallet y Bancos`() {
        assertEquals(
            1250L,
            AmountExtractor.extractEuroCents("Pago en MERCADONA por 12,50 € con tarjeta *1234")
        )
        assertEquals(
            480L,
            AmountExtractor.extractEuroCents("Compra con tarjeta de 4.80 EUR en Cafeteria")
        )
        assertEquals(
            1999L,
            AmountExtractor.extractEuroCents("Google Wallet: €19,99 pagados con Mastercard")
        )
        assertEquals(
            125045L,
            AmountExtractor.extractEuroCents("Cargo de 1.250,45 € en El Corte Ingles")
        )
        assertEquals(
            2000L,
            AmountExtractor.extractEuroCents("Has pagado 20 € en Gasolinera")
        )
    }

    @Test
    fun `ignora notificaciones sin importe en euros`() {
        assertNull(
            AmountExtractor.extractEuroCents("Tu código de seguridad es 482910")
        )
        assertNull(
            AmountExtractor.extractEuroCents("Tienes un mensaje nuevo de WhatsApp")
        )
    }
}
