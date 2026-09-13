package com.nexuskit.app.domain.usecase.tool

import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.core.base.SuspendUseCase
import com.nexuskit.app.domain.model.ToolRegistry
import com.nexuskit.app.domain.repository.IPreferencesRepository
import javax.inject.Inject

data class HideToolParams(val toolId: String)

/**
 * Hides a tool from the home screen and search results.
 * Also removes it from favourites if it was favourited.
 *
 * Edge cases handled:
 *   • Tool not in ToolRegistry → Resource.Error
 *   • Tool already hidden      → no-op, Resource.Success (idempotent)
 *   • Tool in favourites       → removed from favourites automatically
 */
class HideToolUseCase @Inject constructor(
    private val prefsRepository: IPreferencesRepository
) : SuspendUseCase<HideToolParams, Unit>() {

    override suspend fun execute(params: HideToolParams): Resource<Unit> {
        val toolId = params.toolId

        if (ToolRegistry.getToolById(toolId) == null) {
            return Resource.Error("Tool '$toolId' does not exist.")
        }

        // Remove from favourites first (hidden tool should not be in favourites)
        prefsRepository.removeFavorite(toolId)
        prefsRepository.hideTool(toolId)

        return Resource.Success(Unit)
    }
}
