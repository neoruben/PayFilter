package com.neoruben.payfilter

import java.util.Locale

/**
 * Extrae y normaliza importes en euros (en céntimos) a partir del texto de las notificaciones
 * de la cartera móvil y del banco.
 */
object AmountExtractor {

    private val EURO_SUFFIX_REGEX = Regex(
        pattern = """(?<!\d)(\d{1,3}(?:[.,]\d{3})*(?:[.,]\d{1,2})?|\d+(?:[.,]\d{1,2})?)\s*(?:€|EUR\b)""",
        option = RegexOption.IGNORE_CASE
    )

    private val EURO_PREFIX_REGEX = Regex(
        pattern = """(?:€|EUR)\s*(\d{1,3}(?:[.,]\d{3})*(?:[.,]\d{1,2})?|\d+(?:[.,]\d{1,2})?)(?!\d)""",
        option = RegexOption.IGNORE_CASE
    )

    fun extractEuroCents(text: String): Long? {
        val rawNumber = EURO_SUFFIX_REGEX.find(text)?.groupValues?.get(1)
            ?: EURO_PREFIX_REGEX.find(text)?.groupValues?.get(1)
            ?: return null

        return parseNormalizedCents(rawNumber)
    }

    private fun parseNormalizedCents(raw: String): Long? {
        val cleaned = raw.trim()
        if (cleaned.isEmpty()) return null

        val lastComma = cleaned.lastIndexOf(',')
        val lastDot = cleaned.lastIndexOf('.')
        val lastSeparatorIndex = maxOf(lastComma, lastDot)

        return if (lastSeparatorIndex == -1) {
            cleaned.toLongOrNull()?.times(100L)
        } else {
            val digitsAfterSeparator = cleaned.length - lastSeparatorIndex - 1
            if (digitsAfterSeparator in 1..2) {
                val integerPart = cleaned.substring(0, lastSeparatorIndex)
                    .replace(".", "")
                    .replace(",", "")
                val fractionPart = cleaned.substring(lastSeparatorIndex + 1)
                    .padEnd(2, '0')
                val euros = integerPart.toLongOrNull() ?: return null
                val cents = fractionPart.toLongOrNull() ?: return null
                euros * 100L + cents
            } else {
                val integerOnly = cleaned.replace(".", "").replace(",", "")
                integerOnly.toLongOrNull()?.times(100L)
            }
        }
    }

    fun formatCents(cents: Long): String {
        val euros = cents / 100
        val remCents = cents % 100
        return String.format(Locale("es", "ES"), "%d,%02d €", euros, remCents)
    }
}
