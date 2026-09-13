package com.nexuskit.app.feature.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.nexuskit.app.domain.model.PreferenceUpdate
import com.nexuskit.app.domain.model.UserPreferences
import com.nexuskit.app.domain.model.enums.CardShapeType
import com.nexuskit.app.ui.theme.LocalSpacing
import com.nexuskit.app.ui.theme.PresetThemes
import com.nexuskit.app.ui.theme.toComposeShape

/**
 * Theme colour picker bottom sheet.
 * Visual style matches the design: blob-shaped swatches, dynamic toggle,
 * checkmark on selected, + button for custom.
 *
 * When dynamic is ON: colour swatches are hidden.
 * When dynamic is OFF: show 8 preset swatches + custom.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerSheet(
    preferences: UserPreferences,
    onUpdate: (PreferenceUpdate) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = LocalSpacing.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val swatch = CardShapeType.BLOB.toComposeShape()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.xl)
        ) {
            // ── Title ──────────────────────────────────────────────────────
            Text(
                text     = "Theme",
                style    = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = spacing.xl, vertical = spacing.md)
            )

            // ── Dynamic Colour toggle ──────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.xl, vertical = spacing.sm)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = "Dynamic Colour",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text  = "Uses your wallpaper colours (Material You)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked         = preferences.dynamicColorEnabled,
                    onCheckedChange = {
                        onUpdate(PreferenceUpdate.SetDynamicColor(it))
                    }
                )
            }

            // ── Colour swatches (hidden when dynamic is ON) ────────────────
            if (!preferences.dynamicColorEnabled) {
                Spacer(Modifier.height(spacing.md))
                Text(
                    text     = "Simple Variants",
                    style    = MaterialTheme.typography.labelMedium,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = spacing.xl)
                )
                Spacer(Modifier.height(spacing.sm))

                LazyRow(
                    contentPadding        = PaddingValues(horizontal = spacing.xl),
                    horizontalArrangement = Arrangement.spacedBy(spacing.md)
                ) {
                    // 8 preset swatches
                    items(PresetThemes.all) { preset ->
                        val isSelected = preferences.accentColorArgb == preset.argb
                        SwatchItem(
                            color      = preset.seed,
                            shape      = swatch,
                            isSelected = isSelected,
                            onClick    = {
                                onUpdate(PreferenceUpdate.SetAccentColor(preset.argb))
                            }
                        )
                    }

                    // + custom swatch button
                    item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(52.dp)
                                .clickable {
                                    // Custom colour picker opens default accent or current accent
                                    onUpdate(PreferenceUpdate.SetAccentColor(preferences.accentColorArgb))
                                }
                        ) {
                            Surface(
                                shape = swatch,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(52.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Custom colour",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(spacing.md)
                                )
                            }
                        }
                    }
                }
            }

            // ── Bottom buttons ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.xl, vertical = spacing.md),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun SwatchItem(
    color: Color,
    shape: Shape,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(52.dp)
            .clickable(onClick = onClick)
    ) {
        Surface(
            shape = shape,
            color = color,
            modifier = Modifier
                .size(52.dp)
                .then(
                    if (isSelected) Modifier.border(
                        2.dp, MaterialTheme.colorScheme.onSurface, shape
                    ) else Modifier
                )
        ) {}
        if (isSelected) {
            Icon(
                Icons.Filled.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
