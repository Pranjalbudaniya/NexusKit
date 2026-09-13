package com.nexuskit.app.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.ui.theme.LocalSpacing
import kotlinx.coroutines.delay

/**
 * CARD display mode — square tile with icon centred on top, name below.
 * Name is constrained to the same width as the icon so the tile stays square.
 *
 * Shape and elevation are passed in from the parent (HomeScreen reads them
 * from UserPreferences and resolves them before passing down).
 *
 * [staggerIndex] drives the entrance animation delay (index × STAGGER_DELAY_MS).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ToolCard(
    toolInfo: ToolInfo,
    shape: Shape,
    elevation: Dp,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    staggerIndex: Int = 0
) {
    val spacing = LocalSpacing.current
    val iconSize = 28.dp

    // ── Stagger entrance animation ────────────────────────────────────────────
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(AppConstants.ANIMATION_DURATION_MS),
        label = "toolCardAlpha"
    )
    LaunchedEffect(Unit) {
        delay(staggerIndex * AppConstants.STAGGER_DELAY_MS)
        visible = true
    }

    Surface(
        modifier = modifier
            .heightIn(min = 76.dp, max = 88.dp)
            .alpha(alpha)
            .combinedClickable(onClick = onClick, onLongClick = onLongPress),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = elevation
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.sm, vertical = spacing.xs)
        ) {
            Icon(
                imageVector = toolInfo.iconName.toImageVector(),
                contentDescription = null,
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = toolInfo.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
