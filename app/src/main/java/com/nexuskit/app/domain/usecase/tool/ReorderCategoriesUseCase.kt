package com.nexuskit.app.domain.usecase.tool

import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.core.base.SuspendUseCase
import com.nexuskit.app.domain.model.ToolRegistry
import com.nexuskit.app.domain.repository.IPreferencesRepository
import javax.inject.Inject

data class ReorderCategoriesParams(val categoryIds: List<String>)

/**
 * Saves the user's custom category order.
 *
 * Edge cases handled:
 *   • categoryIds list is empty           → Resource.Error
 *   • categoryIds missing some categories → appends missing ones at end
 *     (prevents categories from disappearing if a new category is added
 *      by developer after user has already saved an order)
 *   • categoryIds has unknown IDs         → silently ignored
 */
class ReorderCategoriesUseCase @Inject constructor(
    private val prefsRepository: IPreferencesRepository
) : SuspendUseCase<ReorderCategoriesParams, Unit>() {

    override suspend fun execute(params: ReorderCategoriesParams): Resource<Unit> {
        if (params.categoryIds.isEmpty()) {
            return Resource.Error("Category order list must not be empty.")
        }

        val allKnownIds = ToolRegistry.allCategories.map { it.id }.toSet()

        // Keep only valid IDs from user input, then append any missing ones
        val validOrdered = params.categoryIds.filter { it in allKnownIds }
        val missing = allKnownIds - validOrdered.toSet()
        val finalOrder = validOrdered + missing.sortedBy { id ->
            ToolRegistry.getCategoryById(id)?.order ?: Int.MAX_VALUE
        }

        prefsRepository.updateCategoryOrder(finalOrder)
        return Resource.Success(Unit)
    }
}
