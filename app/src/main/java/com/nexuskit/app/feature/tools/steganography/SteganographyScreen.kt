package com.nexuskit.app.feature.tools.steganography

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EnhancedEncryption
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun SteganographyScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: SteganographyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var passwordVisible by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onCoverImageSelected(uri)
    }

    val decodePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onDecodeImageSelected(uri)
    }

    DrawerNavigation(
        navController = navController,
        currentToolId = "steganography"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Image Steganography",
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
                // Tab Selection Chips
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                    ) {
                        FilterChip(
                            selected = uiState.selectedTab == StegoTab.ENCODE,
                            onClick = { viewModel.onTabSelected(StegoTab.ENCODE) },
                            label = { Text("Hide Message (Encode)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        FilterChip(
                            selected = uiState.selectedTab == StegoTab.DECODE,
                            onClick = { viewModel.onTabSelected(StegoTab.DECODE) },
                            label = { Text("Reveal Message (Decode)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                if (uiState.selectedTab == StegoTab.ENCODE) {
                    // Cover Image Selection Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing.md),
                                verticalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                Text("1. Select Cover Image", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                                if (uiState.coverImageUri != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = uiState.coverImageUri,
                                            contentDescription = "Cover Image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                        )

                                        Spacer(Modifier.width(spacing.md))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Cover Image Selected", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                                            Text(
                                                "Max storage capacity: ${uiState.coverCapacityBytes / 1024} KB",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        OutlinedButton(
                                            onClick = { coverPickerLauncher.launch("image/*") },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Change")
                                        }
                                    }

                                    // Capacity usage bar
                                    val msgBytes = uiState.encodeMessage.toByteArray(Charsets.UTF_8).size
                                    val progress = if (uiState.coverCapacityBytes > 0) {
                                        (msgBytes.toFloat() / uiState.coverCapacityBytes.toFloat()).coerceIn(0f, 1f)
                                    } else 0f

                                    Column(verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
                                        LinearProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Used: $msgBytes bytes", style = MaterialTheme.typography.labelSmall)
                                            Text("Max: ${uiState.coverCapacityBytes} bytes", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { coverPickerLauncher.launch("image/*") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, modifier = Modifier.padding(end = spacing.xs))
                                        Text("Choose Cover Photo")
                                    }
                                }
                            }
                        }
                    }

                    // Message and Encryption Input
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing.md),
                                verticalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                Text("2. Secret Message to Conceal", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                                OutlinedTextField(
                                    value = uiState.encodeMessage,
                                    onValueChange = viewModel::onEncodeMessageChanged,
                                    label = { Text("Secret Message") },
                                    placeholder = { Text("Enter confidential notes, keys, or passwords...") },
                                    minLines = 3,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Text("3. Optional AES-256 Password", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                                OutlinedTextField(
                                    value = uiState.encodePassword,
                                    onValueChange = viewModel::onEncodePasswordChanged,
                                    label = { Text("Password (Optional)") },
                                    placeholder = { Text("Leave blank for unencrypted LSB") },
                                    singleLine = true,
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    trailingIcon = {
                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                            Icon(
                                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                                contentDescription = "Toggle password visibility"
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                if (uiState.encodeError != null) {
                                    Text(
                                        text = uiState.encodeError ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                Button(
                                    onClick = viewModel::encode,
                                    enabled = !uiState.isEncoding && uiState.coverImageUri != null && uiState.encodeMessage.isNotBlank(),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    if (uiState.isEncoding) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                        Spacer(Modifier.width(spacing.xs))
                                        Text("Encoding Secret Bits into Pixels...")
                                    } else {
                                        Icon(Icons.Filled.EnhancedEncryption, contentDescription = null, modifier = Modifier.padding(end = spacing.xs))
                                        Text("Hide Secret in Image")
                                    }
                                }
                            }
                        }
                    }

                    // Success Result
                    val res = uiState.encodeResult
                    if (res != null) {
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                                    ) {
                                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Text(
                                            text = "Stego-Image Created!",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        )
                                    }

                                    Text(
                                        text = "Payload of ${res.payloadSizeBytes} bytes successfully embedded into the image's least-significant pixel bits. The image looks visually unaltered.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
                                    ) {
                                        Text(
                                            text = "Note: Always share or store as a lossless PNG. Chat apps like WhatsApp or Telegram compress images into lossy JPEGs, which will corrupt the hidden bits.",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(spacing.sm)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                                    ) {
                                        Button(
                                            onClick = { viewModel.saveToGallery(context) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.padding(end = spacing.xxs).size(18.dp))
                                            Text(if (uiState.isSavedToGallery) "Saved in Gallery" else "Save to Gallery")
                                        }

                                        FilledTonalButton(
                                            onClick = {
                                                val shareUri = FileProvider.getUriForFile(
                                                    context,
                                                    "${context.packageName}.fileprovider",
                                                    res.outputFile
                                                )
                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "image/png"
                                                    putExtra(Intent.EXTRA_STREAM, shareUri)
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "Share Stego Image (PNG)"))
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
                } else {
                    // DECODE TAB
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing.md),
                                verticalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                Text("1. Select Stego Image", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                                if (uiState.decodeImageUri != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = uiState.decodeImageUri,
                                            contentDescription = "Stego Image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                        )

                                        Spacer(Modifier.width(spacing.md))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Image Selected", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                                            Text("Ready to scan for hidden payloads", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }

                                        OutlinedButton(
                                            onClick = { decodePickerLauncher.launch("image/*") },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Change")
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { decodePickerLauncher.launch("image/*") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, modifier = Modifier.padding(end = spacing.xs))
                                        Text("Select Image to Inspect")
                                    }
                                }

                                Spacer(Modifier.height(spacing.xs))

                                Text("2. Decryption Password (if protected)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                                OutlinedTextField(
                                    value = uiState.decodePassword,
                                    onValueChange = viewModel::onDecodePasswordChanged,
                                    label = { Text("Password (if required)") },
                                    singleLine = true,
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    trailingIcon = {
                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                            Icon(
                                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                                contentDescription = "Toggle password visibility"
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                if (uiState.decodeError != null) {
                                    Text(
                                        text = uiState.decodeError ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                Button(
                                    onClick = viewModel::decode,
                                    enabled = !uiState.isDecoding && uiState.decodeImageUri != null,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    if (uiState.isDecoding) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                        Spacer(Modifier.width(spacing.xs))
                                        Text("Scanning Pixel Bitplanes...")
                                    } else {
                                        Icon(Icons.Filled.Visibility, contentDescription = null, modifier = Modifier.padding(end = spacing.xs))
                                        Text("Extract Hidden Message")
                                    }
                                }
                            }
                        }
                    }

                    // Revealed Message Result
                    val dec = uiState.decodeResult
                    if (dec != null) {
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
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Revealed Secret Message",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        )
                                        if (dec.isEncrypted) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.primary
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onPrimary)
                                                    Text("AES-256 Decrypted", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onPrimary))
                                                }
                                            }
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = dec.message,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = FontFamily.Monospace,
                                                color = MaterialTheme.colorScheme.onSurface
                                            ),
                                            modifier = Modifier.padding(spacing.md)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            clipboard.setText(AnnotatedString(dec.message))
                                            Toast.makeText(context, "Secret message copied to clipboard", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.padding(end = spacing.xs))
                                        Text("Copy Secret Message")
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
