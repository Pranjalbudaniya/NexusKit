package com.nexuskit.app.feature.tools.barcode_generator

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nexuskit.app.core.util.GallerySaver
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarcodeScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: BarcodeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val formatScrollState = rememberScrollState()

    DrawerNavigation(
        navController = navController,
        currentToolId = "barcode_generator"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Barcode Generator",
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
                // Format Selector Chips
                item {
                    Column {
                        Text(
                            text = "Barcode Symbology",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(spacing.xs))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(formatScrollState),
                            horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                        ) {
                            SupportedBarcodeFormat.entries.forEach { format ->
                                FilterChip(
                                    selected = uiState.selectedFormat == format,
                                    onClick = { viewModel.onFormatChanged(format) },
                                    label = { Text(format.title, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }

                // Input Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(spacing.md)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Barcode Data",
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
                                    if (uiState.content.isNotEmpty()) {
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

                            Spacer(modifier = Modifier.height(spacing.xs))
                            Text(
                                text = uiState.selectedFormat.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(spacing.sm))

                            OutlinedTextField(
                                value = uiState.content,
                                onValueChange = { viewModel.onContentChanged(it) },
                                placeholder = { Text("Enter barcode value...") },
                                textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            if (uiState.errorMessage != null) {
                                Spacer(modifier = Modifier.height(spacing.xs))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(spacing.xs))
                                    Text(uiState.errorMessage ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.sm))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Show readable text label", style = MaterialTheme.typography.bodySmall)
                                Switch(
                                    checked = uiState.showTextLabel,
                                    onCheckedChange = { viewModel.onShowTextLabelToggled(it) }
                                )
                            }
                        }
                    }
                }

                // Barcode Preview Card & Save Actions
                val bmp = uiState.barcodeBitmap
                if (bmp != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(spacing.md),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Generated Barcode",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    modifier = Modifier.align(Alignment.Start)
                                )

                                Spacer(modifier = Modifier.height(spacing.md))

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    shadowElevation = 2.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = spacing.sm)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(spacing.md),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            bitmap = bmp.asImageBitmap(),
                                            contentDescription = "Barcode image",
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(spacing.md))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                                ) {
                                    Button(
                                        onClick = {
                                            val res = GallerySaver.saveBitmapToGallery(
                                                context = context,
                                                bitmap = bmp,
                                                titlePrefix = "barcode_${uiState.selectedFormat.name.lowercase()}"
                                            )
                                            scope.launch {
                                                if (res.isSuccess) {
                                                    snackbarHostState.showSnackbar("Saved barcode directly to Gallery (Pictures/NexusKit)")
                                                } else {
                                                    snackbarHostState.showSnackbar("Failed to save: ${res.exceptionOrNull()?.message}")
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(spacing.xs))
                                        Text("Save to Gallery")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val uriRes = GallerySaver.saveBitmapToGallery(
                                                context = context,
                                                bitmap = bmp,
                                                titlePrefix = "barcode_share"
                                            )
                                            uriRes.onSuccess { uri ->
                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "image/png"
                                                    putExtra(Intent.EXTRA_STREAM, uri)
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "Share Barcode"))
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(spacing.xs))
                                        Text("Share")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
