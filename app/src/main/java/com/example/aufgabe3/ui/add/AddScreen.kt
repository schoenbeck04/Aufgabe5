package com.example.aufgabe3.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aufgabe3.model.BookingEntry
import com.example.aufgabe3.viewmodel.SharedViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.yearMonth
import java.time.YearMonth

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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Button(
                onClick = {
                    if (name.isNotBlank() && startDate != null && endDate != null) {
                        sharedViewModel.addBookingEntry(
                            BookingEntry(name, startDate!!, endDate!!)
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }

    if (showDatePicker) {
        DateRangePickerDialog(
            onDateRangeSelected = { start, end ->
                startDate = start
                endDate = end
                showDatePicker = false
            },
            onDismissRequest = {
                showDatePicker = false
            }
        )
    }
}

@Composable
fun DateRangePickerDialog(
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    val currentDate = remember { LocalDate.now() }
    val currentMonth = remember { YearMonth.now() }
    val startDateBoundary = remember { currentMonth.minusMonths(100).atStartOfMonth() }
    val endDateBoundary = remember { currentMonth.plusMonths(100).atEndOfMonth() }
    val state = rememberWeekCalendarState(
        startDate = startDateBoundary,
        endDate = endDateBoundary,
        firstVisibleWeekDate = currentDate,
        firstDayOfWeek = java.time.DayOfWeek.MONDAY
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Date Range") },
        text = {
            Column {
                WeekCalendar(
                    state = state,
                    dayContent = { day ->
                        Day(day, startDate, endDate) { clickedDate ->
                            when {
                                startDate == null -> startDate = clickedDate
                                endDate == null && clickedDate > startDate -> endDate = clickedDate
                                else -> {
                                    startDate = clickedDate
                                    endDate = null
                                }
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Selected Range: ${startDate?.toString() ?: "None"} to ${endDate?.toString() ?: "None"}")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    startDate?.let { start ->
                        endDate?.let { end ->
                            onDateRangeSelected(start, end)
                        }
                    }
                },
                enabled = startDate != null && endDate != null
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun Day(day: WeekDay, startDate: LocalDate?, endDate: LocalDate?, onClick: (LocalDate) -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable { onClick(day.date) }
            .background(
                when {
                    day.date == startDate || day.date == endDate -> Color.Blue
                    startDate != null && endDate != null && day.date > startDate && day.date < endDate -> Color.Red
                    else -> Color.Transparent
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = if (day.date == startDate || day.date == endDate) Color.White else Color.Black
        )
    }
}