package com.nexuskit.app.ui.theme

// Shape tokens are resolved dynamically from UserPreferences via ShapeResolver.kt.
// This file exists as a thin re-export for any composable that needs
// fixed semantic shape tokens (e.g. bottom sheets, dialogs, chips).
//
// All tool card / icon shapes → use CardShapeType.toComposeShape() from ShapeResolver.kt
// All other fixed shapes     → use MaterialTheme.shapes.* from M3

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/** Bottom sheet drag handle area and container. */
val BottomSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

/** Chips, tags, and badge containers. */
val ChipShape = RoundedCornerShape(8.dp)

/** Search bar container. */
val SearchBarShape = RoundedCornerShape(16.dp)

/** Small icon containers (e.g. settings item icon backgrounds). */
val IconContainerShape = RoundedCornerShape(12.dp)
