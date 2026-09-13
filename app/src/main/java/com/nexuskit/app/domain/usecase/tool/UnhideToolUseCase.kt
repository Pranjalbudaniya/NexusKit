package com.nexuskit.app.domain.usecase.tool

import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.core.base.SuspendUseCase
import com.nexuskit.app.domain.model.ToolRegistry
import com.nexuskit.app.domain.repository.IPreferencesRepository
import javax.inject.Inject

data class UnhideToolParams(val toolId: String)

/**
 * Makes a previously hidden tool visible again.
 *
 * Edge cases handled:
 *   • Tool not in ToolRegistry → Resource.Error
 *   • Tool not currently hidden → no-op, Resource.Success (idempotent)
 */
class UnhideToolUseCase @Inject constructor(
    private val prefsRepository: IPreferencesRepository
) : SuspendUseCase<UnhideToolParams, Unit>() {

    override suspend fun execute(params: UnhideToolParams): Resource<Unit> {
        if (ToolRegistry.getToolById(params.toolId) == null) {
            return Resource.Error("Tool '${params.toolId}' does not exist.")
        }
        prefsRepository.unhideTool(params.toolId)
        return Resource.Success(Unit)
    }
}
