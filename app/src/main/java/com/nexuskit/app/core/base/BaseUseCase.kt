package com.nexuskit.app.core.base

import kotlinx.coroutines.flow.Flow

/** Single async call, requires params. */
abstract class SuspendUseCase<in P, out R> {
    abstract suspend fun execute(params: P): Resource<R>
    suspend operator fun invoke(params: P): Resource<R> = execute(params)
}

/** Continuous stream, requires params. */
abstract class FlowUseCase<in P, out R> {
    abstract fun execute(params: P): Flow<Resource<R>>
    operator fun invoke(params: P): Flow<Resource<R>> = execute(params)
}

/** Single async call, no params. */
abstract class NoParamUseCase<out R> {
    abstract suspend fun execute(): Resource<R>
    suspend operator fun invoke(): Resource<R> = execute()
}

/** Continuous stream, no params. */
abstract class NoParamFlowUseCase<out R> {
    abstract fun execute(): Flow<Resource<R>>
    operator fun invoke(): Flow<Resource<R>> = execute()
}
