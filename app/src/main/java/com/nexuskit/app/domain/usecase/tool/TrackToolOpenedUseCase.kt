package com.nexuskit.app.domain.usecase.tool

import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.core.base.SuspendUseCase
import com.nexuskit.app.domain.repository.IToolRepository
import javax.inject.Inject

data class TrackToolParams(val toolId: String)

/**
 * Records that a tool was opened. Used to populate the side drawer Recent list.
 * Fire-and-forget — called from ViewModel when user navigates to a tool.
 *
 * Edge cases handled:
 *   • DB write fails → Resource.Error (caller can safely ignore this)
 *   • Empty toolId   → Resource.Error
 */
class TrackToolOpenedUseCase @Inject constructor(
    private val toolRepository: IToolRepository
) : SuspendUseCase<TrackToolParams, Unit>() {

    override suspend fun execute(params: TrackToolParams): Resource<Unit> {
        if (params.toolId.isBlank()) return Resource.Error("toolId must not be blank.")
        return try {
            toolRepository.trackToolOpened(params.toolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to track tool open", e)
        }
    }
}
