package com.nexuskit.app.feature.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexuskit.app.BuildConfig
import com.nexuskit.app.R
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.domain.model.PreferenceUpdate
import com.nexuskit.app.domain.model.enums.ShadowElevation
import com.nexuskit.app.domain.model.enums.SpacingMode
import com.nexuskit.app.domain.model.enums.StatusBarStyle
import com.nexuskit.app.domain.model.enums.ThemeMode
import com.nexuskit.app.ui.component.ShapePicker
import com.nexuskit.app.ui.theme.IconContainerShape
import com.nexuskit.app.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current

    // Which sections are expanded (local state — not persisted)
    val expandedSections = remember { mutableStateMapOf("appearance" to false, "about" to false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = spacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            // ── APPEARANCE ─────────────────────────────────────────────────
            item(key = "appearance") {
                SettingsSection(
                    title     = "Appearance",
                    icon      = Icons.Filled.Palette,
                    isExpanded= expandedSections["appearance"] == true,
                    onToggle  = { expandedSections["appearance"] = !(expandedSections["appearance"] ?: false) }
                ) {
                    // App Name
                    SettingsItemRow(
                        icon      = Icons.Filled.Edit,
                        title     = "App Name",
                        value     = uiState.preferences.appName,
                        onClick   = { viewModel.openSheet(SettingsSheet.APP_NAME) }
                    )
                    // Theme (colour)
                    SettingsItemRow(
                        icon    = Icons.Filled.Palette,
                        title   = "Theme",
                        value   = if (uiState.preferences.dynamicColorEnabled) "Dynamic"
                                  else "Custom",
                        onClick = { viewModel.openSheet(SettingsSheet.THEME) }
                    )
                    // Theme Mode
                    SettingsItemRow(
                        icon    = Icons.Filled.AutoAwesome,
                        title   = "Theme Mode",
                        value   = uiState.preferences.themeMode.displayName,
                        onClick = { viewModel.openSheet(SettingsSheet.THEME_MODE) }
                    )
                    // Font
                    SettingsItemRow(
                        icon    = Icons.Filled.FontDownload,
                        title   = "Font",
                        value   = uiState.preferences.fontFamily.displayName,
                        onClick = { viewModel.openSheet(SettingsSheet.FONT) }
                    )
                    // Card Shape
                    SettingsItemRow(
                        icon    = Icons.Filled.Settings,
                        title   = "Card Shape",
                        value   = uiState.preferences.cardShape.displayName,
                        onClick = { viewModel.openSheet(SettingsSheet.CARD_SHAPE) }
                    )
                    // Fav Shape
                    SettingsItemRow(
                        icon    = Icons.Filled.Settings,
                        title   = "Favourite Shape",
                        value   = uiState.preferences.favCardShape.displayName,
                        onClick = { viewModel.openSheet(SettingsSheet.FAV_SHAPE) }
                    )
                    // Shadow
                    SettingsSliderItem(
                        icon        = Icons.Filled.Phone,
                        title       = "Shadow",
                        steps       = ShadowElevation.entries.size - 2,
                        value       = ShadowElevation.entries.indexOf(uiState.preferences.shadowElevation).toFloat(),
                        valueRange  = 0f..(ShadowElevation.entries.size - 1).toFloat(),
                        label       = uiState.preferences.shadowElevation.displayName,
                        onValueChange = { index ->
                            val mode = ShadowElevation.entries.getOrNull(index.toInt())
                                ?: ShadowElevation.MEDIUM
                            viewModel.update(PreferenceUpdate.SetShadowElevation(mode))
                        }
                    )
                    // Density
                    SettingsSliderItem(
                        icon        = Icons.Filled.Settings,
                        title       = "Density",
                        steps       = SpacingMode.entries.size - 2,
                        value       = SpacingMode.entries.indexOf(uiState.preferences.spacingMode).toFloat(),
                        valueRange  = 0f..(SpacingMode.entries.size - 1).toFloat(),
                        label       = uiState.preferences.spacingMode.displayName,
                        onValueChange = { index ->
                            val mode = SpacingMode.entries.getOrNull(index.toInt())
                                ?: SpacingMode.COMFORTABLE
                            viewModel.update(PreferenceUpdate.SetSpacingMode(mode))
                        }
                    )
                    // Status Bar
                    SettingsItemRow(
                        icon    = Icons.Filled.Phone,
                        title   = "Status Bar",
                        value   = uiState.preferences.statusBarStyle.displayName,
                        onClick = { viewModel.openSheet(SettingsSheet.STATUS_BAR) }
                    )
                }
            }

            // ── ABOUT ──────────────────────────────────────────────────────
            item(key = "about") {
                SettingsSection(
                    title     = "About",
                    icon      = Icons.Filled.Info,
                    isExpanded= expandedSections["about"] == true,
                    onToggle  = { expandedSections["about"] = !(expandedSections["about"] ?: false) }
                ) {
                    SettingsInfoRow(
                        icon  = Icons.Filled.Info,
                        title = "Version",
                        value = BuildConfig.VERSION_NAME
                    )
                    SettingsLinkRow(
                        icon  = Icons.Filled.Info,
                        title = "GitHub",
                        url   = "https://github.com/Pranjalbudaniya/NexusKit"
                    )
                }
            }

            item { Spacer(Modifier.height(spacing.xxxl)) }
        }
    }

    // ── Bottom sheets ──────────────────────────────────────────────────────────
    when (uiState.openSheet) {
        SettingsSheet.THEME -> ThemePickerSheet(
            preferences = uiState.preferences,
            onUpdate    = viewModel::update,
            onDismiss   = viewModel::closeSheet
        )
        SettingsSheet.CARD_SHAPE -> ShapePicker(
            selectedShape    = uiState.preferences.cardShape,
            onShapeSelected  = { viewModel.update(PreferenceUpdate.SetCardShape(it)) },
            onDismiss        = viewModel::closeSheet
        )
        SettingsSheet.FAV_SHAPE -> ShapePicker(
            selectedShape    = uiState.preferences.favCardShape,
            onShapeSelected  = { viewModel.update(PreferenceUpdate.SetFavCardShape(it)) },
            onDismiss        = viewModel::closeSheet
        )
        SettingsSheet.FONT -> FontPickerSheet(
            selectedFont  = uiState.preferences.fontFamily,
            onFontSelected= { viewModel.update(PreferenceUpdate.SetFontFamily(it)) },
            onDismiss     = viewModel::closeSheet
        )
        SettingsSheet.HIDDEN_TOOLS -> HiddenToolsSheet(
            hiddenTools  = uiState.hiddenTools,
            onUnhide     = viewModel::onUnhideTool,
            onDismiss    = viewModel::closeSheet
        )
        SettingsSheet.THEME_MODE -> SimpleEnumPickerSheet(
            title    = "Theme Mode",
            options  = ThemeMode.entries.toList(),
            selected = uiState.preferences.themeMode,
            labelOf  = { it.displayName },
            onSelect = { viewModel.update(PreferenceUpdate.SetThemeMode(it)) },
            onDismiss= viewModel::closeSheet
        )
        SettingsSheet.STATUS_BAR -> SimpleEnumPickerSheet(
            title    = "Status Bar",
            options  = StatusBarStyle.entries.toList(),
            selected = uiState.preferences.statusBarStyle,
            labelOf  = { it.displayName },
            onSelect = { viewModel.update(PreferenceUpdate.SetStatusBarStyle(it)) },
            onDismiss= viewModel::closeSheet
        )
        SettingsSheet.APP_NAME -> AppNameDialog(
            currentName = uiState.preferences.appName,
            onConfirm   = { viewModel.update(PreferenceUpdate.AppName(it)) },
            onDismiss   = viewModel::closeSheet
        )
        null -> { /* no sheet open */ }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// REUSABLE SETTINGS COMPOSABLES
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    val spacing = LocalSpacing.current
    val chevron by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(AppConstants.ANIMATION_DURATION_MS),
        label = "settingsChevron"
    )
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(tween(AppConstants.ANIMATION_DURATION_MS))
    ) {
        Column {
            // Section header row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = spacing.lg, vertical = spacing.md)
            ) {
                Surface(
                    shape = IconContainerShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(spacing.xs)
                    )
                }
                Spacer(Modifier.width(spacing.md))
                Text(
                    text     = title,
                    style    = MaterialTheme.typography.titleSmall,
                    color    = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(chevron)
                )
            }

            // Expandable content
            AnimatedVisibility(
                visible = isExpanded,
                enter   = fadeIn(tween(AppConstants.ANIMATION_DURATION_MS)) +
                          expandVertically(tween(AppConstants.ANIMATION_DURATION_MS)),
                exit    = fadeOut(tween(AppConstants.ANIMATION_DURATION_MS)) +
                          shrinkVertically(tween(AppConstants.ANIMATION_DURATION_MS))
            ) {
                Column(modifier = Modifier.padding(bottom = spacing.sm)) {
                    content()
                }
            }
        }
    }
}

