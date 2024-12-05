package com.example.aufgabe3.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("add") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Booking")
        }
    }
}

@Composable
fun BookingEntryItem(
    booking: BookingEntry,
    onDelete: () -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text("Name: ${booking.name}")
        Text("Arrival: ${booking.arrivalDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}")
        Text("Departure: ${booking.departureDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}")
        Button(onClick = onDelete) {
            Text("Delete")
        }
    }
}

