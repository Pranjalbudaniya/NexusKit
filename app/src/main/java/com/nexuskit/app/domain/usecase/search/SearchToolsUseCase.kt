package com.nexuskit.app.domain.usecase.search

import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.core.base.SuspendUseCase
import com.nexuskit.app.domain.model.ToolInfo
import com.nexuskit.app.domain.repository.IToolRepository
import javax.inject.Inject

data class SearchToolsParams(val query: String)

/**
 * Simple tool name/description/keyword search.
 * Hidden tools are excluded automatically by the repository.
 *
 * Edge cases handled:
 *   • Blank query → Resource.Success(emptyList()) immediately, no repo call
 *   • Single char → still searches (min length = 1)
 *   • No matches  → Resource.Success(emptyList()), not an error
 */
class SearchToolsUseCase @Inject constructor(
    private val toolRepository: IToolRepository
) : SuspendUseCase<SearchToolsParams, List<ToolInfo>>() {

    override suspend fun execute(params: SearchToolsParams): Resource<List<ToolInfo>> {
        if (params.query.isBlank()) return Resource.Success(emptyList())
        return try {
            Resource.Success(toolRepository.searchTools(params.query))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Search failed", e)
        }
    }
}
