package com.nexuskit.app.domain.usecase.tool

import com.nexuskit.app.core.base.NoParamFlowUseCase
import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.domain.model.ToolRegistry
import com.nexuskit.app.domain.repository.IToolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Returns recently opened tools for the side drawer, newest first.
 *
 * Edge cases handled:
 *   • Tool in history but removed from ToolRegistry → filtered out
 *   • Tool in history but currently hidden → still shown in Recent
 *     (Recent in drawer shows tools even if hidden from home screen)
 *   • No history → Success(emptyList())
 */
class GetRecentToolsUseCase @Inject constructor(
    private val toolRepository: IToolRepository
) : NoParamFlowUseCase<List<ToolInfo>>() {

    private companion object {
        const val RECENT_LIMIT = 10
    }

    override fun execute(): Flow<Resource<List<ToolInfo>>> =
        toolRepository.getRecentToolIds(RECENT_LIMIT)
            .map<List<String>, Resource<List<ToolInfo>>> { ids ->
                val tools = ids.mapNotNull { id -> ToolRegistry.getToolById(id) }
                Resource.Success(tools)
            }
            .catch { emit(Resource.Error(it.message ?: "Failed to load recent tools", it)) }
}