/** Tappable settings row — icon + title on left, current value + arrow on right. */
@Composable
fun SettingsItemRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = spacing.lg, vertical = spacing.md)
    ) {
        Surface(
            shape = IconContainerShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(spacing.xs)
            )
        }
        Spacer(Modifier.width(spacing.md))
        Text(
            text     = title,
            style    = MaterialTheme.typography.bodyLarge,
            color    = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text  = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Read-only info row. */
@Composable
fun SettingsInfoRow(icon: ImageVector, title: String, value: String) {
    val spacing = LocalSpacing.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.lg, vertical = spacing.md)
    ) {
        Surface(
            shape = IconContainerShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(spacing.xs)
            )
        }
        Spacer(Modifier.width(spacing.md))
        Text(
            text     = title,
            style    = MaterialTheme.typography.bodyLarge,
            color    = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text  = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Link row — opens URL via Intent. */
@Composable
fun SettingsLinkRow(icon: ImageVector, title: String, url: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
    SettingsItemRow(
        icon    = icon,
        title   = title,
        value   = "↗",
        onClick = {
            val intent = android.content.Intent(
                android.content.Intent.ACTION_VIEW,
                android.net.Uri.parse(url)
            )
            context.startActivity(intent)
        }
    )
}

/** Slider row for enum-mapped values (Shadow, Density). */
@Composable
fun SettingsSliderItem(
    icon: ImageVector,
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    label: String,
    onValueChange: (Float) -> Unit
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.lg, vertical = spacing.sm)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = IconContainerShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(spacing.xs)
                )
            }
            Spacer(Modifier.width(spacing.md))
            Text(
                text     = title,
                style    = MaterialTheme.typography.bodyLarge,
                color    = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Slider(
            value        = value,
            onValueChange= onValueChange,
            valueRange   = valueRange,
            steps        = steps,
            modifier     = Modifier.padding(top = spacing.xs)
        )
    }
}

