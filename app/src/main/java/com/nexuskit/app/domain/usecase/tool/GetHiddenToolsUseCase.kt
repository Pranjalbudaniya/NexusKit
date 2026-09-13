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
 * Returns full ToolInfo objects for all hidden tools.
 * Used by the Hidden Tools settings screen.
 *
 * Edge cases handled:
 *   • Stored toolId not in ToolRegistry (tool removed by developer)
 *     → silently filtered out, stale ID cleaned on next unhide/reset
 *   • No hidden tools → Success(emptyList())
 */
class GetHiddenToolsUseCase @Inject constructor(
    private val toolRepository: IToolRepository
) : NoParamFlowUseCase<List<ToolInfo>>() {

    override fun execute(): Flow<Resource<List<ToolInfo>>> =
        toolRepository.getHiddenToolIds()
            .map<Set<String>, Resource<List<ToolInfo>>> { hiddenIds ->
                val tools = hiddenIds
                    .mapNotNull { id -> ToolRegistry.getToolById(id) }
                    .sortedBy { it.name }
                Resource.Success(tools)
            }
            .catch { emit(Resource.Error(it.message ?: "Failed to load hidden tools", it)) }
}
