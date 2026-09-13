package com.nexuskit.app.feature.tools.health_fitness

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    initialTab: FitnessTab = FitnessTab.WATER,
    viewModel: FitnessViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current

    LaunchedEffect(initialTab) {
        viewModel.onTabSelected(initialTab)
    }

    DrawerNavigation(
        navController = navController,
        currentToolId = "health_fitness"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Health & Fitness",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // ── Tab selector ─────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.md, vertical = spacing.xs)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                ) {
                    val tabs = listOf(
                        FitnessTab.WATER to "Water Intake",
                        FitnessTab.MACROS to "Daily Macros",
                        FitnessTab.IDEAL_WEIGHT to "Ideal Weight",
                        FitnessTab.ONE_REP_MAX to "1-Rep Max",
                        FitnessTab.HEART_RATE to "HR Zones",
                        FitnessTab.SLEEP_CYCLES to "Sleep Cycles",
                        FitnessTab.PACE to "Pace Calculator"
                    )
                    tabs.forEach { (tab, label) ->
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

                when (uiState.selectedTab) {
                    FitnessTab.WATER -> {
                        WaterIntakeView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    FitnessTab.MACROS -> {
                        MacrosView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    FitnessTab.IDEAL_WEIGHT -> {
                        IdealWeightView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    FitnessTab.ONE_REP_MAX -> {
                        OneRepMaxView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    FitnessTab.HEART_RATE -> {
                        HeartRateView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    FitnessTab.SLEEP_CYCLES -> {
                        SleepCyclesView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    FitnessTab.PACE -> {
                        PaceView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    else -> {
                        WaterIntakeView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                }
            }
        }
    }
}

@Composable
private fun WaterIntakeView(uiState: FitnessUiState, viewModel: FitnessViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("Daily Water Intake Calculator", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    OutlinedTextField(
                        value = uiState.waterWeightKg,
                        onValueChange = { viewModel.onWaterInputsChanged(it, uiState.waterActivityHours) },
                        label = { Text("Body Weight (kg)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = uiState.waterActivityHours,
                        onValueChange = { viewModel.onWaterInputsChanged(uiState.waterWeightKg, it) },
                        label = { Text("Daily Exercise / Activity (Hours)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text("Recommended Daily Water", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = String.format(java.util.Locale.US, "%.2f Liters", uiState.waterIntakeLiters),
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Text("≈ ${(uiState.waterIntakeLiters * 4.2).toInt()} standard glasses (250ml each)", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun MacrosView(uiState: FitnessUiState, viewModel: FitnessViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("Daily Macronutrient Targets", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        OutlinedTextField(
                            value = uiState.macroWeightKg,
                            onValueChange = { viewModel.onMacroInputsChanged(it, uiState.macroHeightCm, uiState.macroAge, uiState.macroIsMale, uiState.macroGoal) },
                            label = { Text("Weight (kg)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = uiState.macroHeightCm,
                            onValueChange = { viewModel.onMacroInputsChanged(uiState.macroWeightKg, it, uiState.macroAge, uiState.macroIsMale, uiState.macroGoal) },
                            label = { Text("Height (cm)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                        listOf("maintain" to "Maintain", "cut" to "Fat Loss (Cut)", "bulk" to "Muscle Gain (Bulk)").forEach { (g, label) ->
                            FilterChip(
                                selected = uiState.macroGoal == g,
                                onClick = { viewModel.onMacroInputsChanged(uiState.macroWeightKg, uiState.macroHeightCm, uiState.macroAge, uiState.macroIsMale, g) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("Daily Target: ${uiState.macroResult.dailyCalories} kcal", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${uiState.macroResult.proteinGrams}g", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            Text("Protein", style = MaterialTheme.typography.labelMedium)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${uiState.macroResult.carbsGrams}g", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            Text("Carbs", style = MaterialTheme.typography.labelMedium)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${uiState.macroResult.fatGrams}g", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                            Text("Fats", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IdealWeightView(uiState: FitnessUiState, viewModel: FitnessViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("Ideal Body Weight (Clinical Formulas)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    OutlinedTextField(
                        value = uiState.idealHeightCm,
                        onValueChange = { viewModel.onIdealWeightInputsChanged(it, uiState.idealIsMale) },
                        label = { Text("Height (cm)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                        FilterChip(
                            selected = uiState.idealIsMale,
                            onClick = { viewModel.onIdealWeightInputsChanged(uiState.idealHeightCm, true) },
                            label = { Text("Male") }
                        )
                        FilterChip(
                            selected = !uiState.idealIsMale,
                            onClick = { viewModel.onIdealWeightInputsChanged(uiState.idealHeightCm, false) },
                            label = { Text("Female") }
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    uiState.idealWeightResults.forEach { (formula, kg) ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(formula, style = MaterialTheme.typography.bodyMedium)
                            Text(String.format(java.util.Locale.US, "%.1f kg (%.1f lbs)", kg, kg * 2.20462), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun OneRepMaxView(uiState: FitnessUiState, viewModel: FitnessViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("1-Rep Max Calculator (Epley)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        OutlinedTextField(
                            value = uiState.liftWeightKg,
                            onValueChange = { viewModel.onOneRepMaxInputsChanged(it, uiState.liftReps) },
                            label = { Text("Lifted Weight (kg)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = uiState.liftReps,
                            onValueChange = { viewModel.onOneRepMaxInputsChanged(uiState.liftWeightKg, it) },
                            label = { Text("Reps Completed") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text("Estimated 1-Rep Max", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = String.format(java.util.Locale.US, "%.1f kg", uiState.estimated1Rm),
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Text("≈ ${(uiState.estimated1Rm * 2.20462).toInt()} lbs", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun HeartRateView(uiState: FitnessUiState, viewModel: FitnessViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("Target Heart Rate Training Zones", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    OutlinedTextField(
                        value = uiState.hrAge,
                        onValueChange = viewModel::onHrAgeChanged,
                        label = { Text("Your Age") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("Max Heart Rate: ${uiState.hrZones.maxHr} BPM", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    ZoneRow("Zone 1: Active Recovery (50-60%)", "${uiState.hrZones.zone1Recovery.first} - ${uiState.hrZones.zone1Recovery.last} BPM")
                    ZoneRow("Zone 2: Aerobic Endurance (60-70%)", "${uiState.hrZones.zone2Aerobic.first} - ${uiState.hrZones.zone2Aerobic.last} BPM")
                    ZoneRow("Zone 3: Tempo / Stamina (70-80%)", "${uiState.hrZones.zone3Tempo.first} - ${uiState.hrZones.zone3Tempo.last} BPM")
                    ZoneRow("Zone 4: Threshold (80-90%)", "${uiState.hrZones.zone4Threshold.first} - ${uiState.hrZones.zone4Threshold.last} BPM")
                    ZoneRow("Zone 5: Anaerobic Max (90-100%)", "${uiState.hrZones.zone5Anaerobic.first} - ${uiState.hrZones.zone5Anaerobic.last} BPM")
                }
            }
        }
    }
}

@Composable
private fun ZoneRow(label: String, bpm: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Text(bpm, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
    }
}

@Composable
private fun SleepCyclesView(uiState: FitnessUiState, viewModel: FitnessViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("90-Minute Sleep Cycle Calculator", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Waking up in the middle of a sleep cycle causes grogginess. Target one of these bedtimes to wake up refreshed at 07:00 AM.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text("Recommended Bedtimes (to wake at 07:00 AM)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    uiState.bedTimes.forEach { time ->
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                            Text(time, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary), modifier = Modifier.padding(10.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaceView(uiState: FitnessUiState, viewModel: FitnessViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("Running & Cycling Pace Calculator", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        OutlinedTextField(
                            value = uiState.paceDistanceKm,
                            onValueChange = { viewModel.onPaceInputsChanged(it, uiState.paceTimeMin) },
                            label = { Text("Distance (km)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = uiState.paceTimeMin,
                            onValueChange = { viewModel.onPaceInputsChanged(uiState.paceDistanceKm, it) },
                            label = { Text("Time (Minutes)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text("Calculated Performance", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("Average Pace: ${uiState.calculatedPace}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Average Speed: ${uiState.calculatedSpeed}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
