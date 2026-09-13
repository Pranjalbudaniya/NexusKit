package com.nexuskit.app.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.ui.theme.LocalSpacing
import kotlinx.coroutines.delay

/**
 * LIST display mode — full-width row with icon left, name + description right.
 * No card surface — sits directly on category background.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ToolListItem(
    toolInfo: ToolInfo,
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
        label = "listItemAlpha"
    )
    LaunchedEffect(Unit) {
        delay(staggerIndex * AppConstants.STAGGER_DELAY_MS)
        visible = true
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .combinedClickable(onClick = onClick, onLongClick = onLongPress)
            .padding(horizontal = spacing.lg, vertical = spacing.md)
    ) {
        Icon(
            imageVector = toolInfo.iconName.toImageVector(),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(spacing.lg))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = toolInfo.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (toolInfo.description.isNotBlank()) {
                Text(
                    text = toolInfo.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
