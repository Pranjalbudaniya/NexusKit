package com.nexuskit.app.feature.tools.science_education

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScienceScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    initialTab: ScienceTab = ScienceTab.PERIODIC_TABLE,
    viewModel: ScienceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current

    LaunchedEffect(initialTab) {
        viewModel.onTabSelected(initialTab)
    }

    DrawerNavigation(
        navController = navController,
        currentToolId = "science_education"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Science & Education",
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
                        ScienceTab.PERIODIC_TABLE to "Periodic Table",
                        ScienceTab.CONSTANTS to "Constants",
                        ScienceTab.PLANETS to "Planets",
                        ScienceTab.OHMS_LAW to "Ohm's Law",
                        ScienceTab.TRIGONOMETRY to "Trigonometry",
                        ScienceTab.SCIENTIFIC_NOTATION to "Sci-Notation"
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
                    ScienceTab.PERIODIC_TABLE -> {
                        PeriodicTableView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    ScienceTab.CONSTANTS -> {
                        ConstantsView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    ScienceTab.PLANETS -> {
                        PlanetsView(spacing = spacing)
                    }
                    ScienceTab.OHMS_LAW -> {
                        OhmsLawView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    ScienceTab.TRIGONOMETRY -> {
                        TrigonometryView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    ScienceTab.SCIENTIFIC_NOTATION -> {
                        ScientificNotationView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                    ScienceTab.FORMULAS -> {
                        ConstantsView(uiState = uiState, viewModel = viewModel, spacing = spacing)
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodicTableView(uiState: ScienceUiState, viewModel: ScienceViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    val filtered = ScienceEngine.periodicTable.filter {
        val q = uiState.elementSearchQuery.trim().lowercase()
        q.isEmpty() || it.name.lowercase().contains(q) || it.symbol.lowercase().contains(q) || it.number.toString() == q
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        item {
            OutlinedTextField(
                value = uiState.elementSearchQuery,
                onValueChange = viewModel::onElementSearchChanged,
                placeholder = { Text("Search element by name, symbol, or number...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        uiState.selectedElement?.let { elem ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("#${elem.number} ${elem.name}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                                Text(elem.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(elem.symbol, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary))
                                }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text("Atomic Mass: ${elem.mass} u", style = MaterialTheme.typography.bodyMedium)
                        Text("Electron Configuration: ${elem.electronConfig}", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace))
                        Text("Group: ${elem.group} | Period: ${elem.period}", style = MaterialTheme.typography.bodyMedium)
                        Text(elem.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        items(filtered, key = { it.number }) { elem ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onSelectElement(elem) },
                shape = RoundedCornerShape(12.dp),
                color = if (uiState.selectedElement?.number == elem.number) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceContainer
            ) {
                Row(
                    modifier = Modifier.padding(spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.md)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(elem.symbol, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(elem.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                        Text("${elem.category} • #${elem.number}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("${elem.mass} u", style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace))
                }
            }
        }
    }
}

@Composable
private fun ConstantsView(uiState: ScienceUiState, viewModel: ScienceViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    val filtered = ScienceEngine.constants.filter {
        val q = uiState.constantSearchQuery.trim().lowercase()
        q.isEmpty() || it.name.lowercase().contains(q) || it.symbol.lowercase().contains(q) || it.category.lowercase().contains(q)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm)
    ) {
        item {
            OutlinedTextField(
                value = uiState.constantSearchQuery,
                onValueChange = viewModel::onConstantSearchChanged,
                placeholder = { Text("Search physical constants...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        items(filtered) { const ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(const.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1f))
                        Text(const.symbol, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    }
                    Text("${const.value} ${const.unit}", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace))
                    Text(const.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun PlanetsView(spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        items(ScienceEngine.planets) { planet ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(planet.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        Text(planet.type, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    Text(planet.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Diameter: ${planet.diameterKm} km", style = MaterialTheme.typography.bodySmall)
                        Text("Gravity: ${planet.gravityMps2} m/s²", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Orbit: ${planet.orbitalPeriod}", style = MaterialTheme.typography.bodySmall)
                        Text("Moons: ${planet.moons}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun OhmsLawView(uiState: ScienceUiState, viewModel: ScienceViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
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
                    Text("Ohm's Law & Power Calculator", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Enter any two values to solve for the other two (V = I × R, P = V × I)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OutlinedTextField(
                        value = uiState.voltageInput,
                        onValueChange = viewModel::onVoltageChanged,
                        label = { Text("Voltage (V) in Volts") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = uiState.currentInput,
                        onValueChange = viewModel::onCurrentChanged,
                        label = { Text("Current (I) in Amperes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = uiState.resistanceInput,
                        onValueChange = viewModel::onResistanceChanged,
                        label = { Text("Resistance (R) in Ohms (Ω)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = uiState.powerInput,
                        onValueChange = viewModel::onPowerChanged,
                        label = { Text("Power (P) in Watts (W)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        if (uiState.ohmsResults.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                        Text("Calculated Results", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        uiState.ohmsResults["V"]?.let { Text("Voltage (V): ${String.format(java.util.Locale.US, "%.4f", it)} V", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)) }
                        uiState.ohmsResults["I"]?.let { Text("Current (I): ${String.format(java.util.Locale.US, "%.4f", it)} A", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)) }
                        uiState.ohmsResults["R"]?.let { Text("Resistance (R): ${String.format(java.util.Locale.US, "%.4f", it)} Ω", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)) }
                        uiState.ohmsResults["P"]?.let { Text("Power (P): ${String.format(java.util.Locale.US, "%.4f", it)} W", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrigonometryView(uiState: ScienceUiState, viewModel: ScienceViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
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
                    Text("Trigonometric Functions", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = uiState.trigAngleInput,
                            onValueChange = viewModel::onTrigAngleChanged,
                            label = { Text("Angle") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(Modifier.width(spacing.sm))
                        FilterChip(
                            selected = uiState.isTrigDegrees,
                            onClick = viewModel::toggleTrigUnit,
                            label = { Text(if (uiState.isTrigDegrees) "Degrees (°)" else "Radians (rad)") }
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
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    uiState.trigResults.forEach { (fn, value) ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("$fn(θ)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                            Text(if (value.isNaN()) "Undefined" else String.format(java.util.Locale.US, "%.6f", value), style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace))
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun ScientificNotationView(uiState: ScienceUiState, viewModel: ScienceViewModel, spacing: com.nexuskit.app.ui.theme.NexusKitSpacing) {
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
                    Text("Standard Number to Scientific Notation", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    OutlinedTextField(
                        value = uiState.decimalInput,
                        onValueChange = viewModel::onDecimalChanged,
                        label = { Text("Decimal / Integer Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Result: ${uiState.scientificResult}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                            modifier = Modifier.padding(spacing.md)
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
                Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("Scientific Notation to Standard Number", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        OutlinedTextField(
                            value = uiState.mantissaInput,
                            onValueChange = { viewModel.onMantissaExponentChanged(it, uiState.exponentInput) },
                            label = { Text("Mantissa (e.g. 4.2)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = uiState.exponentInput,
                            onValueChange = { viewModel.onMantissaExponentChanged(uiState.mantissaInput, it) },
                            label = { Text("Exponent (e.g. -5)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Standard: ${uiState.decimalResult}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                            modifier = Modifier.padding(spacing.md)
                        )
                    }
                }
            }
        }
    }
}
