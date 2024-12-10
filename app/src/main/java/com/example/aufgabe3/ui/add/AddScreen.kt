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
    var showCalendar by remember { mutableStateOf(false) }
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCalendar = true }) {
                Text("+", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
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
                    .clickable { showCalendar = true },
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save")
            }
        }
    }

    if (showCalendar) {
        CalendarRangePickerDialog(
            onDateRangeSelected = { start, end ->
                startDate = start
                endDate = end
                showCalendar = false
            },
            onDismissRequest = { showCalendar = false }
        )
    }
}

@Composable
fun CalendarRangePickerDialog(
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) } // State für aktuellen Monat

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Date Range") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Previous Month"
                        )
                    }

                    Text(
                        text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next Month"
                        )
                    }
                }

                CalendarView(
                    currentMonth = currentMonth,
                    startDate = startDate,
                    endDate = endDate,
                    onDayClick = { selectedDate ->
                        if (startDate == null || (endDate != null && selectedDate < startDate)) {
                            startDate = selectedDate
                            endDate = null
                        } else if (startDate != null && endDate == null) {
                            if (selectedDate >= startDate!!) {
                                endDate = selectedDate
                            } else {
                                startDate = selectedDate
                            }
                        } else {
                            startDate = selectedDate
                            endDate = null
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Selected Range: ${startDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "None"} to ${endDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "None"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (startDate != null && endDate != null) {
                        onDateRangeSelected(startDate!!, endDate!!)
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
fun CalendarView(
    currentMonth: YearMonth,
    startDate: LocalDate?,
    endDate: LocalDate?,
    onDayClick: (LocalDate) -> Unit
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val startDay = LocalDate.of(currentMonth.year, currentMonth.month, 1)
    val firstDayOfWeek = startDay.dayOfWeek.value % 7 // 0 = Sonntag, 1 = Montag usw.

    Column {
        Spacer(modifier = Modifier.height(8.dp))

        // Woche-Header
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Tage-Anzeige
        (0 until firstDayOfWeek + daysInMonth).chunked(7).forEach { week ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                week.forEach { day ->
                    val date = if (day >= firstDayOfWeek) startDay.plusDays((day - firstDayOfWeek).toLong()) else null
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(enabled = date != null) { date?.let { onDayClick(it) } }
                            .background(
                                when {
                                    date == null -> Color.Transparent
                                    date == startDate || date == endDate -> Color.Blue
                                    startDate != null && endDate != null && date in startDate..endDate -> Color.LightGray
                                    else -> Color.Transparent
                                },
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = if (date == startDate || date == endDate) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
