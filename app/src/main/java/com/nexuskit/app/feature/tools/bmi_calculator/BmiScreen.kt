package com.nexuskit.app.feature.tools.bmi_calculator

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun BmiScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: BmiViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current

    DrawerNavigation(
        navController = navController,
        currentToolId = "bmi_calculator"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "BMI & Health Calculator",
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
                // Unit system selector
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        FilterChip(
                            selected = uiState.unitSystem == UnitSystem.METRIC,
                            onClick = { viewModel.onUnitSystemChanged(UnitSystem.METRIC) },
                            label = { Text("Metric (kg / cm)") },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        FilterChip(
                            selected = uiState.unitSystem == UnitSystem.IMPERIAL,
                            onClick = { viewModel.onUnitSystemChanged(UnitSystem.IMPERIAL) },
                            label = { Text("Imperial (lbs / ft)") },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                // Inputs Card
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
                            verticalArrangement = Arrangement.spacedBy(spacing.md)
                        ) {
                            // Gender and Age Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                                    FilterChip(
                                        selected = uiState.gender == Gender.MALE,
                                        onClick = { viewModel.onGenderChanged(Gender.MALE) },
                                        label = { Text("Male") }
                                    )
                                    FilterChip(
                                        selected = uiState.gender == Gender.FEMALE,
                                        onClick = { viewModel.onGenderChanged(Gender.FEMALE) },
                                        label = { Text("Female") }
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Age: ${uiState.age} yrs",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                            Slider(
                                value = uiState.age.toFloat(),
                                onValueChange = { viewModel.onAgeChanged(it.toInt()) },
                                valueRange = 10f..100f
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            if (uiState.unitSystem == UnitSystem.METRIC) {
                                // Height Slider (cm)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Height", style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        "${uiState.heightCm.toInt()} cm",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    )
                                }
                                Slider(
                                    value = uiState.heightCm,
                                    onValueChange = { viewModel.onHeightCmChanged(it) },
                                    valueRange = 100f..230f
                                )

                                // Weight Slider (kg)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Weight", style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        "${String.format(java.util.Locale.US, "%.1f", uiState.weightKg)} kg",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    )
                                }
                                Slider(
                                    value = uiState.weightKg,
                                    onValueChange = { viewModel.onWeightKgChanged(it) },
                                    valueRange = 30f..180f
                                )
                            } else {
                                // Imperial Height
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Height", style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        "${uiState.heightFeet} ft ${uiState.heightInches} in",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.md)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Feet: ${uiState.heightFeet}", style = MaterialTheme.typography.bodySmall)
                                        Slider(
                                            value = uiState.heightFeet.toFloat(),
                                            onValueChange = { viewModel.onHeightImperialChanged(it.toInt(), uiState.heightInches) },
                                            valueRange = 3f..7f
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Inches: ${uiState.heightInches}", style = MaterialTheme.typography.bodySmall)
                                        Slider(
                                            value = uiState.heightInches.toFloat(),
                                            onValueChange = { viewModel.onHeightImperialChanged(uiState.heightFeet, it.toInt()) },
                                            valueRange = 0f..11f
                                        )
                                    }
                                }

                                // Imperial Weight
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Weight", style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        "${String.format(java.util.Locale.US, "%.1f", uiState.weightLbs)} lbs",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    )
                                }
                                Slider(
                                    value = uiState.weightLbs,
                                    onValueChange = { viewModel.onWeightLbsChanged(it) },
                                    valueRange = 70f..400f
                                )
                            }
                        }
                    }
                }

                // Results Card
                uiState.result?.let { res ->
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
                                    text = "Your Body Mass Index",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = BmiEngine.formatNumber(res.bmi),
                                    style = MaterialTheme.typography.displayLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 48.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = Color(res.category.colorHex).copy(alpha = 0.9f),
                                    modifier = Modifier.padding(vertical = spacing.xs)
                                ) {
                                    Text(
                                        text = res.category.label,
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.xs)
                                    )
                                }

                                Spacer(modifier = Modifier.height(spacing.sm))

                                val healthyRange = if (uiState.unitSystem == UnitSystem.METRIC) {
                                    "${BmiEngine.formatNumber(res.minHealthyWeightKg)} - ${BmiEngine.formatNumber(res.maxHealthyWeightKg)} kg"
                                } else {
                                    "${BmiEngine.formatNumber(res.minHealthyWeightKg * 2.20462)} - ${BmiEngine.formatNumber(res.maxHealthyWeightKg * 2.20462)} lbs"
                                }

                                Text(
                                    text = "Healthy weight range: $healthyRange",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // BMR & Caloric Needs
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
                                Text(
                                    text = "Basal Metabolic Rate (BMR)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${BmiEngine.formatNumber(res.bmrCalories)} kcal / day",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                )
                                Text(
                                    text = "Calories your body burns at complete rest.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = spacing.xs))

                                Text(
                                    text = "Daily Calorie Maintenance",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )

                                res.dailyCaloriesMap.forEach { (level, kcal) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = level.label,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "$kcal kcal",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
