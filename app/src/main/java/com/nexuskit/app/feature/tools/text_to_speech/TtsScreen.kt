package com.nexuskit.app.feature.tools.text_to_speech

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TtsScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: TtsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ttsState by viewModel.ttsState.collectAsStateWithLifecycle()
    val languages by viewModel.availableLanguages.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val snackbarHostState = remember { SnackbarHostState() }
    var langDropdownExpanded by remember { mutableStateOf(false) }

    DrawerNavigation(
        navController = navController,
        currentToolId = "text_to_speech"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Offline Text-to-Speech",
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
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                // Engine Status Card
                item {
                    val isSpeaking = ttsState == TtsState.SPEAKING
                    val statusText = when (ttsState) {
                        TtsState.INITIALIZING -> "Initializing offline speech engine..."
                        TtsState.READY -> "Speech Engine Ready (Offline)"
                        TtsState.SPEAKING -> "Speaking..."
                        TtsState.ERROR -> "Speech engine error on this device"
                    }
                    val statusColor = when (ttsState) {
                        TtsState.SPEAKING -> MaterialTheme.colorScheme.primary
                        TtsState.ERROR -> MaterialTheme.colorScheme.error
                        else -> Color(0xFF2E7D32)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (isSpeaking) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(spacing.sm))
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = statusColor
                            )
                        }
                    }
                }

                // Input Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(spacing.md)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Text to Speak",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Row {
                                    OutlinedButton(
                                        onClick = { viewModel.loadSample() },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Sample", style = MaterialTheme.typography.labelSmall)
                                    }
                                    if (uiState.text.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(spacing.xs))
                                        IconButton(
                                            onClick = { viewModel.clear() },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.sm))

                            OutlinedTextField(
                                value = uiState.text,
                                onValueChange = { viewModel.onTextChanged(it) },
                                placeholder = { Text("Enter text to speak aloud...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                maxLines = 8
                            )
                        }
                    }
                }

                // Voice & Sound Controls Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(spacing.md)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Voice & Engine Settings",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                IconButton(
                                    onClick = { viewModel.resetControls() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.RestartAlt, contentDescription = "Reset", modifier = Modifier.size(18.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.sm))

                            // Language Picker
                            if (languages.isNotEmpty()) {
                                ExposedDropdownMenuBox(
                                    expanded = langDropdownExpanded,
                                    onExpandedChange = { langDropdownExpanded = !langDropdownExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = uiState.selectedLanguage?.displayName ?: "Default Voice",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Language / Voice") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = langDropdownExpanded) },
                                        modifier = Modifier.fillMaxWidth().menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = langDropdownExpanded,
                                        onDismissRequest = { langDropdownExpanded = false }
                                    ) {
                                        languages.forEach { lang ->
                                            DropdownMenuItem(
                                                text = { Text(lang.displayName) },
                                                onClick = {
                                                    viewModel.onLanguageSelected(lang)
                                                    langDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(spacing.md))
                            }

                            // Speech Rate Slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Speech Rate", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "${String.format(Locale.US, "%.1f", uiState.speechRate)}x",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Slider(
                                value = uiState.speechRate,
                                onValueChange = { viewModel.onSpeechRateChanged(it) },
                                valueRange = 0.5f..2.5f,
                                steps = 19
                            )

                            Spacer(modifier = Modifier.height(spacing.xs))

                            // Pitch Slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Pitch", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "${String.format(Locale.US, "%.1f", uiState.pitch)}x",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Slider(
                                value = uiState.pitch,
                                onValueChange = { viewModel.onPitchChanged(it) },
                                valueRange = 0.5f..2.0f,
                                steps = 14
                            )
                        }
                    }
                }

                // Play / Stop Action Button
                item {
                    val isSpeaking = ttsState == TtsState.SPEAKING
                    if (isSpeaking) {
                        Button(
                            onClick = { viewModel.stop() },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(spacing.xs))
                            Text("Stop Speaking")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.speak() },
                            enabled = uiState.text.isNotBlank() && ttsState == TtsState.READY,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(spacing.xs))
                            Text("Speak Text")
                        }
                    }
                }
            }
        }
    }
}
