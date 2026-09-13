package com.nexuskit.app.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EnhancedEncryption
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Resolves a Material Icons Extended name string to its ImageVector.
 * ToolInfo.iconName is stored as a String in ToolRegistry.
 * This function bridges String → ImageVector at the UI layer only.
 */
fun String.toImageVector(): ImageVector = iconMap[this] ?: Icons.Filled.Apps

val iconMap: Map<String, ImageVector> = mapOf(
    // ── Tools ──────────────────────────────────────────────────────────────
    "Calculate"          to Icons.Filled.Calculate,
    "Percent"            to Icons.Filled.Percent,
    "Numbers"            to Icons.Filled.Numbers,
    "SwapHoriz"          to Icons.Filled.SwapHoriz,
    "QrCode"             to Icons.Filled.QrCode,
    "Password"           to Icons.Filled.Password,
    "Lock"               to Icons.Filled.Lock,
    "EditNote"           to Icons.Filled.EditNote,
    "Note"               to Icons.AutoMirrored.Filled.Note,
    "Timer"              to Icons.Filled.Timer,
    "CameraAlt"          to Icons.Filled.CameraAlt,
    "Link"               to Icons.Filled.Link,
    "TextFields"         to Icons.Filled.TextFields,
    "FitnessCenter"      to Icons.Filled.FitnessCenter,
    "Event"              to Icons.Filled.Event,
    "CalendarMonth"      to Icons.Filled.CalendarMonth,
    "LocalOffer"         to Icons.Filled.LocalOffer,
    "AccountBalance"     to Icons.Filled.AccountBalance,
    "FormatSize"         to Icons.Filled.FormatSize,
    "Fingerprint"        to Icons.Filled.Fingerprint,
    "ColorLens"          to Icons.Filled.ColorLens,
    "Palette"            to Icons.Filled.Palette,
    "EnhancedEncryption" to Icons.Filled.EnhancedEncryption,
    "Public"             to Icons.Filled.Public,
    "FlashlightOn"       to Icons.Filled.FlashlightOn,
    "Code"               to Icons.Filled.Code,
    "Science"            to Icons.Filled.Science,
    "DirectionsRun"      to Icons.Filled.DirectionsRun,
    "TaskAlt"            to Icons.Filled.TaskAlt,
    "AssignmentTurnedIn" to Icons.Filled.AssignmentTurnedIn,
    "CompareArrows"      to Icons.Filled.CompareArrows,

    // ── Categories ─────────────────────────────────────────────────────────
    "Build"              to Icons.Filled.Build,
    "AutoAwesome"        to Icons.Filled.AutoAwesome,
    "MenuBook"           to Icons.AutoMirrored.Filled.MenuBook,
    "Apps"               to Icons.Filled.Apps,

    // ── UI Actions ─────────────────────────────────────────────────────────
    "Settings"           to Icons.Filled.Settings,
    "Search"             to Icons.Filled.Search,
    "Info"               to Icons.Filled.Info,
    "Edit"               to Icons.Filled.Edit,
    "Delete"             to Icons.Filled.Delete,
    "ContentCopy"        to Icons.Filled.ContentCopy,
    "Favorite"           to Icons.Filled.Favorite,
    "FavoriteBorder"     to Icons.Filled.FavoriteBorder,
    "Star"               to Icons.Filled.Star,
    "VisibilityOff"      to Icons.Filled.VisibilityOff,
    "ArrowBack"          to Icons.AutoMirrored.Filled.ArrowBack
)
