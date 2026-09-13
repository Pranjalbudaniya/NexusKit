package com.nexuskit.app.domain.usecase.tool

import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.core.base.SuspendUseCase
import com.nexuskit.app.domain.model.ToolRegistry
import com.nexuskit.app.domain.repository.IPreferencesRepository
import javax.inject.Inject

data class ReorderToolsParams(val categoryId: String, val toolIds: List<String>)

/**
 * Saves the user's custom tool order within a category.
 *
 * Edge cases handled:
 *   • categoryId not in ToolRegistry      → Resource.Error
 *   • toolIds list missing some tools     → appends missing tools at end
 *   • toolIds contains unknown tool IDs   → silently ignored
 *   • toolIds empty                       → clears custom order (reverts to default)
 */
class ReorderToolsInCategoryUseCase @Inject constructor(
    private val prefsRepository: IPreferencesRepository
) : SuspendUseCase<ReorderToolsParams, Unit>() {

    override suspend fun execute(params: ReorderToolsParams): Resource<Unit> {
        if (ToolRegistry.getCategoryById(params.categoryId) == null) {
            return Resource.Error("Category '${params.categoryId}' does not exist.")
        }

        val allToolsInCategory = ToolRegistry.getToolsByCategory(params.categoryId)
            .map { it.id }.toSet()

        // Keep valid IDs, append missing tools at end
        val validOrdered = params.toolIds.filter { it in allToolsInCategory }
        val missing = allToolsInCategory - validOrdered.toSet()
        val finalOrder = validOrdered + missing

        prefsRepository.updateToolOrder(params.categoryId, finalOrder)
        return Resource.Success(Unit)
    }
}
