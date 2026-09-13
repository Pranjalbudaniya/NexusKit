package com.nexuskit.app.domain.usecase.tool

import com.nexuskit.app.core.base.NoParamFlowUseCase
import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.domain.repository.IToolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Returns the user's favourites in saved order.
 *
 * Edge cases handled:
 *   • Favourited tool was removed from ToolRegistry → filtered out silently
 *   • Favourited tool was hidden by user → filtered out (hidden overrides favourite)
 *   • Empty favourites list → Success(emptyList()) — not an error
 */
class GetFavoriteToolsUseCase @Inject constructor(
    private val toolRepository: IToolRepository
) : NoParamFlowUseCase<List<ToolInfo>>() {

    override fun execute(): Flow<Resource<List<ToolInfo>>> =
        toolRepository.getFavoriteTools()
            .map<List<ToolInfo>, Resource<List<ToolInfo>>> { Resource.Success(it) }
            .catch { emit(Resource.Error(it.message ?: "Failed to load favourites", it)) }
}
