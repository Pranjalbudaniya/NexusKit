package com.nexuskit.app.domain.model

import com.nexuskit.app.domain.model.enums.CardShapeType
import com.nexuskit.app.domain.model.enums.DisplayMode
import com.nexuskit.app.domain.model.enums.FontFamily
import com.nexuskit.app.domain.model.enums.ShadowElevation
import com.nexuskit.app.domain.model.enums.SpacingMode
import com.nexuskit.app.domain.model.enums.StatusBarStyle
import com.nexuskit.app.domain.model.enums.ThemeMode

/**
 * Sealed class representing every possible preference update action.
 * Passed to UpdatePreferencesUseCase as the params argument.
 * Settings ViewModel creates one of these for each user action.
 */
sealed class PreferenceUpdate {
    data class  AppName(val name: String)                                         : PreferenceUpdate()
    data class  SetThemeMode(val mode: ThemeMode)                                 : PreferenceUpdate()
    data class  SetDynamicColor(val enabled: Boolean)                             : PreferenceUpdate()
    /** null = revert to Material You dynamic color */
    data class  SetAccentColor(val argb: Long?)                                   : PreferenceUpdate()
    data class  SetFontFamily(val font: FontFamily)                               : PreferenceUpdate()
    data class  SetCardShape(val shape: CardShapeType)                            : PreferenceUpdate()
    data class  SetFavCardShape(val shape: CardShapeType)                         : PreferenceUpdate()
    data class  SetShadowElevation(val elevation: ShadowElevation)                : PreferenceUpdate()
    data class  SetSpacingMode(val mode: SpacingMode)                             : PreferenceUpdate()
    data class  SetStatusBarStyle(val style: StatusBarStyle)                      : PreferenceUpdate()
    data class  SetCategoryDisplayMode(val categoryId: String, val mode: DisplayMode) : PreferenceUpdate()
    data class  SetCategoryExpanded(val categoryId: String, val expanded: Boolean)    : PreferenceUpdate()
    data class  SetGridColumns(val categoryId: String, val columns: Int)          : PreferenceUpdate()
    data object ResetToDefaults                                                   : PreferenceUpdate()
    data object CompleteOnboarding                                                : PreferenceUpdate()
}
