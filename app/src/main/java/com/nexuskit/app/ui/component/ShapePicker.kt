package com.nexuskit.app.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexuskit.app.R
import com.nexuskit.app.domain.model.enums.CardShapeType
import com.nexuskit.app.ui.theme.LocalSpacing
import com.nexuskit.app.ui.theme.toComposeShape

/**
 * Bottom sheet showing all 53 shapes in a 6-column grid.
 * Selected shape has a primary-coloured border + checkmark overlay.
 * Matches the visual style from the shape picker screenshot.
 *
 * Used by Settings when user changes card shape or fav card shape.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShapePicker(
    selectedShape: CardShapeType,
    onShapeSelected: (CardShapeType) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = LocalSpacing.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Title ──────────────────────────────────────────────────────
            Text(
                text = "Icon Shape",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(
                    horizontal = spacing.xl,
                    vertical = spacing.md
                )
            )

            // ── Shape grid (6 columns, all 53 shapes) ──────────────────────
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                contentPadding = PaddingValues(horizontal = spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                items(CardShapeType.entries.toList()) { shapeType ->
                    ShapePickerCell(
                        shapeType = shapeType,
                        isSelected = shapeType == selectedShape,
                        onClick = {
                            onShapeSelected(shapeType)
                            onDismiss()
                        }
                    )
                }
            }

            Spacer(Modifier.height(spacing.md))

            // ── Close button ───────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.lg, vertical = spacing.md),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.close),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun ShapePickerCell(
    shapeType: CardShapeType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val shape = shapeType.toComposeShape()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        Surface(
            shape = shape,
            color = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .size(44.dp)
                .then(
                    if (isSelected) Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = shape
                    ) else Modifier
                )
        ) {}

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
