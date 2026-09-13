package com.nexuskit.app.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexuskit.app.domain.model.enums.SpacingMode

/**
 * Spacing tokens for NexusKit.
 * All spacing values in UI must come from LocalSpacing.current — never hardcoded dp.
 *
 * SpacingMode.multiplier scales all values at once.
 * COMPACT = 0.75×, COMFORTABLE = 1.0×, SPACIOUS = 1.35×
 */
data class NexusKitSpacing(
    val xxs: Dp,   // 2dp base
    val xs: Dp,    // 4dp base
    val sm: Dp,    // 8dp base
    val md: Dp,    // 12dp base
    val lg: Dp,    // 16dp base
    val xl: Dp,    // 24dp base
    val xxl: Dp,   // 32dp base
    val xxxl: Dp   // 48dp base
) {
    companion object {
        fun forMode(mode: SpacingMode): NexusKitSpacing {
            val m = mode.multiplier
            return NexusKitSpacing(
                xxs  = (2  * m).dp,
                xs   = (4  * m).dp,
                sm   = (8  * m).dp,
                md   = (12 * m).dp,
                lg   = (16 * m).dp,
                xl   = (24 * m).dp,
                xxl  = (32 * m).dp,
                xxxl = (48 * m).dp
            )
        }

        val Default = forMode(SpacingMode.COMFORTABLE)
    }
}

val LocalSpacing = compositionLocalOf { NexusKitSpacing.Default }
