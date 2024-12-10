package com.example.aufgabe3.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aufgabe3.model.BookingEntry
import com.example.aufgabe3.viewmodel.SharedViewModel
import java.time.format.DateTimeFormatter

/**
 * Repräsentiert den Home-Bildschirm der App, auf dem alle Buchungseinträge angezeigt werden.
 * Erlaubt das Hinzufügen neuer Buchungen und das Löschen bestehender Einträge.
 *
 * @param navController Der NavHostController, der für die Navigation zwischen den Bildschirmen verantwortlich ist.
 * @param sharedViewModel Das ViewModel, das die Buchungsdaten verwaltet und mit der UI kommuniziert.
 */
@Composable
fun HomeScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    // Beobachten der Buchungseinträge im ViewModel
    val bookings = sharedViewModel.bookingsEntries.collectAsState()

    // Box wird verwendet, um den gesamten Inhalt sowie den FloatingActionButton zu umschließen
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Überschrift, die "BOOKING ENTRIES" anzeigt
            Text(
                text = "BOOKING ENTRIES",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(), // Überschrift nimmt die gesamte Breite ein
                color = MaterialTheme.colorScheme.primary
            )

            // Überprüfen, ob Buchungen vorhanden sind
            if (bookings.value.isEmpty()) {
                // Text anzeigen, wenn keine Buchungen vorhanden sind
                Text("No bookings available.")
            } else {
                // LazyColumn wird verwendet, um die Buchungen in einer scrollbaren Liste darzustellen
                LazyColumn {
                    items(bookings.value) { booking ->
                        // Für jede Buchung wird das BookingEntryItem-Element erstellt
                        BookingEntryItem(
                            booking = booking,
                            onDelete = { sharedViewModel.deleteBookingEntry(booking) } // Löschen der Buchung
                        )
                    }
                }
            }
        }

        // FloatingActionButton für das Hinzufügen einer neuen Buchung
        FloatingActionButton(
            onClick = { navController.navigate("add") }, // Navigiert zum AddScreen, wenn der Button gedrückt wird
            modifier = Modifier
                .align(Alignment.BottomEnd) // Positioniert den Button rechts unten
                .padding(16.dp) // Etwas Abstand vom Rand
        ) {
            // Das Symbol für das Hinzufügen einer Buchung
            Icon(Icons.Filled.Add, contentDescription = "Add Booking", tint = Color.White)
        }
    }
}

/**
 * Repräsentiert das UI-Element für einen einzelnen Buchungseintrag in der Liste.
 * Enthält die Buchungsdetails und ermöglicht das Löschen des Eintrags.
 *
 * @param booking Der Buchungseintrag, der angezeigt werden soll.
 * @param onDelete Die Funktion, die ausgeführt wird, wenn der Benutzer auf das Löschen-Symbol klickt.
 */
@Composable
fun BookingEntryItem(
    booking: BookingEntry,
    onDelete: () -> Unit
) {
    // Card, die den Buchungseintrag umhüllt und die visuelle Darstellung aufwertet
    Card(
        modifier = Modifier
            .fillMaxWidth() // Card nimmt die gesamte Breite ein
            .padding(vertical = 8.dp) // Vertikale Abstände zwischen den Cards
            .clickable { /* Optional: Hier kann zusätzliche Logik für Klicks hinzugefügt werden */ },
        shape = RoundedCornerShape(12.dp), // Abgerundete Ecken der Card
    ) {
        // Row für die horizontale Anordnung der Buchungsdetails
        Row(
            modifier = Modifier
                .padding(16.dp) // Padding innerhalb der Row
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, // Platzierung der Elemente am linken und rechten Rand
            verticalAlignment = Alignment.CenterVertically // Zentriert die Elemente vertikal
        ) {
            // Column für die Buchungsdetails (Name, Ankunfts- und Abreisedatum)
            Column(
                modifier = Modifier.weight(1f) // Column nimmt den verbleibenden Platz ein
            ) {
                // Text für den Namen der Buchung
                Text(
                    text = "Name: ${booking.name}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp) // Padding nach unten
                )
                // Text für das Start- und Enddatum der Buchung
                Text(
                    text = "${booking.startDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} - ${booking.endDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} ",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 10.dp) // Padding nach unten
                )
            }

            // Button zum Löschen des Buchungseintrags
            IconButton(
                onClick = onDelete, // Löscht die Buchung, wenn der Button gedrückt wird
                modifier = Modifier.align(Alignment.CenterVertically) // Vertikale Zentrierung des Buttons
            ) {
                // Das Löschen-Symbol, das den Benutzer zum Löschen der Buchung auffordert
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Booking",
                    tint = MaterialTheme.colorScheme.error // Rote Farbe für das Löschen-Symbol
                )
            }
        }
    }
}
