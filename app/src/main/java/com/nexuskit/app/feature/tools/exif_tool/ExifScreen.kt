package com.nexuskit.app.feature.tools.exif_tool

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExifScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: ExifViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    androidx.compose.runtime.LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onImageSelected(uri)
    }

    DrawerNavigation(
        navController = navController,
        currentToolId = "exif_tool"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "EXIF Metadata & Privacy Stripper",
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
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                if (uiState.selectedUri == null) {
                    // Empty State: Select photo
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
                                verticalArrangement = Arrangement.spacedBy(spacing.md)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(72.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }

                                Text(
                                    text = "Inspect & Strip Photo Metadata",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )

                                Text(
                                    text = "Photos taken on smartphones contain embedded EXIF metadata including your exact GPS location, camera serial numbers, device model, and date/time. NexusKit inspects and completely strips this data 100% offline.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Button(
                                    onClick = { photoPickerLauncher.launch("image/*") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, modifier = Modifier.padding(end = spacing.xs))
                                    Text("Select Photo from Device")
                                }
                            }
                        }
                    }
                } else {
                    // Photo Selected Overview Card
                    val analysis = uiState.analysisResult
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing.md),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = uiState.selectedUri,
                                    contentDescription = "Selected Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                Spacer(Modifier.width(spacing.md))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = analysis?.fileName ?: "Selected Image",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1
                                    )
                                    if (analysis != null) {
                                        Text(
                                            text = "${analysis.fileSizeFormatted} • ${analysis.dimensions}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${analysis.tags.size} EXIF Tags Detected",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (analysis.hasGps) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                            )
                                        )
                                    }
                                }

                                OutlinedButton(
                                    onClick = { photoPickerLauncher.launch("image/*") },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Change")
                                }
                            }
                        }
                    }

                    // GPS Privacy Risk Alert
                    if (analysis?.hasGps == true) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(spacing.md),
                                    verticalArrangement = Arrangement.spacedBy(spacing.xs)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                    ) {
                                        Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                                        Text(
                                            text = "Privacy Alert: GPS Geolocation Found!",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onErrorContainer
                                            )
                                        )
                                    }
                                    Text(
                                        text = "Coordinates: ${analysis.gpsCoordinates ?: "Unknown"}",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    )
                                    Text(
                                        text = "Anyone you send this original photo to can pinpoint where it was taken.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f)
                                    )

                                    if (!analysis.mapsUrl.isNullOrEmpty()) {
                                        FilledTonalButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(analysis.mapsUrl))
                                                context.startActivity(intent)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.filledTonalButtonColors(
                                                containerColor = MaterialTheme.colorScheme.error,
                                                contentColor = MaterialTheme.colorScheme.onError
                                            )
                                        ) {
                                            Icon(Icons.Filled.Map, contentDescription = null, modifier = Modifier.padding(end = spacing.xs))
                                            Text("View Location on Map")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Strip Metadata CTA / Success Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing.md),
                                verticalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                if (uiState.cleanedFile == null) {
                                    Text(
                                        text = "Strip All Metadata",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                    Text(
                                        text = "Cleans all GPS coordinates, camera serials, timestamps, and personal tags by re-encoding the pure visual pixels without metadata.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )

                                    Button(
                                        onClick = viewModel::stripMetadata,
                                        enabled = !uiState.isStripping,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        if (uiState.isStripping) {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                            Spacer(Modifier.width(spacing.xs))
                                            Text("Stripping EXIF...")
                                        } else {
                                            Icon(Icons.Filled.Security, contentDescription = null, modifier = Modifier.padding(end = spacing.xs))
                                            Text("Strip Metadata & Clean Photo")
                                        }
                                    }
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                    ) {
                                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Text(
                                            text = "Cleaned Photo Ready!",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        )
                                    }
                                    Text(
                                        text = "All metadata stripped. File is 100% private and safe to share.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                                    ) {
                                        Button(
                                            onClick = { viewModel.saveCleanedToGallery(context) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.padding(end = spacing.xxs).size(18.dp))
                                            Text(if (uiState.isSavedToGallery) "Saved in Gallery" else "Save to Gallery")
                                        }

                                        FilledTonalButton(
                                            onClick = {
                                                val file = uiState.cleanedFile ?: return@FilledTonalButton
                                                val shareUri = FileProvider.getUriForFile(
                                                    context,
                                                    "${context.packageName}.fileprovider",
                                                    file
                                                )
                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "image/jpeg"
                                                    putExtra(Intent.EXTRA_STREAM, shareUri)
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "Share Cleaned Photo"))
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.padding(end = spacing.xxs).size(18.dp))
                                            Text("Share")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Category Filter Chips
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                        ) {
                            listOf("ALL", "GPS & Location", "Camera & Hardware", "Date & Time", "Image & Format", "Software & Device").forEach { cat ->
                                FilterChip(
                                    selected = uiState.selectedFilter == cat,
                                    onClick = { viewModel.onFilterSelected(cat) },
                                    label = { Text(cat) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }

                    // Tags List
                    val filteredTags = analysis?.tags?.filter {
                        uiState.selectedFilter == "ALL" || it.category == uiState.selectedFilter
                    } ?: emptyList()

                    if (filteredTags.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                            ) {
                                Row(
                                    modifier = Modifier.padding(spacing.md),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                                ) {
                                    Icon(Icons.Filled.NoPhotography, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text(
                                        text = "No EXIF tags found in this category. The photo has minimal or no metadata.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredTags, key = { it.label }) { tag ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(
                                    containerColor = if (tag.isSensitive) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = spacing.md, vertical = spacing.sm),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tag.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (tag.isSensitive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = tag.value,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        )
                                    }

                                    IconButton(onClick = {
                                        clipboard.setText(AnnotatedString("${tag.label}: ${tag.value}"))
                                        Toast.makeText(context, "${tag.label} copied", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy")
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
