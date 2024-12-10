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

@Composable
fun HomeScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    val bookings = sharedViewModel.bookingsEntries.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) { // Box als Container für den Inhalt und den FloatingActionButton
        Column(modifier = Modifier.padding(16.dp)) {
            // Überschrift hinzufügen
            Text(
                text = "BOOKING ENTRIES",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(), // Die Überschrift nimmt die ganze Breite ein
                color = MaterialTheme.colorScheme.primary
            )

            // Prüfen, ob Buchungen vorhanden sind
            if (bookings.value.isEmpty()) {
                Text("No bookings available.")
            } else {
                LazyColumn {
                    items(bookings.value) { booking ->
                        BookingEntryItem(
                            booking = booking,
                            onDelete = { sharedViewModel.deleteBookingEntry(booking) }
                        )
                    }
                }
            }
        }

        // FloatingActionButton am unteren rechten Rand
        FloatingActionButton(
            onClick = { navController.navigate("add") },
            modifier = Modifier
                .align(Alignment.BottomEnd) // Positioniert den Button rechts unten
                .padding(16.dp) // Etwas Abstand vom Rand
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Booking", tint = Color.White)
        }
    }
}


@Composable
fun BookingEntryItem(
    booking: BookingEntry,
    onDelete: () -> Unit
) {
    // Card to wrap the booking entry
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* Optional: Add onClick logic here if needed */ },
        shape = RoundedCornerShape(12.dp),
    ) {
        // Row for horizontal layout of details
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Column for the booking details (name, arrival, departure)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Name: ${booking.name}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Arrival: ${booking.arrivalDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = "Departure: ${booking.departureDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Button to delete the booking entry
            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Booking",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
