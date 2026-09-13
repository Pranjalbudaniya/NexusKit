package com.nexuskit.app.feature.tools.screen_light

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
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
fun ScreenLightScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: ScreenLightViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    DrawerNavigation(
        navController = navController,
        currentToolId = "screen_light"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Flashlight & Morse Tools",
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
                // ── Mode selector ─────────────────────────────────────────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                    ) {
                        LightMode.entries.forEach { mode ->
                            val label = when (mode) {
                                LightMode.SCREEN -> "Screen Light"
                                LightMode.FLASHLIGHT -> "Camera Torch"
                                LightMode.MORSE -> "Morse Transmitter"
                                LightMode.STROBE -> "Strobe"
                                LightMode.SOS -> "Emergency SOS"
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
                    LightMode.MORSE -> {
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
                                    // Visual Beacon / Flasher
                                    val beaconColor by animateColorAsState(
                                        targetValue = if (uiState.isMorseTransmitting && uiState.isScreenFlashOn && uiState.morseScreenEnabled) Color.White else Color(0xFF1E1E1E),
                                        label = "beacon"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(72.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(beaconColor)
                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (uiState.isMorseTransmitting) "TRANSMITTING MORSE CODE" else "READY TO TRANSMIT",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 2.sp,
                                                color = if (uiState.isMorseTransmitting && uiState.isScreenFlashOn && uiState.morseScreenEnabled) Color.Black else Color.Gray
                                            )
                                        )
                                    }

                                    // Input Text Field
                                    OutlinedTextField(
                                        value = uiState.morseInputText,
                                        onValueChange = viewModel::onMorseInputChanged,
                                        label = { Text("Enter text to transmit in Morse") },
                                        placeholder = { Text("Type any message...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = {
                                            if (uiState.morseInputText.isNotEmpty()) {
                                                IconButton(onClick = { viewModel.onMorseInputChanged("") }) {
                                                    Icon(Icons.Filled.Clear, contentDescription = "Clear")
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    // Quick Presets
                                    Text("Quick Presets", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                    ) {
                                        listOf("SOS", "HELP", "HELLO", "OK", "MAYDAY", "LOVE YOU", "SAFE").forEach { preset ->
                                            FilterChip(
                                                selected = uiState.morseInputText.equals(preset, ignoreCase = true),
                                                onClick = { viewModel.setMorsePreset(preset) },
                                                label = { Text(preset, fontWeight = FontWeight.SemiBold) }
                                            )
                                        }
                                    }

                                    // Live Morse Code Visualizer Card
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Column(modifier = Modifier.padding(spacing.md), verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("Morse Code Translation", style = MaterialTheme.typography.titleSmall)
                                                IconButton(onClick = {
                                                    val morseStr = ScreenLightEngine.textToMorse(uiState.morseInputText)
                                                    clipboardManager.setText(AnnotatedString(morseStr))
                                                    Toast.makeText(context, "Morse Code copied to clipboard", Toast.LENGTH_SHORT).show()
                                                }) {
                                                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp))
                                                }
                                            }

                                            // Flow of symbols
                                            FlowRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                uiState.morseSymbols.forEachIndexed { index, item ->
                                                    val isCurrent = uiState.isMorseTransmitting && uiState.activeSymbolIndex == index
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                                        border = if (isCurrent) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                                                    ) {
                                                        Column(
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                            horizontalAlignment = Alignment.CenterHorizontally
                                                        ) {
                                                            Text(
                                                                text = if (item.char == ' ') "␣" else item.char.toString(),
                                                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                                                color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                            Text(
                                                                text = item.morse,
                                                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                                                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    HorizontalDivider()

                                    // Transmission Channels
                                    Text("Transmission Channels", style = MaterialTheme.typography.titleSmall)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                    ) {
                                        FilterChip(
                                            selected = uiState.morseTorchEnabled,
                                            onClick = viewModel::toggleMorseTorch,
                                            leadingIcon = { Icon(Icons.Filled.FlashlightOn, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                            label = { Text("Flashlight LED") }
                                        )
                                        FilterChip(
                                            selected = uiState.morseScreenEnabled,
                                            onClick = viewModel::toggleMorseScreen,
                                            leadingIcon = { Icon(Icons.Filled.StayCurrentPortrait, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                            label = { Text("Screen") }
                                        )
                                        FilterChip(
                                            selected = uiState.morseAudioEnabled,
                                            onClick = viewModel::toggleMorseAudio,
                                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                            label = { Text("Audio Beep") }
                                        )
                                        FilterChip(
                                            selected = uiState.morseHapticEnabled,
                                            onClick = viewModel::toggleMorseHaptic,
                                            leadingIcon = { Icon(Icons.Filled.Vibration, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                            label = { Text("Vibrate") }
                                        )
                                    }

                                    // Speed & Looping
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Speed: ${uiState.morseWpm} WPM (${ScreenLightEngine.wpmToDotDurationMs(uiState.morseWpm)}ms/dot)", style = MaterialTheme.typography.bodyMedium)
                                        FilterChip(
                                            selected = uiState.isMorseLooping,
                                            onClick = viewModel::toggleMorseLoop,
                                            leadingIcon = { Icon(Icons.Filled.Repeat, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                            label = { Text("Loop") }
                                        )
                                    }
                                    Slider(
                                        value = uiState.morseWpm.toFloat(),
                                        onValueChange = { viewModel.onMorseWpmChanged(it.toInt()) },
                                        valueRange = 5f..35f,
                                        steps = 29
                                    )

                                    // Start / Stop Transmission Button
                                    Button(
                                        onClick = viewModel::toggleMorseTransmission,
                                        modifier = Modifier.fillMaxWidth().height(54.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (uiState.isMorseTransmitting) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text(
                                            text = if (uiState.isMorseTransmitting) "STOP TRANSMISSION" else "TRANSMIT IN MORSE CODE",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    LightMode.SCREEN -> {
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
                                    Text("Screen Color Light Preview", style = MaterialTheme.typography.titleSmall)

                                    // Display Lamp
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(uiState.screenColor.copy(alpha = uiState.brightnessFraction))
                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                                    )

                                    // Brightness Slider
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Brightness", style = MaterialTheme.typography.bodySmall)
                                        Text("${(uiState.brightnessFraction * 100).toInt()}%", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                    }
                                    Slider(
                                        value = uiState.brightnessFraction,
                                        onValueChange = viewModel::onBrightnessChanged,
                                        valueRange = 0.1f..1f
                                    )

                                    Text("Color Presets", style = MaterialTheme.typography.titleSmall)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                                    ) {
                                        ScreenLightEngine.presets.forEach { preset ->
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.clickable { viewModel.onScreenColorSelected(preset.color) }
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(50.dp)
                                                        .clip(CircleShape)
                                                        .background(preset.color)
                                                        .border(
                                                            width = if (uiState.screenColor == preset.color) 3.dp else 1.dp,
                                                            color = if (uiState.screenColor == preset.color) MaterialTheme.colorScheme.primary else Color.Gray,
                                                            shape = CircleShape
                                                        )
                                                )
                                                Spacer(Modifier.height(4.dp))
                                                Text(preset.name, style = MaterialTheme.typography.labelSmall)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    LightMode.FLASHLIGHT -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(spacing.xxl),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(spacing.lg)
                                ) {
                                    Button(
                                        onClick = viewModel::toggleFlashlight,
                                        modifier = Modifier.size(130.dp),
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (uiState.isTorchOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (uiState.isTorchOn) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
                                            contentDescription = if (uiState.isTorchOn) "Torch ON" else "Torch OFF",
                                            modifier = Modifier.size(60.dp),
                                            tint = if (uiState.isTorchOn) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Text(
                                        text = if (uiState.isTorchOn) "Flashlight is ON" else "Flashlight is OFF",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (uiState.isTorchOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    LightMode.STROBE -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(spacing.lg),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(spacing.md)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                if (uiState.isStrobeRunning && uiState.isScreenFlashOn) Color.White else Color.Black
                                            )
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Strobe Frequency", style = MaterialTheme.typography.titleSmall)
                                        Text("${uiState.strobeFrequencyHz.toInt()} Hz", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                                    }

                                    Slider(
                                        value = uiState.strobeFrequencyHz,
                                        onValueChange = viewModel::onStrobeFrequencyChanged,
                                        valueRange = 1f..15f,
                                        steps = 13
                                    )

                                    Button(
                                        onClick = viewModel::toggleStrobe,
                                        modifier = Modifier.fillMaxWidth().height(50.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (uiState.isStrobeRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text(if (uiState.isStrobeRunning) "Stop Strobe" else "Start Strobe Flasher", style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                        }
                    }

                    LightMode.SOS -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(
                                    containerColor = if (uiState.isSosRunning) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(spacing.lg),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(spacing.md)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Warning,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "Emergency SOS (Morse Code)",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "Flashes camera LED & screen in standard Morse SOS signal (... --- ...)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Button(
                                        onClick = viewModel::toggleSos,
                                        modifier = Modifier.fillMaxWidth().height(54.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (uiState.isSosRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text(if (uiState.isSosRunning) "STOP SOS SIGNAL" else "START EMERGENCY SOS", style = MaterialTheme.typography.titleMedium)
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
