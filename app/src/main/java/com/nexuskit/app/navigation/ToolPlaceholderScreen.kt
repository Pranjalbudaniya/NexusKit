package com.nexuskit.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nexuskit.app.domain.model.ToolRegistry

/**
 * Temporary placeholder shown for tools that are registered in ToolRegistry
 * but whose screen composable has not been built yet.
 *
 * When a tool is built, replace its composable block in NavGraph.kt and
 * delete this call. This file can be deleted once all tools are built.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolPlaceholderScreen(
    toolId: String,
    onBack: () -> Unit
) {
    val tool = ToolRegistry.getToolById(toolId)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tool?.name ?: toolId) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { _ ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = "${tool?.name ?: toolId} coming soon",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
