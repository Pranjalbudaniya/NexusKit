package com.nexuskit.app.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.ui.theme.LocalSpacing

/**
 * Shown when the user long-presses any tool item (Card, Pill, List, or Icon).
 *
 * Layout:
 *   ┌──────────────────────────────────────┐
 *   │  [shaped icon]  Tool Name            │  ← header row
 *   │                 Description text     │
 *   │                                      │
 *   │  [♡ Favourite]        [→ Use]        │  ← action buttons
 *   └──────────────────────────────────────┘
 *
 * [isFavourited]   reflects current favourite status — drives icon + label.
 * [onFavouriteToggle] called when favourite button is tapped.
 * [onUse]          navigates to the tool screen and dismisses sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolInfoBottomSheet(
    toolInfo: ToolInfo,
    isFavourited: Boolean,
    cardShape: Shape,
    onFavouriteToggle: () -> Unit,
    onUse: () -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = LocalSpacing.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.xl, vertical = spacing.lg)
        ) {

            // ── Header row: icon + name + description ────────────────────────
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Tool icon in card shape
                Surface(
                    shape = cardShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = toolInfo.iconName.toImageVector(),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .padding(spacing.md)
                            .size(28.dp)
                    )
                }

                Spacer(Modifier.width(spacing.lg))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = toolInfo.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (toolInfo.description.isNotBlank()) {
                        Spacer(Modifier.height(spacing.xs))
                        Text(
                            text = toolInfo.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(spacing.xl))

            // ── Action buttons ───────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                // Favourite toggle button
                OutlinedButton(
                    onClick = {
                        onFavouriteToggle()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isFavourited) Icons.Filled.Favorite
                                      else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(spacing.xs))
                    Text(if (isFavourited) "Unfavourite" else "Favourite")
                }

                // Use button — navigates to the tool
                Button(
                    onClick = {
                        onUse()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Use")
                }
            }

            Spacer(Modifier.height(spacing.lg))
        }
    }
}
