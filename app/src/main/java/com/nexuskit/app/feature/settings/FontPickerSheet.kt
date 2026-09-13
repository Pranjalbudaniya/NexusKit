package com.nexuskit.app.feature.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexuskit.app.domain.model.enums.FontFamily
import com.nexuskit.app.ui.theme.ChipShape
import com.nexuskit.app.ui.theme.LocalSpacing
import com.nexuskit.app.ui.theme.resolveFontFamily

/**
 * Font picker bottom sheet.
 * Shows all 11 fonts with a live preview "AaBbCc 123" rendered in each font.
 * Selected font has a primary-coloured border + checkmark.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontPickerSheet(
    selectedFont: FontFamily,
    onFontSelected: (FontFamily) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = LocalSpacing.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column {
            Text(
                text     = "Font",
                style    = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = spacing.xl, vertical = spacing.md)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.sm),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                items(FontFamily.entries.toList()) { font ->
                    val isSelected = font == selectedFont
                    val family = resolveFontFamily(font)

                    Surface(
                        shape = ChipShape,
                        color = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.xs)
                            .then(
                                if (isSelected) Modifier.border(
                                    1.5.dp, MaterialTheme.colorScheme.primary, ChipShape
                                ) else Modifier
                            )
                            .clickable {
                                onFontSelected(font)
                                onDismiss()
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                horizontal = spacing.lg, vertical = spacing.md
                            )
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text       = font.displayName,
                                    style      = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = family,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color      = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text       = "The quick brown fox jumps over the lazy dog 123",
                                    style      = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = family
                                    ),
                                    color      = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize   = 13.sp,
                                    maxLines   = 1
                                )
                            }
                            if (isSelected) {
                                Spacer(Modifier.width(spacing.sm))
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = spacing.xs)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(spacing.md))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.xl),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("Close") }
            }
            Spacer(Modifier.height(spacing.lg))
        }
    }
}
