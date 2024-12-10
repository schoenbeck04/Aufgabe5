package com.example.aufgabe3.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aufgabe3.model.BookingEntry
import com.example.aufgabe3.viewmodel.SharedViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    var name by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Booking Entry") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Input for the name of the booking
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date range picker text field
            OutlinedTextField(
                value = if (startDate != null && endDate != null) {
                    "${startDate!!.format(dateFormatter)} - ${endDate!!.format(dateFormatter)}"
                } else {
                    ""
                },
                onValueChange = {},
                label = { Text("Select Date Range") },
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save button to save the booking entry
            Button(
                onClick = {
                    if (name.isNotBlank() && startDate != null && endDate != null) {
                        sharedViewModel.addBookingEntry(
                            BookingEntry(name, startDate!!, endDate!!)
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save")
            }
        }
    }

    // Show the Date Range Picker Modal when triggered
    if (showDatePicker) {
        DateRangePickerModal(
            onDateRangeSelected = { start, end ->
                startDate = start
                endDate = end
                showDatePicker = false
            },
            onDismissRequest = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    val state = rememberDateRangePickerState()
    var selectedStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEndDate by remember { mutableStateOf<LocalDate?>(null) }

    // Display the Modal Bottom Sheet
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
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
                text = "Select Date Range",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DateRangePicker(
                state = state,
                title = { Text("Pick a Date Range") },
                headline = { Text("Choose Start and End Dates, when you finished press the pencil") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel", color = Color.White)
                }

                Button(
                    onClick = {
                        val startMillis = state.selectedStartDateMillis
                        val endMillis = state.selectedEndDateMillis

                        if (startMillis != null && endMillis != null) {
                            selectedStartDate = LocalDate.ofEpochDay(startMillis / (24 * 60 * 60 * 1000))
                            selectedEndDate = LocalDate.ofEpochDay(endMillis / (24 * 60 * 60 * 1000))
                            onDateRangeSelected(selectedStartDate!!, selectedEndDate!!)
                        }
                    },
                    enabled = state.selectedStartDateMillis != null && state.selectedEndDateMillis != null,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)) // Button in Türkis
                ) {
                    Text("Save", color = Color.Black)
                }
            }
        }
    }
}




