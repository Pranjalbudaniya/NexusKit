package com.nexuskit.app.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.ui.theme.LocalSpacing
import kotlinx.coroutines.delay

/**
 * ICON_GRID display mode — icon only, no text, shaped container.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ToolIconItem(
    toolInfo: ToolInfo,
    shape: Shape,
    elevation: Dp,
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
        label = "iconItemAlpha"
    )
    LaunchedEffect(Unit) {
        delay(staggerIndex * AppConstants.STAGGER_DELAY_MS)
        visible = true
    }

    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .alpha(alpha)
            .combinedClickable(onClick = onClick, onLongClick = onLongPress),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = elevation
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().padding(spacing.md)
        ) {
            Icon(
                imageVector = toolInfo.iconName.toImageVector(),
                contentDescription = toolInfo.name,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
