package com.nexuskit.app.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.ui.theme.ChipShape
import com.nexuskit.app.ui.theme.LocalSpacing
import kotlinx.coroutines.delay

/**
 * PILL display mode — compact chip with a tiny circular icon + name text.
 * Shape param applies to the pill container itself (not the icon circle).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ToolPill(
    toolInfo: ToolInfo,
    shape: Shape = ChipShape,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    staggerIndex: Int = 0
) {
    val spacing = LocalSpacing.current

    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(AppConstants.ANIMATION_DURATION_MS),
        label = "pillAlpha"
    )
    LaunchedEffect(Unit) {
        delay(staggerIndex * AppConstants.STAGGER_DELAY_MS)
        visible = true
    }

    Surface(
        modifier = modifier
            .alpha(alpha)
            .combinedClickable(onClick = onClick, onLongClick = onLongPress),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.xs)
        ) {
            // Tiny circular icon container
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = toolInfo.iconName.toImageVector(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .padding(spacing.xxs)
                        .size(14.dp)
                )
            }
            Spacer(Modifier.width(spacing.xs))
            Text(
                text = toolInfo.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
