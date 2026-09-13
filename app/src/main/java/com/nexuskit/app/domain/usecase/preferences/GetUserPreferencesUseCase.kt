package com.nexuskit.app.domain.usecase.preferences

import com.nexuskit.app.core.base.NoParamFlowUseCase
import com.nexuskit.app.core.base.Resource
import com.nexuskit.app.domain.model.UserPreferences
import com.nexuskit.app.domain.repository.IPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Exposes the live UserPreferences stream to ViewModels.
 * Re-emits on every preference change.
 */
class GetUserPreferencesUseCase @Inject constructor(
    private val prefsRepository: IPreferencesRepository
) : NoParamFlowUseCase<UserPreferences>() {

    override fun execute(): Flow<Resource<UserPreferences>> =
        prefsRepository.getUserPreferences()
            .map<UserPreferences, Resource<UserPreferences>> { Resource.Success(it) }
            .catch { emit(Resource.Error(it.message ?: "Failed to load preferences", it)) }
}
