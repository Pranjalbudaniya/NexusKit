package com.nexuskit.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.nexuskit.app.feature.tools.markdown_editor.MarkdownDocumentHolder
import com.nexuskit.app.feature.tools.markdown_editor.MarkdownImportedDocument
import com.nexuskit.app.navigation.NavGraph
import com.nexuskit.app.navigation.NavRoutes
import com.nexuskit.app.ui.theme.NexusKitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val EXTRA_TOOL_ROUTE = "EXTRA_TOOL_ROUTE"
    }

    private var targetRoute by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIncomingIntent(intent)

        setContent {
            NexusKitTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    LaunchedEffect(targetRoute) {
                        targetRoute?.let { route ->
                            if (navController.currentDestination?.route != route) {
                                navController.navigate(route) {
                                    launchSingleTop = true
                                }
                            }
                        }
                    }

                    NavGraph(
                        navController = navController,
                        initialToolRoute = targetRoute
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return

        val action = intent.action
        val uri = intent.data

        if ((action == Intent.ACTION_VIEW || action == Intent.ACTION_EDIT) && uri != null) {
            val (fileName, content) = readTextFromUri(uri)
            if (content != null) {
                MarkdownDocumentHolder.setPendingDocument(
                    MarkdownImportedDocument(
                        fileName = fileName,
                        content = content,
                        uri = uri
                    )
                )
                targetRoute = NavRoutes.TOOL_MARKDOWN_EDITOR
                return
            }
        }

        intent.getStringExtra(EXTRA_TOOL_ROUTE)?.let { route ->
            targetRoute = route
        }
    }

    private fun readTextFromUri(uri: Uri): Pair<String, String?> {
        var name = "Document.md"
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    val queriedName = cursor.getString(nameIndex)
                    if (!queriedName.isNullOrBlank()) {
                        name = queriedName
                    }
                }
            }
        } catch (_: Exception) {
            name = uri.lastPathSegment?.substringAfterLast('/') ?: "Document.md"
        }

        val content = try {
            contentResolver.openInputStream(uri)?.use { stream ->
                stream.bufferedReader(Charsets.UTF_8).readText()
            }
        } catch (_: Exception) {
            null
        }

        return Pair(name, content)
    }
}
