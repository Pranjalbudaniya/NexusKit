package com.nexuskit.app.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleException(throwable)
    }

    protected fun launchSafe(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(exceptionHandler) { block() }
    }

    protected open fun handleException(throwable: Throwable) {
        // Subclasses override to surface errors to UI state
    }
}