/**
 * Generic bottom sheet picker for small enums (ThemeMode, StatusBarStyle).
 * Shows each option as a row; selected option has a checkmark.
 * Tapping any row selects it and dismisses immediately.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SimpleEnumPickerSheet(
    title: String,
    options: List<T>,
    selected: T,
    labelOf: (T) -> String,
    onSelect: (T) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = LocalSpacing.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(modifier = Modifier.padding(bottom = spacing.xl)) {
            Text(
                text     = title,
                style    = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = spacing.xl, vertical = spacing.md)
            )
            options.forEach { option ->
                val isSelected = option == selected
                Surface(
                    color = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect(option)
                            onDismiss()
                        }
                        .padding(horizontal = spacing.xl, vertical = spacing.md)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text     = labelOf(option),
                            style    = MaterialTheme.typography.bodyLarge,
                            color    = if (isSelected)
                                           MaterialTheme.colorScheme.onPrimaryContainer
                                       else
                                           MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog for editing the app name.
 * Confirms via [onConfirm] with the trimmed text — blank input falls back
 * to AppConstants.APP_NAME_DEFAULT inside NexusKitDataStore.updateAppName().
 */
@Composable
fun AppNameDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember(currentName) { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("App Name") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                placeholder = { Text("NexusKit") }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(text)
                onDismiss()
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
