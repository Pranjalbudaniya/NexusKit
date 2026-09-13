package com.nexuskit.app.domain.usecase.tool

import com.nexuskit.app.core.base.NoParamFlowUseCase
import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.domain.model.CategoryInfo
import com.nexuskit.app.domain.repository.IToolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Returns all categories with their visible tools as a reactive stream.
 * Categories with zero visible tools are still included so the user can
 * see them and manage hidden tools from Settings.
 *
 * Edge cases handled:
 *   • All tools in a category hidden  → category returned with empty tools list
 *   • ToolRegistry empty              → empty list wrapped in Success (not Error)
 *   • DataStore read error            → Resource.Error emitted downstream
 */
class GetCategoriesUseCase @Inject constructor(
    private val toolRepository: IToolRepository
) : NoParamFlowUseCase<List<CategoryInfo>>() {

    override fun execute(): Flow<Resource<List<CategoryInfo>>> =
        toolRepository.getCategoriesWithTools()
            .map<List<CategoryInfo>, Resource<List<CategoryInfo>>> { Resource.Success(it) }
            .catch { emit(Resource.Error(it.message ?: "Failed to load categories", it)) }
}
