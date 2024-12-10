package com.example.aufgabe3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aufgabe3.model.BookingEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel, das die Buchungseinträge verwaltet.
 * Es hält die aktuelle Liste der Buchungseinträge und ermöglicht das Hinzufügen und Löschen von Einträgen.
 *
 * @property _bookingsEntries MutableStateFlow, das den aktuellen Zustand der Buchungseinträge enthält.
 * @property bookingsEntries StateFlow, das den aktuellen Zustand der Buchungseinträge als unveränderliche Sichtbarkeit bietet.
 */
class SharedViewModel : ViewModel() {
    // Interne MutableStateFlow-Variable, die die Liste der Buchungseinträge enthält
    private val _bookingsEntries = MutableStateFlow<List<BookingEntry>>(emptyList())

    // Öffentliche StateFlow-Variable, die nur Lesezugriff auf die Buchungseinträge gewährt
    val bookingsEntries: StateFlow<List<BookingEntry>> = _bookingsEntries

    /**
     * Fügt einen neuen Buchungseintrag der Liste hinzu.
     *
     * @param entry Der neue Buchungseintrag, der zur Liste hinzugefügt werden soll.
     */
    fun addBookingEntry(entry: BookingEntry) {
        // Die Liste wird durch die Hinzufügung des neuen Eintrags aktualisiert
        _bookingsEntries.value = _bookingsEntries.value + entry
    }

    /**
     * Entfernt einen Buchungseintrag aus der Liste.
     *
     * @param entry Der Buchungseintrag, der entfernt werden soll.
     */
    fun deleteBookingEntry(entry: BookingEntry) {
        // Die Liste wird durch das Entfernen des angegebenen Eintrags aktualisiert
        _bookingsEntries.value = _bookingsEntries.value - entry
    }
}
