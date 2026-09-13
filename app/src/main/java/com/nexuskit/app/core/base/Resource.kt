package com.nexuskit.app.core.base

sealed class Resource<out T> {
    data class  Success<T>(val data: T)                                       : Resource<T>()
    data class  Error(val message: String, val throwable: Throwable? = null)  : Resource<Nothing>()
    data object Loading                                                       : Resource<Nothing>()

    val isSuccess get() = this is Success
    val isError   get() = this is Error
    val isLoading get() = this is Loading

    fun getOrNull(): T?        = (this as? Success)?.data
    fun errorOrNull(): String? = (this as? Error)?.message
}
