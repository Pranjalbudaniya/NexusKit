package com.nexuskit.app.feature.tools.randomizer

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun RandomizerScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: RandomizerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current

    DrawerNavigation(
        navController = navController,
        currentToolId = "randomizer"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Randomizer & Decisions",
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
                // Mode Chips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                    ) {
                        RandomizerMode.entries.forEach { mode ->
                            val label = when (mode) {
                                RandomizerMode.NUMBERS -> "Numbers"
                                RandomizerMode.DICE -> "Dice Roller"
                                RandomizerMode.COIN -> "Coin Flipper"
                                RandomizerMode.LIST -> "Decision / Pick"
                            }
                            FilterChip(
                                selected = uiState.selectedMode == mode,
                                onClick = { viewModel.onModeSelected(mode) },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }

                when (uiState.selectedMode) {
                    RandomizerMode.NUMBERS -> {
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
                                    Text("Number Range", style = MaterialTheme.typography.titleSmall)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Min: ${uiState.minNumber}")
                                        Text("Max: ${uiState.maxNumber}")
                                    }
                                    Slider(
                                        value = uiState.maxNumber.toFloat(),
                                        onValueChange = { viewModel.onRangeChanged(uiState.minNumber, it.toInt(), uiState.numberCount, uiState.allowDuplicates) },
                                        valueRange = 10f..1000f
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Quantity: ${uiState.numberCount}")
                                    }
                                    Slider(
                                        value = uiState.numberCount.toFloat(),
                                        onValueChange = { viewModel.onRangeChanged(uiState.minNumber, uiState.maxNumber, it.toInt(), uiState.allowDuplicates) },
                                        valueRange = 1f..10f
                                    )

                                    Button(
                                        onClick = viewModel::generateNumbers,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Generate Random Numbers")
                                    }
                                }
                            }
                        }

                        // Number Results
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
                                    Text("Result", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                    Spacer(Modifier.height(spacing.sm))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalArrangement = Arrangement.spacedBy(spacing.xs)
                                    ) {
                                        uiState.numberResult.numbers.forEach { num ->
                                            Surface(
                                                shape = CircleShape,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(spacing.xs).size(60.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = num.toString(),
                                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    RandomizerMode.DICE -> {
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
                                    Text("Dice Type", style = MaterialTheme.typography.titleSmall)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                    ) {
                                        DiceType.entries.forEach { dice ->
                                            FilterChip(
                                                selected = uiState.selectedDiceType == dice,
                                                onClick = { viewModel.onDiceTypeSelected(dice) },
                                                label = { Text(dice.label) }
                                            )
                                        }
                                    }

                                    Text("Number of Dice: ${uiState.diceCount}")
                                    Slider(
                                        value = uiState.diceCount.toFloat(),
                                        onValueChange = { viewModel.onDiceCountChanged(it.toInt()) },
                                        valueRange = 1f..6f
                                    )

                                    Button(
                                        onClick = viewModel::rollDice,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Filled.Casino, contentDescription = null)
                                        Spacer(Modifier.size(spacing.xs))
                                        Text("Roll Dice")
                                    }
                                }
                            }
                        }

                        // Dice Result
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
                                    Text("Total Sum", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                    Text(
                                        text = "${uiState.diceResult.totalSum}",
                                        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )

                                    Spacer(Modifier.height(spacing.sm))

                                    FlowRow(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalArrangement = Arrangement.spacedBy(spacing.xs)
                                    ) {
                                        uiState.diceResult.rolls.forEach { roll ->
                                            Surface(
                                                shape = MaterialTheme.shapes.medium,
                                                color = MaterialTheme.colorScheme.surface,
                                                modifier = Modifier.padding(spacing.xs).size(48.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = roll.toString(),
                                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    RandomizerMode.COIN -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(spacing.xl),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(spacing.md)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(130.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = uiState.coinResult,
                                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = viewModel::flipCoin,
                                        modifier = Modifier.fillMaxWidth().height(50.dp)
                                    ) {
                                        Text("Flip Coin", style = MaterialTheme.typography.titleMedium)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Text("Heads: ${uiState.headsCount}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                        Text("Tails: ${uiState.tailsCount}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                    }

                                    TextButton(onClick = viewModel::resetCoinScores) {
                                        Text("Reset Counter")
                                    }
                                }
                            }
                        }
                    }

                    RandomizerMode.LIST -> {
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
                                    Text("Enter Options (1 per line)", style = MaterialTheme.typography.titleSmall)
                                    OutlinedTextField(
                                        value = uiState.listInputText,
                                        onValueChange = viewModel::onListInputChanged,
                                        modifier = Modifier.fillMaxWidth().height(120.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                                    ) {
                                        Button(
                                            onClick = viewModel::pickFromList,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Pick One")
                                        }
                                        OutlinedButton(
                                            onClick = viewModel::shuffleList,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Shuffle List")
                                        }
                                    }
                                }
                            }
                        }

                        // Picked item
                        uiState.pickedItem?.let { picked ->
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
                                        Text("Random Pick", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                        Text(
                                            text = picked,
                                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }

                        // Shuffled items
                        if (uiState.shuffledList.isNotEmpty()) {
                            item {
                                Text("Shuffled Order:", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = spacing.sm))
                            }
                            items(uiState.shuffledList) { itm ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.small,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                                ) {
                                    Text(text = itm, modifier = Modifier.padding(spacing.md), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
