package com.nexuskit.app.feature.tools.color_palette

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPaletteScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: ColorPaletteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val currentColor = Color(uiState.red, uiState.green, uiState.blue)

    DrawerNavigation(
        navController = navController,
        currentToolId = "color_palette"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Color Palette & Contrast",
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
                // Color Display & HEX Input
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(spacing.md)
                        ) {
                            // Big Color swatch
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(currentColor)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(spacing.xs)
                                ) {
                                    Text(
                                        text = uiState.hexString,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        ),
                                        modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.xs)
                                    )
                                }
                            }

                            Spacer(Modifier.height(spacing.md))

                            OutlinedTextField(
                                value = uiState.hexString,
                                onValueChange = viewModel::onHexChanged,
                                label = { Text("HEX Code") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(spacing.sm))

                            // RGB Sliders
                            // Red
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Red", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE53935))
                                Text("${uiState.red}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }
                            Slider(
                                value = uiState.red.toFloat(),
                                onValueChange = { viewModel.onRgbChanged(it.toInt(), uiState.green, uiState.blue) },
                                valueRange = 0f..255f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFFE53935), activeTrackColor = Color(0xFFE53935))
                            )

                            // Green
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Green", style = MaterialTheme.typography.bodySmall, color = Color(0xFF43A047))
                                Text("${uiState.green}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }
                            Slider(
                                value = uiState.green.toFloat(),
                                onValueChange = { viewModel.onRgbChanged(uiState.red, it.toInt(), uiState.blue) },
                                valueRange = 0f..255f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF43A047), activeTrackColor = Color(0xFF43A047))
                            )

                            // Blue
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Blue", style = MaterialTheme.typography.bodySmall, color = Color(0xFF1E88E5))
                                Text("${uiState.blue}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }
                            Slider(
                                value = uiState.blue.toFloat(),
                                onValueChange = { viewModel.onRgbChanged(uiState.red, uiState.green, it.toInt()) },
                                valueRange = 0f..255f,
                                colors = SliderDefaults.colors(thumbColor = Color(0xFF1E88E5), activeTrackColor = Color(0xFF1E88E5))
                            )
                        }
                    }
                }

                // WCAG Contrast Card
                val contrast = uiState.contrast
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(spacing.md),
                            verticalArrangement = Arrangement.spacedBy(spacing.sm)
                        ) {
                            Text("WCAG 2.1 Contrast Ratio", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.md)
                            ) {
                                // On White
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f).border(1.dp, Color.LightGray, MaterialTheme.shapes.medium)
                                ) {
                                    Column(modifier = Modifier.padding(spacing.sm), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("On White", style = MaterialTheme.typography.labelSmall, color = Color.Black)
                                        Text(
                                            "${String.format(java.util.Locale.US, "%.2f", contrast.contrastOnWhite)}:1",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = currentColor)
                                        )
                                        Text(
                                            if (contrast.passAAWhite) "AA PASS" else "AA FAIL",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (contrast.passAAWhite) Color(0xFF2E7D32) else Color(0xFFC62828)
                                            )
                                        )
                                    }
                                }

                                // On Black
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(spacing.sm), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("On Black", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                        Text(
                                            "${String.format(java.util.Locale.US, "%.2f", contrast.contrastOnBlack)}:1",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = currentColor)
                                        )
                                        Text(
                                            if (contrast.passAABlack) "AA PASS" else "AA FAIL",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (contrast.passAABlack) Color(0xFF81C784) else Color(0xFFEF5350)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Color Harmonies
                item {
                    Text(
                        text = "Harmonic Palettes",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(uiState.swatches, key = { it.name }) { swatch ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(spacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.md)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(swatch.color)
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                )
                                Column {
                                    Text(swatch.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                    Text(swatch.hex, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = MaterialTheme.colorScheme.primary)
                                    Text(swatch.hsl, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(swatch.hex))
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Copied ${swatch.hex} to clipboard")
                                    }
                                }
                            ) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = "Copy HEX")
                            }
                        }
                    }
                }
            }
        }
    }
}
