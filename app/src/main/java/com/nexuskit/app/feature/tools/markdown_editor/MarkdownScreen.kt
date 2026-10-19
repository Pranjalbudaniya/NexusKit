package com.nexuskit.app.feature.tools.markdown_editor

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: MarkdownViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var showMenu by remember { mutableStateOf(false) }

    // File Pickers
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.openDocumentFromUri(context, it) }
    }

    val saveDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/markdown")
    ) { uri: Uri? ->
        uri?.let { viewModel.saveDocumentToUri(context, it) }
    }

    // Show toast messages from ViewModel
    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    // Intercept back button: close drawer first, then search, then navigate back
    BackHandler(enabled = uiState.isOutlineOpen || uiState.isSearchOpen) {
        when {
            uiState.isOutlineOpen -> viewModel.toggleOutline(false)
            uiState.isSearchOpen -> viewModel.toggleSearch(false)
        }
    }

    val shareFile: (File, String) -> Unit = { file, mimeType ->
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Document"))
    }

    DrawerNavigation(
        navController = navController,
        currentToolId = "markdown_editor"
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    Column {
                        TopAppBar(
                            title = {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.xxs)
                                    ) {
                                        Text(
                                            text = uiState.documentTitle,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (uiState.isDirty) {
                                            Surface(
                                                shape = CircleShape,
                                                color = MaterialTheme.colorScheme.tertiary,
                                                modifier = Modifier.size(7.dp)
                                            ) {}
                                        }
                                    }
                                    Text(
                                        text = "${uiState.stats.words} words • ${uiState.stats.lines} lines" +
                                                if (uiState.isDirty) " (Modified)" else "",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = onBack) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            },
                            actions = {
                                IconButton(onClick = { viewModel.toggleSearch() }) {
                                    Icon(
                                        Icons.Filled.Search,
                                        contentDescription = "Search",
                                        tint = if (uiState.isSearchOpen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(onClick = { viewModel.toggleOutline() }) {
                                    Icon(
                                        Icons.Filled.AccountTree,
                                        contentDescription = "Document Outline",
                                        tint = if (uiState.isOutlineOpen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(Icons.Filled.MoreVert, contentDescription = "More Options")
                                }

                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Open File...") },
                                        leadingIcon = { Icon(Icons.Filled.FileOpen, contentDescription = null) },
                                        onClick = {
                                            showMenu = false
                                            openDocumentLauncher.launch(arrayOf("text/*", "application/octet-stream"))
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Save As...") },
                                        leadingIcon = { Icon(Icons.Filled.Save, contentDescription = null) },
                                        onClick = {
                                            showMenu = false
                                            val defaultName = if (uiState.documentTitle.endsWith(".md")) {
                                                uiState.documentTitle
                                            } else {
                                                "${uiState.documentTitle}.md"
                                            }
                                            saveDocumentLauncher.launch(defaultName)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Export PDF") },
                                        leadingIcon = { Icon(Icons.Filled.PictureAsPdf, contentDescription = null) },
                                        onClick = {
                                            showMenu = false
                                            viewModel.exportPdf { file -> shareFile(file, "application/pdf") }
                                        },
                                        enabled = uiState.markdownText.isNotBlank()
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Export HTML") },
                                        leadingIcon = { Icon(Icons.Filled.Share, contentDescription = null) },
                                        onClick = {
                                            showMenu = false
                                            viewModel.exportHtml { file -> shareFile(file, "text/html") }
                                        },
                                        enabled = uiState.markdownText.isNotBlank()
                                    )
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = { Text("Copy Markdown") },
                                        leadingIcon = { Icon(Icons.Filled.ContentCopy, contentDescription = null) },
                                        onClick = {
                                            showMenu = false
                                            clipboard.setText(AnnotatedString(uiState.markdownText))
                                            Toast.makeText(context, "Markdown copied to clipboard", Toast.LENGTH_SHORT).show()
                                        },
                                        enabled = uiState.markdownText.isNotBlank()
                                    )
                                    DropdownMenuItem(
                                        text = { Text("New Document") },
                                        leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null) },
                                        onClick = {
                                            showMenu = false
                                            if (uiState.isDirty && uiState.markdownText.isNotBlank()) {
                                                viewModel.setShowNewDocumentConfirmDialog(true)
                                            } else {
                                                viewModel.newDocument()
                                            }
                                        }
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        )

                        // Search & Replace In-Editor Bar
                        AnimatedVisibility(
                            visible = uiState.isSearchOpen,
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = spacing.md, vertical = spacing.xs),
                                    verticalArrangement = Arrangement.spacedBy(spacing.xxs)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                    ) {
                                        OutlinedTextField(
                                            value = uiState.searchQuery,
                                            onValueChange = viewModel::onSearchQueryChanged,
                                            placeholder = { Text("Find...") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            textStyle = MaterialTheme.typography.bodySmall,
                                            trailingIcon = {
                                                if (uiState.searchQuery.isNotEmpty()) {
                                                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                                        Icon(Icons.Filled.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                                    }
                                                }
                                            }
                                        )

                                        Text(
                                            text = if (uiState.searchMatches.isNotEmpty()) {
                                                "${uiState.currentMatchIndex + 1}/${uiState.searchMatches.size}"
                                            } else if (uiState.searchQuery.isNotEmpty()) {
                                                "0/0"
                                            } else "",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        IconButton(
                                            onClick = viewModel::previousMatch,
                                            enabled = uiState.searchMatches.isNotEmpty()
                                        ) {
                                            Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Previous Match")
                                        }
                                        IconButton(
                                            onClick = viewModel::nextMatch,
                                            enabled = uiState.searchMatches.isNotEmpty()
                                        ) {
                                            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Next Match")
                                        }
                                        IconButton(onClick = { viewModel.toggleSearch(false) }) {
                                            Icon(Icons.Filled.Close, contentDescription = "Close Search")
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                    ) {
                                        OutlinedTextField(
                                            value = uiState.replaceQuery,
                                            onValueChange = viewModel::onReplaceQueryChanged,
                                            placeholder = { Text("Replace with...") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            textStyle = MaterialTheme.typography.bodySmall
                                        )

                                        OutlinedButton(
                                            onClick = viewModel::replaceCurrentMatch,
                                            enabled = uiState.searchMatches.isNotEmpty(),
                                            contentPadding = PaddingValues(horizontal = spacing.sm, vertical = 0.dp)
                                        ) {
                                            Text("Replace", style = MaterialTheme.typography.labelSmall)
                                        }
                                        FilledTonalButton(
                                            onClick = viewModel::replaceAllMatches,
                                            enabled = uiState.searchMatches.isNotEmpty(),
                                            contentPadding = PaddingValues(horizontal = spacing.sm, vertical = 0.dp)
                                        ) {
                                            Text("All", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Tab Selection Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.md, vertical = spacing.xs),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                    ) {
                        FilterChip(
                            selected = uiState.selectedTab == MarkdownTab.EDIT,
                            onClick = { viewModel.onTabSelected(MarkdownTab.EDIT) },
                            label = { Text("Editor") },
                            leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        FilterChip(
                            selected = uiState.selectedTab == MarkdownTab.PREVIEW,
                            onClick = { viewModel.onTabSelected(MarkdownTab.PREVIEW) },
                            label = { Text("Live Preview") },
                            leadingIcon = { Icon(Icons.Filled.Visibility, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }

                    if (uiState.selectedTab == MarkdownTab.EDIT) {
                        // Quick Format & Action Toolbar
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = spacing.sm, vertical = spacing.xxs),
                                horizontalArrangement = Arrangement.spacedBy(spacing.xxs),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Undo / Redo
                                IconButton(
                                    onClick = viewModel::undo,
                                    enabled = uiState.canUndo
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Undo,
                                        contentDescription = "Undo",
                                        tint = if (uiState.canUndo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    )
                                }
                                IconButton(
                                    onClick = viewModel::redo,
                                    enabled = uiState.canRedo
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Redo,
                                        contentDescription = "Redo",
                                        tint = if (uiState.canRedo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    )
                                }

                                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = spacing.xxs))

                                IconButton(onClick = { viewModel.insertFormatting("# ", "") }) {
                                    Icon(Icons.Filled.Title, contentDescription = "Header 1")
                                }
                                IconButton(onClick = { viewModel.insertFormatting("## ", "") }) {
                                    Text("H2", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                }
                                IconButton(onClick = { viewModel.insertFormatting("### ", "") }) {
                                    Text("H3", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                }
                                IconButton(onClick = { viewModel.insertFormatting("**", "**") }) {
                                    Icon(Icons.Filled.FormatBold, contentDescription = "Bold")
                                }
                                IconButton(onClick = { viewModel.insertFormatting("*", "*") }) {
                                    Icon(Icons.Filled.FormatItalic, contentDescription = "Italic")
                                }
                                IconButton(onClick = { viewModel.insertFormatting("~~", "~~") }) {
                                    Icon(Icons.Filled.FormatStrikethrough, contentDescription = "Strikethrough")
                                }
                                IconButton(onClick = { viewModel.insertFormatting("`", "`") }) {
                                    Icon(Icons.Filled.Code, contentDescription = "Inline Code")
                                }
                                IconButton(onClick = { viewModel.insertFormatting("```\n", "\n```") }) {
                                    Text("{ }", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                }
                                IconButton(onClick = { viewModel.insertFormatting("- ", "") }) {
                                    Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = "Bullet List")
                                }
                                IconButton(onClick = { viewModel.insertFormatting("1. ", "") }) {
                                    Icon(Icons.Filled.FormatListNumbered, contentDescription = "Numbered List")
                                }
                                IconButton(onClick = { viewModel.insertFormatting("- [ ] ", "") }) {
                                    Icon(Icons.Filled.Checklist, contentDescription = "Checklist")
                                }
                                IconButton(onClick = { viewModel.insertFormatting("> ", "") }) {
                                    Icon(Icons.Filled.FormatQuote, contentDescription = "Quote")
                                }
                                IconButton(onClick = { viewModel.setShowTableDialog(true) }) {
                                    Icon(Icons.Filled.TableChart, contentDescription = "Insert Table")
                                }
                                IconButton(onClick = { viewModel.setShowLinkDialog(true) }) {
                                    Icon(Icons.Filled.Link, contentDescription = "Insert Link")
                                }
                                IconButton(onClick = { viewModel.insertFormatting("\n---\n", "") }) {
                                    Icon(Icons.Filled.HorizontalRule, contentDescription = "Divider")
                                }
                            }
                        }

                        // Markdown Input Area
                        OutlinedTextField(
                            value = uiState.markdownText,
                            onValueChange = viewModel::onTextChanged,
                            placeholder = {
                                Text(
                                    text = "Type or paste Markdown here...\nTap outline icon (top right) to inspect document structure.",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = spacing.md, vertical = spacing.xs),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                            )
                        )

                        // Statistics Status Bar
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.md, vertical = spacing.sm),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${uiState.stats.words} Words • ${uiState.stats.characters} Chars • ${uiState.stats.lines} Lines",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                                ) {
                                    if (uiState.stats.headingsCount > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.clickable { viewModel.toggleOutline(true) }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = spacing.xs, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Icon(Icons.Filled.AccountTree, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                                Text(
                                                    "${uiState.stats.headingsCount} Headings",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    }

                                    if (uiState.markdownText.isNotEmpty()) {
                                        Text(
                                            text = "~${uiState.stats.readingTimeMinutes} min read",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // LIVE PREVIEW TAB
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = spacing.md),
                            contentPadding = PaddingValues(vertical = spacing.md),
                            verticalArrangement = Arrangement.spacedBy(spacing.sm)
                        ) {
                            if (uiState.markdownText.isBlank()) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = MaterialTheme.shapes.large,
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(spacing.xl),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(spacing.sm)
                                        ) {
                                            Icon(Icons.Filled.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                                            Text("Document is Empty", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                            Text("Switch back to the Editor tab to write notes or open a markdown file.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            } else {
                                val lines = uiState.markdownText.lines()
                                var inCode = false
                                val codeBuffer = StringBuilder()

                                for (index in lines.indices) {
                                    val line = lines[index]
                                    val trimmed = line.trim()
                                    val lineNumber = index + 1

                                    if (trimmed.startsWith("```")) {
                                        if (inCode) {
                                            val codeText = codeBuffer.toString()
                                            codeBuffer.clear()
                                            item {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(modifier = Modifier.padding(spacing.sm)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.End
                                                        ) {
                                                            TextButton(
                                                                onClick = {
                                                                    clipboard.setText(AnnotatedString(codeText))
                                                                    Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                                                                },
                                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                                            ) {
                                                                Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                                                                Spacer(Modifier.width(4.dp))
                                                                Text("Copy", style = MaterialTheme.typography.labelSmall)
                                                            }
                                                        }
                                                        Text(
                                                            text = codeText,
                                                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                                            modifier = Modifier.padding(horizontal = spacing.xs)
                                                        )
                                                    }
                                                }
                                            }
                                            inCode = false
                                        } else {
                                            inCode = true
                                        }
                                    } else if (inCode) {
                                        codeBuffer.append(line).append("\n")
                                    } else if (trimmed.startsWith("# ")) {
                                        item {
                                            Text(
                                                text = trimmed.removePrefix("# "),
                                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                                                modifier = Modifier.padding(top = spacing.sm)
                                            )
                                            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                        }
                                    } else if (trimmed.startsWith("## ")) {
                                        item {
                                            Text(
                                                text = trimmed.removePrefix("## "),
                                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                                modifier = Modifier.padding(top = spacing.xs)
                                            )
                                        }
                                    } else if (trimmed.startsWith("### ")) {
                                        item {
                                            Text(
                                                text = trimmed.removePrefix("### "),
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                            )
                                        }
                                    } else if (trimmed.startsWith("#### ") || trimmed.startsWith("##### ") || trimmed.startsWith("###### ")) {
                                        val headingText = trimmed.replace(Regex("^#{4,6}\\s+"), "")
                                        item {
                                            Text(
                                                text = headingText,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium)
                                            )
                                        }
                                    } else if (trimmed.startsWith("- [ ] ") || trimmed.startsWith("- [x] ") ||
                                        trimmed.startsWith("- [X] ") || trimmed.startsWith("* [ ] ") ||
                                        trimmed.startsWith("* [x] ")
                                    ) {
                                        val isChecked = trimmed.contains("[x]", ignoreCase = true)
                                        val taskText = trimmed.replace(Regex("^[-*]\\s+\\[[ xX]\\]\\s*"), "")
                                        item {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { viewModel.toggleChecklistAtLine(lineNumber) }
                                                    .padding(vertical = spacing.xxs),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = isChecked,
                                                    onCheckedChange = { viewModel.toggleChecklistAtLine(lineNumber) }
                                                )
                                                Spacer(Modifier.width(spacing.xs))
                                                Text(
                                                    text = taskText,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontStyle = if (isChecked) FontStyle.Italic else FontStyle.Normal
                                                    ),
                                                    color = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    } else if (trimmed.startsWith("> ")) {
                                        item {
                                            Surface(
                                                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp, bottomStart = 0.dp),
                                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = spacing.xxs)
                                            ) {
                                                Row(modifier = Modifier.padding(spacing.sm)) {
                                                    Box(
                                                        modifier = Modifier
                                                            .width(4.dp)
                                                            .height(24.dp)
                                                            .clip(CircleShape)
                                                            .background(MaterialTheme.colorScheme.primary)
                                                    )
                                                    Spacer(Modifier.width(spacing.sm))
                                                    Text(
                                                        text = trimmed.removePrefix("> "),
                                                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
                                                    )
                                                }
                                            }
                                        }
                                    } else if (trimmed.startsWith("- ") || trimmed.startsWith("* ")) {
                                        item {
                                            Row(
                                                modifier = Modifier.padding(start = spacing.sm, top = spacing.xxs),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                            ) {
                                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary))
                                                Text(trimmed.substring(2), style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                    } else if (trimmed.matches(Regex("^\\d+\\.\\s+.*"))) {
                                        val num = trimmed.substringBefore('.')
                                        val content = trimmed.substringAfter(". ")
                                        item {
                                            Row(
                                                modifier = Modifier.padding(start = spacing.sm, top = spacing.xxs),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                            ) {
                                                Text("$num.", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                                                Text(content, style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                    } else if (trimmed == "---" || trimmed == "***") {
                                        item {
                                            HorizontalDivider(modifier = Modifier.padding(vertical = spacing.xs))
                                        }
                                    } else if (trimmed.startsWith("|") && trimmed.endsWith("|")) {
                                        // Render single table row or table block
                                        val cells = trimmed.trim('|').split('|').map { it.trim() }
                                        val isSep = cells.all { it.all { ch -> ch == '-' || ch == ':' || ch == ' ' } }
                                        if (!isSep) {
                                            item {
                                                Surface(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = spacing.xs, horizontal = spacing.sm),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        cells.forEach { cell ->
                                                            Text(
                                                                text = cell,
                                                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                                                modifier = Modifier.weight(1f, fill = false)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else if (trimmed.isNotEmpty()) {
                                        item {
                                            Text(trimmed, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── RIGHT-SIDE SLIDE-OUT OUTLINE DRAWER ───────────────────────────
            // Semi-transparent Backdrop
            AnimatedVisibility(
                visible = uiState.isOutlineOpen,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { viewModel.toggleOutline(false) }
                        )
                )
            }

            // Slide-out Drawer Panel from Right
            AnimatedVisibility(
                visible = uiState.isOutlineOpen,
                enter = slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
                ),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(min = 280.dp, max = 340.dp)
                        .fillMaxWidth(0.85f),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    tonalElevation = 8.dp,
                    shadowElevation = 16.dp,
                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = spacing.md)
                    ) {
                        // Drawer Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = spacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Document Structure",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${uiState.outline.totalCount} Headings in hierarchy",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = viewModel::expandAllHeadings) {
                                    Icon(Icons.Filled.UnfoldMore, contentDescription = "Expand All", modifier = Modifier.size(20.dp))
                                }
                                IconButton(onClick = viewModel::collapseAllHeadings) {
                                    Icon(Icons.Filled.UnfoldLess, contentDescription = "Collapse All", modifier = Modifier.size(20.dp))
                                }
                                IconButton(onClick = { viewModel.toggleOutline(false) }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Close")
                                }
                            }
                        }

                        // Search filter inside outline
                        if (uiState.outline.totalCount > 4) {
                            OutlinedTextField(
                                value = uiState.outlineFilterQuery,
                                onValueChange = viewModel::onOutlineFilterChanged,
                                placeholder = { Text("Filter headings...", style = MaterialTheme.typography.bodySmall) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.md, vertical = spacing.xs),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall,
                                trailingIcon = {
                                    if (uiState.outlineFilterQuery.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.onOutlineFilterChanged("") }) {
                                            Icon(Icons.Filled.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(top = spacing.xs))

                        // Headings Tree List
                        if (uiState.outline.roots.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(spacing.xl),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(spacing.sm)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(56.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.AccountTree,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(14.dp)
                                        )
                                    }
                                    Text(
                                        text = "No Headings Found",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Add # H1 or ## H2 to your markdown text to view hierarchical structure here.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentPadding = PaddingValues(horizontal = spacing.sm, vertical = spacing.xs),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                items(uiState.outline.roots, key = { it.id }) { rootNode ->
                                    HeadingTreeNode(
                                        node = rootNode,
                                        expandedIds = uiState.expandedHeadingIds,
                                        filterQuery = uiState.outlineFilterQuery,
                                        onToggleExpanded = viewModel::toggleHeadingExpanded,
                                        onHeadingClick = viewModel::jumpToHeading
                                    )
                                }
                            }
                        }

                        // Outline Bottom Summary Card
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = spacing.md, vertical = spacing.xs),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing.sm),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("H1", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    Text("${uiState.outline.h1Count}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("H2", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                    Text("${uiState.outline.h2Count}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("H3+", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                                    Text("${uiState.outline.h3PlusCount}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Words", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${uiState.stats.words}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Table Insertion Dialog ────────────────────────────────────────────────
    if (uiState.showTableDialog) {
        var rows by remember { mutableFloatStateOf(3f) }
        var cols by remember { mutableFloatStateOf(3f) }
        var hasHeader by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { viewModel.setShowTableDialog(false) },
            title = { Text("Insert Markdown Table") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text("Columns: ${cols.toInt()}", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = cols,
                        onValueChange = { cols = it },
                        valueRange = 1f..6f,
                        steps = 4
                    )

                    Text("Rows: ${rows.toInt()}", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = rows,
                        onValueChange = { rows = it },
                        valueRange = 1f..10f,
                        steps = 8
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Include Header Row", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = hasHeader, onCheckedChange = { hasHeader = it })
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.insertTable(rows.toInt(), cols.toInt(), hasHeader) }) {
                    Text("Insert Table")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowTableDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ── Link Insertion Dialog ─────────────────────────────────────────────────
    if (uiState.showLinkDialog) {
        var linkTitle by remember { mutableStateOf("") }
        var linkUrl by remember { mutableStateOf("https://") }

        AlertDialog(
            onDismissRequest = { viewModel.setShowLinkDialog(false) },
            title = { Text("Insert Link") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    OutlinedTextField(
                        value = linkTitle,
                        onValueChange = { linkTitle = it },
                        label = { Text("Link Text") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = linkUrl,
                        onValueChange = { linkUrl = it },
                        label = { Text("URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.insertLink(linkTitle.ifBlank { "Link" }, linkUrl) },
                    enabled = linkUrl.isNotBlank()
                ) {
                    Text("Insert Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowLinkDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ── New Document Confirm Dialog ───────────────────────────────────────────
    if (uiState.showNewDocumentConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowNewDocumentConfirmDialog(false) },
            title = { Text("Discard Unsaved Changes?") },
            text = { Text("Your current document has unsaved edits. Are you sure you want to discard them and create a new document?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.setShowNewDocumentConfirmDialog(false)
                    viewModel.newDocument()
                }) {
                    Text("Discard & Create New")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowNewDocumentConfirmDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Recursive composable for rendering a hierarchical, collapsible heading node
 * with indentation, level badges, and expand/collapse child branches.
 */
@Composable
private fun HeadingTreeNode(
    node: HeadingNode,
    expandedIds: Set<String>,
    filterQuery: String,
    onToggleExpanded: (String) -> Unit,
    onHeadingClick: (HeadingNode) -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpanded = expandedIds.contains(node.id)
    val hasChildren = node.children.isNotEmpty()
    val matchesFilter = filterQuery.isEmpty() || node.title.contains(filterQuery, ignoreCase = true) ||
            node.children.any { it.title.contains(filterQuery, ignoreCase = true) }

    if (!matchesFilter) return

    val badgeColor = when (node.level) {
        1 -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        2 -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        3 -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    val indent = ((node.level - 1) * 14).dp

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onHeadingClick(node) }
                .padding(start = indent, end = 4.dp, top = 3.dp, bottom = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Expand/collapse chevron for parents
            if (hasChildren) {
                IconButton(
                    onClick = { onToggleExpanded(node.id) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        if (isExpanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.ExpandLess,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Spacer(Modifier.width(24.dp))
            }

            // Level Pill (H1, H2, ...)
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = badgeColor.first,
                modifier = Modifier.padding(end = 6.dp)
            ) {
                Text(
                    text = "H${node.level}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = badgeColor.second,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }

            // Title
            Text(
                text = node.title,
                style = when (node.level) {
                    1 -> MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    2 -> MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    else -> MaterialTheme.typography.bodySmall
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            // Line indicator
            Text(
                text = "L${node.lineNumber}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Render children if expanded
        if (hasChildren && isExpanded) {
            node.children.forEach { child ->
                HeadingTreeNode(
                    node = child,
                    expandedIds = expandedIds,
                    filterQuery = filterQuery,
                    onToggleExpanded = onToggleExpanded,
                    onHeadingClick = onHeadingClick
                )
            }
        }
    }
}
