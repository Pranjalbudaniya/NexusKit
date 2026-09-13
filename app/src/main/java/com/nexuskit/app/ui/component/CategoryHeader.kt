package com.nexuskit.app.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.domain.model.CategoryInfo
import com.nexuskit.app.ui.theme.IconContainerShape
import com.nexuskit.app.ui.theme.LocalSpacing

/**
 * Expandable/collapsible category section.
 *
 * Layout:
 *   ┌─ subtle Surface card ───────────────────────────────────────┐
 *   │  [Icon]  Category Name                           [chevron]  │  ← header (always visible)
 *   │                                                             │
 *   │  tool items appear here when expanded                       │  ← animated content
 *   └─────────────────────────────────────────────────────────────┘
 *
 * The chevron rotates 180° smoothly on expand/collapse.
 * Content fades + slides in/out vertically.
 */
@Composable
fun CategoryHeader(
    categoryInfo: CategoryInfo,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val spacing = LocalSpacing.current
    val chevronRotation by animateFloatAsState(
        targetValue = if (categoryInfo.isExpanded) 180f else 0f,
        animationSpec = tween(AppConstants.ANIMATION_DURATION_MS),
        label = "chevronRotation"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(tween(AppConstants.ANIMATION_DURATION_MS)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            // ── Header row ──────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = spacing.lg, vertical = spacing.md)
            ) {
                // Category icon in small container
                Surface(
                    shape = IconContainerShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = categoryInfo.iconName.toImageVector(),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(spacing.xs)
                    )
                }
                Spacer(Modifier.width(spacing.md))
                Text(
                    text = categoryInfo.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = if (categoryInfo.isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(chevronRotation)
                )
            }

            // ── Expanded content ─────────────────────────────────────────────
            AnimatedVisibility(
                visible = categoryInfo.isExpanded,
                enter = fadeIn(tween(AppConstants.ANIMATION_DURATION_MS)) +
                        expandVertically(tween(AppConstants.ANIMATION_DURATION_MS)),
                exit  = fadeOut(tween(AppConstants.ANIMATION_DURATION_MS)) +
                        shrinkVertically(tween(AppConstants.ANIMATION_DURATION_MS))
            ) {
                Column(modifier = Modifier.padding(bottom = spacing.sm)) {
                    content()
                }
            }
        }
    }
}
