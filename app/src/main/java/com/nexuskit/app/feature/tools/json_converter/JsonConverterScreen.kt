package com.nexuskit.app.feature.tools.json_converter

import android.content.Intent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JsonConverterScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: JsonConverterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val modeScrollState = rememberScrollState()

    DrawerNavigation(
        navController = navController,
        currentToolId = "json_converter"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "JSON / CSV / YAML",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                // Mode Selector
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(modeScrollState),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                    ) {
                        ConvertMode.entries.forEach { mode ->
                            FilterChip(
                                selected = uiState.mode == mode,
                                onClick = { viewModel.onModeChanged(mode) },
                                label = { Text(mode.title, style = MaterialTheme.typography.labelMedium) }
                            )
                        }
                    }
                }

                // Options (for CSV modes)
                if (uiState.mode == ConvertMode.JSON_TO_CSV || uiState.mode == ConvertMode.CSV_TO_JSON) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.md, vertical = spacing.sm),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                ) {
                                    Text("Delimiter:", style = MaterialTheme.typography.bodySmall)
                                    listOf(',' to "Comma (,)", ';' to "Semicolon (;)", '\t' to "Tab (⇥)").forEach { (delim, label) ->
                                        FilterChip(
                                            selected = uiState.delimiter == delim,
                                            onClick = { viewModel.onDelimiterChanged(delim) },
                                            label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Input Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(spacing.md)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val inputLabel = when (uiState.mode) {
                                    ConvertMode.JSON_TO_CSV, ConvertMode.JSON_TO_YAML -> "Input JSON"
                                    ConvertMode.CSV_TO_JSON -> "Input CSV"
                                    ConvertMode.YAML_TO_JSON -> "Input YAML"
                                }
                                Text(
                                    text = inputLabel,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Row {
                                    OutlinedButton(
                                        onClick = { viewModel.loadSample() },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Sample", style = MaterialTheme.typography.labelSmall)
                                    }
                                    if (uiState.input.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(spacing.xs))
                                        IconButton(
                                            onClick = { viewModel.clear() },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.sm))

                            OutlinedTextField(
                                value = uiState.input,
                                onValueChange = { viewModel.onInputChanged(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Paste or type content here...",
                                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                                    )
                                },
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                minLines = 5,
                                maxLines = 10
                            )

                            if (uiState.errorMessage != null) {
                                Spacer(modifier = Modifier.height(spacing.xs))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Error,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(spacing.xs))
                                    Text(
                                        text = uiState.errorMessage ?: "",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }

                // Swap Button
                if (uiState.output.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.swap() }
                            ) {
                                Icon(Icons.Default.SwapVert, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(spacing.xs))
                                Text("Swap Input & Output", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }

                // Output Card
                if (uiState.output.isNotEmpty()) {
                    item {
                        val outputLabel = when (uiState.mode) {
                            ConvertMode.JSON_TO_CSV -> "Output CSV"
                            ConvertMode.CSV_TO_JSON, ConvertMode.YAML_TO_JSON -> "Output JSON"
                            ConvertMode.JSON_TO_YAML -> "Output YAML"
                        }
                        val outputScroll = rememberScrollState()

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(spacing.md)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = outputLabel,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Row {
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(uiState.output))
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Copied to clipboard")
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.ContentCopy,
                                                contentDescription = "Copy",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(spacing.xs))
                                        IconButton(
                                            onClick = {
                                                val sendIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, uiState.output)
                                                    type = "text/plain"
                                                }
                                                context.startActivity(Intent.createChooser(sendIntent, "Share Converted Data"))
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Share,
                                                contentDescription = "Share",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(spacing.sm))

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(outputScroll),
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ) {
                                    Text(
                                        text = uiState.output,
                                        modifier = Modifier.padding(spacing.sm),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
