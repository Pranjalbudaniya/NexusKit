package com.nexuskit.app.domain.model

/**
 * Represents a single tool registered in the ToolRegistry.
 *
 * [id]             Unique identifier, snake_case (e.g. "qr_generator").
 *                  Must match the route segment in NavRoutes (Spec 07).
 *
 * [iconName]       Name of a Material Icons Extended icon as a String
 *                  (e.g. "Calculate", "QrCode").
 *                  Resolved to ImageVector at the UI layer only — see
 *                  ShapeResolver.kt (Spec 08). Never import Compose here.
 *
 * [categoryId]     Must match a CategoryInfo.id in ToolRegistry.allCategories.
 *
 * [route]          Full navigation route string used by NavGraph (Spec 07).
 *                  Format: "tool/{id}" — e.g. "tool/calculator".
 *
 * [isAvailable]    Set to false for placeholder/future tools not yet built.
 *                  Hidden from home screen when false.
 *
 * [searchKeywords] Extra words that help surface this tool in search beyond
 *                  its name and description.
 */
data class ToolInfo(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val categoryId: String,
    val route: String,
    val isAvailable: Boolean = true,
    val searchKeywords: List<String> = emptyList()
)
