package com.example.aufgabe3.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aufgabe3.model.BookingEntry
import com.example.aufgabe3.viewmodel.SharedViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Composable-Funktion, die den Bildschirm zum Hinzufügen eines neuen Buchungseintrags darstellt.
 * Auf diesem Bildschirm kann der Benutzer den Namen der Buchung eingeben und einen Datumsbereich auswählen.
 * Nachdem die Eingaben validiert wurden, wird der Buchungseintrag im SharedViewModel gespeichert.
 *
 * @param navController Der Navigation-Controller, um zwischen den Bildschirmen zu navigieren.
 * @param sharedViewModel Das ViewModel, das die Buchungseinträge verwaltet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    // Zustände für Benutzereingaben
    var name by remember { mutableStateOf("") } // Name des Buchungseintrags
    var startDate by remember { mutableStateOf<LocalDate?>(null) } // Startdatum der Buchung
    var endDate by remember { mutableStateOf<LocalDate?>(null) } // Enddatum der Buchung
    var showDatePicker by remember { mutableStateOf(false) } // Zeigt den DatePicker an oder nicht
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy") // Formatierung des Datums

    // Scaffold, das die grundlegende Layoutstruktur des Bildschirms bereitstellt
    Scaffold(
        topBar = {
            // TopAppBar mit Zurück-Schaltfläche und Titel
            TopAppBar(
                title = { Text("Buchung hinzufügen") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Hauptinhalt des Bildschirms in einer Column
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Eingabefeld für den Namen der Buchung
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Eingabefeld für den Datumsbereich
            OutlinedTextField(
                value = if (startDate != null && endDate != null) {
                    "${startDate!!.format(dateFormatter)} - ${endDate!!.format(dateFormatter)}"
                } else {
                    ""
                },
                onValueChange = {},
                label = { Text("Datumsbereich auswählen") },
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }, // DatePicker anzeigen, wenn darauf geklickt wird
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Schaltfläche zum Speichern der Buchung
            Button(
                onClick = {
                    // Sicherstellen, dass alle Felder ausgefüllt sind, bevor gespeichert wird
                    if (name.isNotBlank() && startDate != null && endDate != null) {
                        // Neuen Buchungseintrag dem ViewModel hinzufügen
                        sharedViewModel.addBookingEntry(
                            BookingEntry(name, startDate!!, endDate!!)
                        )
                        // Zurück zur vorherigen Seite navigieren
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Speichern")
            }
        }
    }

    // Wenn der DatePicker angezeigt werden soll, wird das DateRangePickerModal geöffnet
    if (showDatePicker) {
        DateRangePickerModal(
            onDateRangeSelected = { start, end ->
                // Wenn ein Datumsbereich ausgewählt wurde, die Werte speichern
                startDate = start
                endDate = end
                showDatePicker = false // DatePicker schließen
            },
            onDismissRequest = { showDatePicker = false } // DatePicker schließen, wenn abgebrochen
        )
    }
}

/**
 * Modal-Bottom-Sheet zum Auswählen eines Datumsbereichs.
 *
 * In diesem Modal können die Benutzer einen Start- und Endtermin auswählen.
 *
 * @param onDateRangeSelected Callback, der den ausgewählten Datumsbereich zurückgibt.
 * @param onDismissRequest Callback, um das Modal zu schließen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    val state = rememberDateRangePickerState() // Zustand für den DatePicker

    // ModalBottomSheet für den DatePicker
    ModalBottomSheet(
        onDismissRequest = onDismissRequest, // Wenn das Modal abgebrochen wird, wird es geschlossen
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Datumsbereich auswählen", // Überschrift des Modals
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Box, die den DatePicker enthält
            Box(
                modifier = Modifier
                    .height(400.dp) // Höhe des DatePickers
                    .width(350.dp) // Breite des DatePickers
                    .background(MaterialTheme.colorScheme.background)
            ) {
                DateRangePicker(
                    state = state, // Zustand des DatePickers
                    title = { Text("Datumsbereich wählen", fontSize = 16.sp) }, // Titel des Pickers
                    headline = { Text("Start- und Enddatum auswählen", fontSize = 14.sp) } // Überschrift des Pickers
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Schaltfläche zum Speichern des ausgewählten Datumsbereichs
            Button(
                onClick = {
                    val startMillis = state.selectedStartDateMillis
                    val endMillis = state.selectedEndDateMillis

                    // Wenn beide Daten ausgewählt wurden, werden sie in LocalDate umgewandelt
                    if (startMillis != null && endMillis != null) {
                        val selectedStartDate = LocalDate.ofEpochDay(startMillis / (24 * 60 * 60 * 1000))
                        val selectedEndDate = LocalDate.ofEpochDay(endMillis / (24 * 60 * 60 * 1000))
                        onDateRangeSelected(selectedStartDate, selectedEndDate) // Rückgabe des Datumsbereichs
                    }
                },
                enabled = state.selectedStartDateMillis != null && state.selectedEndDateMillis != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Datum speichern", color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Schaltfläche zum Abbrechen des Datumsbereichs-Auswahlprozesses
            TextButton(onClick = onDismissRequest) {
                Text("Abbrechen", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
