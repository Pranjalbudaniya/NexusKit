package com.nexuskit.app.feature.tools.date_calculator

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateCalcScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: DateCalcViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }

    var showDatePickerFor by remember { mutableStateOf<String?>(null) }

    DrawerNavigation(
        navController = navController,
        currentToolId = "date_calculator"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Age & Date Calculator",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                // Mode Tabs
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                    ) {
                        DateToolTab.entries.forEach { tab ->
                            val label = when (tab) {
                                DateToolTab.AGE -> "Age Calculator"
                                DateToolTab.DIFFERENCE -> "Date Difference"
                                DateToolTab.ADD_SUBTRACT -> "Add / Subtract"
                            }
                            FilterChip(
                                selected = uiState.selectedTab == tab,
                                onClick = { viewModel.onTabSelected(tab) },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }

                when (uiState.selectedTab) {
                    DateToolTab.AGE -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(spacing.md),
                                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                                ) {
                                    Text("Select Date of Birth", style = MaterialTheme.typography.titleSmall)
                                    OutlinedButton(
                                        onClick = { showDatePickerFor = "birth" },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = uiState.birthDate.format(dateFormatter),
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }

                        uiState.ageResult?.let { res ->
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.large,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(spacing.lg),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Your Exact Age",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                        Text(
                                            text = "${res.years} Years",
                                            style = MaterialTheme.typography.displayMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 44.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "${res.months} months | ${res.days} days",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )

                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                                            modifier = Modifier.padding(vertical = spacing.md)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceAround
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("Next Birthday", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                                Text("${res.daysUntilNextBirthday} days", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimaryContainer)
                                                Text(res.nextBirthdayDayOfWeek, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                            }
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("Total Days", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                                Text("%,d".format(res.totalDays), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimaryContainer)
                                                Text("%,d weeks".format(res.totalWeeks), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    DateToolTab.DIFFERENCE -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(spacing.md),
                                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                                ) {
                                    Text("Start Date", style = MaterialTheme.typography.titleSmall)
                                    OutlinedButton(onClick = { showDatePickerFor = "start" }, modifier = Modifier.fillMaxWidth()) {
                                        Text(uiState.startDate.format(dateFormatter), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    }

                                    Spacer(Modifier.height(spacing.xs))

                                    Text("End Date", style = MaterialTheme.typography.titleSmall)
                                    OutlinedButton(onClick = { showDatePickerFor = "end" }, modifier = Modifier.fillMaxWidth()) {
                                        Text(uiState.endDate.format(dateFormatter), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }
                        }

                        uiState.differenceResult?.let { diff ->
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.large,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(spacing.lg),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Difference Between Dates", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                        Text(
                                            text = "${diff.years}y ${diff.months}m ${diff.days}d",
                                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(Modifier.height(spacing.sm))
                                        Text("Total: %,d Days (%,d Hours)".format(diff.totalDays, diff.totalHours), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f))
                                    }
                                }
                            }
                        }
                    }

                    DateToolTab.ADD_SUBTRACT -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(spacing.md),
                                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                                ) {
                                    Text("Starting Date", style = MaterialTheme.typography.titleSmall)
                                    OutlinedButton(onClick = { showDatePickerFor = "base" }, modifier = Modifier.fillMaxWidth()) {
                                        Text(uiState.baseDate.format(dateFormatter), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                                    ) {
                                        FilterChip(
                                            selected = uiState.isAdd,
                                            onClick = { viewModel.onDeltasChanged(uiState.yearsDelta, uiState.monthsDelta, uiState.daysDelta, true) },
                                            label = { Text("Add (+)") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        FilterChip(
                                            selected = !uiState.isAdd,
                                            onClick = { viewModel.onDeltasChanged(uiState.yearsDelta, uiState.monthsDelta, uiState.daysDelta, false) },
                                            label = { Text("Subtract (-)") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Text("Years: ${uiState.yearsDelta}")
                                    Slider(value = uiState.yearsDelta.toFloat(), onValueChange = { viewModel.onDeltasChanged(it.toLong(), uiState.monthsDelta, uiState.daysDelta, uiState.isAdd) }, valueRange = 0f..20f)

                                    Text("Months: ${uiState.monthsDelta}")
                                    Slider(value = uiState.monthsDelta.toFloat(), onValueChange = { viewModel.onDeltasChanged(uiState.yearsDelta, it.toLong(), uiState.daysDelta, uiState.isAdd) }, valueRange = 0f..24f)

                                    Text("Days: ${uiState.daysDelta}")
                                    Slider(value = uiState.daysDelta.toFloat(), onValueChange = { viewModel.onDeltasChanged(uiState.yearsDelta, uiState.monthsDelta, it.toLong(), uiState.isAdd) }, valueRange = 0f..60f)
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(spacing.lg),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Calculated Result Date", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                    Text(
                                        text = uiState.calculatedDate.format(dateFormatter),
                                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = uiState.calculatedDate.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Date Picker Dialog
        if (showDatePickerFor != null) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = System.currentTimeMillis()
            )

            DatePickerDialog(
                onDismissRequest = { showDatePickerFor = null },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            when (showDatePickerFor) {
                                "birth" -> viewModel.onBirthDateChanged(localDate)
                                "start" -> viewModel.onStartDateChanged(localDate)
                                "end" -> viewModel.onEndDateChanged(localDate)
                                "base" -> viewModel.onBaseDateChanged(localDate)
                            }
                        }
                        showDatePickerFor = null
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePickerFor = null }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
