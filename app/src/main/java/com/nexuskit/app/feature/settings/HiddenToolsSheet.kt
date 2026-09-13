package com.nexuskit.app.feature.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.domain.model.ToolRegistry
import com.nexuskit.app.ui.component.toImageVector
import com.nexuskit.app.ui.theme.LocalSpacing

/**
 * Bottom sheet listing hidden tools grouped by category.
 * Each category is an expandable card. Each tool has a toggle to unhide it.
 *
 * [hiddenTools] list of hidden ToolInfo objects (from GetHiddenToolsUseCase).
 * [onUnhide]   called with toolId when user toggles a tool back to visible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenToolsSheet(
    hiddenTools: List<ToolInfo>,
    onUnhide: (toolId: String) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = LocalSpacing.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // Which category sections are expanded
    val expanded = remember { mutableStateMapOf<String, Boolean>() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text     = "Hidden Tools",
                style    = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = spacing.xl, vertical = spacing.md)
            )

            if (hiddenTools.isEmpty()) {
                Text(
                    text     = "No hidden tools.",
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = spacing.xl, vertical = spacing.lg)
                )
            } else {
                // Group hidden tools by category
                val grouped = hiddenTools
                    .groupBy { it.categoryId }
                    .mapKeys { (catId, _) ->
                        ToolRegistry.getCategoryById(catId)?.name ?: catId
                    }

                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                    grouped.forEach { (categoryName, tools) ->
                        val isExpanded = expanded[categoryName] != false

                        item(key = "cat_$categoryName") {
                            // Expandable category header
                            val chevron by animateFloatAsState(
                                targetValue = if (isExpanded) 180f else 0f,
                                animationSpec = tween(AppConstants.ANIMATION_DURATION_MS),
                                label = "chev"
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize()
                                    .clickable { expanded[categoryName] = !isExpanded }
                                    .padding(horizontal = spacing.xl, vertical = spacing.md)
                            ) {
                                Text(
                                    text     = categoryName,
                                    style    = MaterialTheme.typography.titleSmall,
                                    color    = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Filled.ExpandMore,
                                    contentDescription = null,
                                    tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp).rotate(chevron)
                                )
                            }
                        }

                        if (isExpanded) {
                            items(tools, key = { it.id }) { tool ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = spacing.xl, vertical = spacing.sm)
                                ) {
                                    Icon(
                                        imageVector = tool.iconName.toImageVector(),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(Modifier.width(spacing.md))
                                    Text(
                                        text     = tool.name,
                                        style    = MaterialTheme.typography.bodyLarge,
                                        color    = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    // Toggle: currently OFF (hidden). Switching ON = unhide.
                                    Switch(
                                        checked         = false,
                                        onCheckedChange = { onUnhide(tool.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(spacing.md))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.xl, vertical = spacing.md),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        }
    }
}
