package com.nexuskit.app.core.util

import com.nexuskit.app.core.base.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** Wrap a Flow<T> into Flow<Resource<T>>, catching errors automatically. */
fun <T> Flow<T>.asResource(): Flow<Resource<T>> = this
    .map<T, Resource<T>> { Resource.Success(it) }
    .catch { emit(Resource.Error(it.message ?: "Unknown error", it)) }

/** Returns true if string is non-blank (valid search input). */
fun String.isValidQuery(): Boolean = this.trim().isNotEmpty()

/**
 * Move an item in a list from [from] index to [to] index.
 * Returns the same list if indices are equal or out of bounds.
 */
fun <T> List<T>.moveItem(from: Int, to: Int): List<T> {
    if (from == to || from < 0 || to < 0 || from >= size || to >= size) return this
    return toMutableList().also { it.add(to, it.removeAt(from)) }
}
