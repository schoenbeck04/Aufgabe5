package com.example.aufgabe3.model

import java.time.LocalDate

/**
 * Ein Modell für einen Buchungseintrag.
 * Ein Buchungseintrag besteht aus einem Namen, einem Startdatum und einem Enddatum.
 *
 * @property name Der Name der Buchung.
 * @property startDate Das Startdatum der Buchung.
 * @property endDate Das Enddatum der Buchung.
 */
data class BookingEntry(
    val name: String, // Name des Buchungseintrags
    val startDate: LocalDate, // Startdatum der Buchung
    val endDate: LocalDate // Enddatum der Buchung
)
