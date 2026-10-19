package com.nexuskit.app.feature.tools.gradient_generator

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.nexuskit.app.core.util.GallerySaver
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradientScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: GradientViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedCodeTab by remember { mutableIntStateOf(0) }
    val presetScrollState = rememberScrollState()

    val quickColors = listOf(
        Color(0xFFFF512F), Color(0xFFDD2476), Color(0xFF8A2387), Color(0xFFE94057),
        Color(0xFF00C9FF), Color(0xFF92FE9D), Color(0xFF7F00FF), Color(0xFF1BFFFF),
        Color(0xFFF12711), Color(0xFFF5AF19), Color(0xFF000428), Color(0xFF0575E6)
    )

    val currentBrush: Brush = when (uiState.gradientType) {
        GradientType.LINEAR -> Brush.linearGradient(colors = uiState.colors)
        GradientType.RADIAL -> Brush.radialGradient(colors = uiState.colors)
        GradientType.SWEEP -> Brush.sweepGradient(colors = uiState.colors)
    }

    DrawerNavigation(
        navController = navController,
        currentToolId = "gradient_generator"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Gradient Studio",
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
                // Live Interactive Gradient Preview Box
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(currentBrush),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(spacing.md)
                                    .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${uiState.gradientType.label} • ${uiState.angleDegrees.toInt()}°",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Action Buttons: Save to Gallery, Share, Randomize
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        Button(
                            onClick = {
                                val bmp = GradientEngine.renderWallpaperBitmap(
                                    type = uiState.gradientType,
                                    colors = uiState.colors,
                                    angleDegrees = uiState.angleDegrees
                                )
                                val res = GallerySaver.saveBitmapToGallery(
                                    context = context,
                                    bitmap = bmp,
                                    titlePrefix = "gradient_wallpaper"
                                )
                                scope.launch {
                                    if (res.isSuccess) {
                                        snackbarHostState.showSnackbar("Saved HD Wallpaper to Gallery!")
                                    } else {
                                        snackbarHostState.showSnackbar("Failed to save wallpaper")
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(spacing.xs))
                            Text("Save Wallpaper")
                        }

                        OutlinedButton(
                            onClick = {
                                val bmp = GradientEngine.renderWallpaperBitmap(
                                    type = uiState.gradientType,
                                    colors = uiState.colors,
                                    angleDegrees = uiState.angleDegrees
                                )
                                val res = GallerySaver.saveBitmapToGallery(
                                    context = context,
                                    bitmap = bmp,
                                    titlePrefix = "gradient_share"
                                )
                                res.onSuccess { uri ->
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "image/png"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Gradient Wallpaper"))
                                }
                            },
                            modifier = Modifier.weight(0.7f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(spacing.xs))
                            Text("Share")
                        }

                        IconButton(
                            onClick = { viewModel.randomize() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Randomize", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // Presets Carousel
                item {
                    Column {
                        Text(
                            text = "Designer Presets",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(spacing.xs))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(presetScrollState),
                            horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                        ) {
                            GradientEngine.PRESETS.forEach { preset ->
                                FilterChip(
                                    selected = uiState.activePresetName == preset.name,
                                    onClick = { viewModel.applyPreset(preset) },
                                    label = { Text(preset.name, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }

                // Customization Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(spacing.md)) {
                            Text(
                                text = "Gradient Style & Geometry",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(spacing.xs))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                            ) {
                                GradientType.entries.forEach { type ->
                                    FilterChip(
                                        selected = uiState.gradientType == type,
                                        onClick = { viewModel.onGradientTypeChanged(type) },
                                        label = { Text(type.label, style = MaterialTheme.typography.labelSmall) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            if (uiState.gradientType == GradientType.LINEAR) {
                                Spacer(modifier = Modifier.height(spacing.sm))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Angle", style = MaterialTheme.typography.bodySmall)
                                    Text("${uiState.angleDegrees.toInt()}°", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                }
                                Slider(
                                    value = uiState.angleDegrees,
                                    onValueChange = { viewModel.onAngleChanged(it) },
                                    valueRange = 0f..360f,
                                    steps = 71
                                )
                            }

                            Spacer(modifier = Modifier.height(spacing.sm))

                            // Color Stops List
                            Text(
                                text = "Color Stops (${uiState.colors.size}/5)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(spacing.xs))

                            uiState.colors.forEachIndexed { index, color ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                        Spacer(modifier = Modifier.width(spacing.sm))
                                        Text(
                                            text = GradientEngine.toHexString(color),
                                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                                        )
                                    }
                                    if (uiState.colors.size > 2) {
                                        IconButton(
                                            onClick = { viewModel.removeColor(index) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete stop", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }

                            // Quick Color Swatches to Add/Replace
                            Spacer(modifier = Modifier.height(spacing.sm))
                            Text(
                                text = "Add or Pick Color",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(spacing.xs))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                quickColors.forEach { qColor ->
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(qColor)
                                            .clickable {
                                                if (uiState.colors.size < 5) {
                                                    viewModel.addColor(qColor)
                                                } else {
                                                    viewModel.updateColor(uiState.colors.size - 1, qColor)
                                                }
                                            }
                                    )
                                }
                            }
                        }
                    }
                }

                // Code Exports Card
                item {
                    val cssCode = GradientEngine.generateCss(uiState.gradientType, uiState.colors, uiState.angleDegrees)
                    val composeCode = GradientEngine.generateComposeCode(uiState.gradientType, uiState.colors, uiState.angleDegrees)
                    val xmlCode = GradientEngine.generateXmlShape(uiState.gradientType, uiState.colors, uiState.angleDegrees)

                    val activeCode = when (selectedCodeTab) {
                        0 -> cssCode
                        1 -> composeCode
                        else -> xmlCode
                    }

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
                                    text = "Code Export",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(activeCode))
                                        scope.launch { snackbarHostState.showSnackbar("Copied code to clipboard") }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy code", modifier = Modifier.size(18.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.xs))

                            TabRow(
                                selectedTabIndex = selectedCodeTab,
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                Tab(selected = selectedCodeTab == 0, onClick = { selectedCodeTab = 0 }, text = { Text("CSS") })
                                Tab(selected = selectedCodeTab == 1, onClick = { selectedCodeTab = 1 }, text = { Text("Compose") })
                                Tab(selected = selectedCodeTab == 2, onClick = { selectedCodeTab = 2 }, text = { Text("Android XML") })
                            }

                            Spacer(modifier = Modifier.height(spacing.sm))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = activeCode,
                                    modifier = Modifier.padding(spacing.md),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
