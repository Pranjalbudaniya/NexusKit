package com.nexuskit.app.domain.usecase.tool

import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.core.base.SuspendUseCase
import com.nexuskit.app.core.util.AppConstants
import com.nexuskit.app.domain.model.ToolRegistry
import com.nexuskit.app.domain.repository.IPreferencesRepository
import com.nexuskit.app.domain.repository.IToolRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class ToggleFavoriteParams(val toolId: String)

/**
 * Adds a tool to favourites if not already there; removes it if it is.
 *
 * Edge cases handled:
 *   • Adding when already at max (8) → Resource.Error with user-facing message
 *   • toolId not in ToolRegistry     → Resource.Error (prevents phantom favourites)
 *   • Already favourited             → removes it (toggle)
 *   • Not favourited                 → adds it
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val toolRepository: IToolRepository,
    private val prefsRepository: IPreferencesRepository
) : SuspendUseCase<ToggleFavoriteParams, Unit>() {

    override suspend fun execute(params: ToggleFavoriteParams): Resource<Unit> {
        val toolId = params.toolId

        // Guard: tool must exist in registry
        if (ToolRegistry.getToolById(toolId) == null) {
            return Resource.Error("Tool '$toolId' does not exist in ToolRegistry.")
        }

        val currentFavs = prefsRepository.getUserPreferences().first().favoriteToolIds

        return if (toolId in currentFavs) {
            prefsRepository.removeFavorite(toolId)
            Resource.Success(Unit)
        } else {
            if (currentFavs.size >= AppConstants.FAVORITES_MAX_COUNT) {
                Resource.Error(
                    "Favourites are full (max ${AppConstants.FAVORITES_MAX_COUNT}). " +
                    "Remove one before adding another."
                )
            } else {
                prefsRepository.addFavorite(toolId)
                Resource.Success(Unit)
            }
        }
    }
}
