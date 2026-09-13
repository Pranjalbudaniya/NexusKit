package com.nexuskit.app.domain.usecase.preferences

import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.core.base.SuspendUseCase
import com.nexuskit.app.domain.model.PreferenceUpdate
import com.nexuskit.app.domain.repository.IPreferencesRepository
import javax.inject.Inject

/**
 * Single entry point for all preference updates from the Settings screen.
 * Dispatches to the correct repository method based on the PreferenceUpdate type.
 *
 * Edge cases handled:
 *   • AppName blank        → trimmed; if still blank, default name restored
 *   • AccentColor with
 *     dynamicColor still on → stores the color but dynamic takes priority in Theme
 *   • GridColumns          → clamped to [GRID_COLUMNS_MIN, GRID_COLUMNS_MAX] by DataStore
 *   • Any exception        → wrapped in Resource.Error, never crashes
 */
class UpdatePreferencesUseCase @Inject constructor(
    private val prefsRepository: IPreferencesRepository
) : SuspendUseCase<PreferenceUpdate, Unit>() {

    override suspend fun execute(params: PreferenceUpdate): Resource<Unit> = try {
        when (params) {
            is PreferenceUpdate.AppName ->
                prefsRepository.updateAppName(params.name.trim())
            is PreferenceUpdate.SetThemeMode ->
                prefsRepository.updateThemeMode(params.mode)
            is PreferenceUpdate.SetDynamicColor ->
                prefsRepository.updateDynamicColorEnabled(params.enabled)
            is PreferenceUpdate.SetAccentColor ->
                prefsRepository.updateAccentColor(params.argb)
            is PreferenceUpdate.SetFontFamily ->
                prefsRepository.updateFontFamily(params.font)
            is PreferenceUpdate.SetCardShape ->
                prefsRepository.updateCardShape(params.shape)
            is PreferenceUpdate.SetFavCardShape ->
                prefsRepository.updateFavCardShape(params.shape)
            is PreferenceUpdate.SetShadowElevation ->
                prefsRepository.updateShadowElevation(params.elevation)
            is PreferenceUpdate.SetSpacingMode ->
                prefsRepository.updateSpacingMode(params.mode)
            is PreferenceUpdate.SetStatusBarStyle ->
                prefsRepository.updateStatusBarStyle(params.style)
            is PreferenceUpdate.SetCategoryDisplayMode ->
                prefsRepository.updateCategoryDisplayMode(params.categoryId, params.mode)
            is PreferenceUpdate.SetCategoryExpanded ->
                prefsRepository.updateCategoryExpanded(params.categoryId, params.expanded)
            is PreferenceUpdate.SetGridColumns ->
                prefsRepository.updateGridColumns(params.categoryId, params.columns)
            is PreferenceUpdate.ResetToDefaults ->
                prefsRepository.resetToDefaults()
            is PreferenceUpdate.CompleteOnboarding ->
                prefsRepository.setOnboardingCompleted()
        }
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Preference update failed", e)
    }
}
