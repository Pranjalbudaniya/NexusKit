package com.nexuskit.app.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexuskit.app.R
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.ui.theme.LocalSpacing

/**
 * The Favourites strip at the top of the home screen.
 * Always expanded — no collapse. Vanishes completely when [tools] is empty.
 *
 * Each item uses [favShape] which can differ from the main card shape.
 * Items are square tiles same size as ToolCard but using the fav shape.
 *
 * [showFirstLaunchHint] shows a one-time hint "Long press any tool to favourite it"
 * the first time the user sees an empty favourites slot. Set to false after user
 * adds their first favourite (persisted in UserPreferences.onboardingCompleted).
 */
@Composable
fun FavouritesRow(
    tools: List<ToolInfo>,
    favShape: Shape,
    elevation: Dp,
    onToolClick: (toolId: String) -> Unit,
    onToolLongPress: (toolInfo: ToolInfo) -> Unit,
    modifier: Modifier = Modifier,
    showFirstLaunchHint: Boolean = false
) {
    val spacing = LocalSpacing.current
    if (tools.isEmpty() && !showFirstLaunchHint) return  // vanish when 0 favs

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.section_favourites),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                start = spacing.lg,
                bottom = spacing.xs
            )
        )

        if (tools.isEmpty()) {
            // One-time hint text
            Text(
                text = stringResource(R.string.favourites_empty_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(
                    horizontal = spacing.lg,
                    vertical = spacing.sm
                )
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                itemsIndexed(tools) { index, tool ->
                    ToolCard(
                        toolInfo = tool,
                        shape = favShape,
                        elevation = elevation,
                        onClick = { onToolClick(tool.id) },
                        onLongPress = { onToolLongPress(tool) },
                        staggerIndex = index,
                        modifier = Modifier.size(80.dp)   // fixed compact size for favs row
                    )
                }
            }
        }
    }
}
