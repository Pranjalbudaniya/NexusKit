package com.nexuskit.app.feature.tools.qr_generator

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.nexuskit.app.navigation.DrawerNavigation
import com.nexuskit.app.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrGeneratorScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: QrGeneratorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    DrawerNavigation(
        navController = navController,
        currentToolId = "qr_generator"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "QR Generator",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
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
                // ── QR Type Selector Pills ───────────────────────────────────────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs)
                    ) {
                        QrType.values().forEach { type ->
                            val isSelected = type == uiState.selectedType
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onTypeSelected(type) },
                                label = { Text(type.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }

                // ── Live QR Preview Card ─────────────────────────────────────────────
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(spacing.lg),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(240.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(androidx.compose.ui.graphics.Color.White)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isGenerating) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                } else if (uiState.qrBitmap != null) {
                                    Image(
                                        bitmap = uiState.qrBitmap!!.asImageBitmap(),
                                        contentDescription = "Generated QR Code",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Text(
                                        text = "Enter text to generate QR code",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = androidx.compose.ui.graphics.Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.md))

                            // Action buttons: Copy raw content & Share
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(uiState.rawPayload))
                                    },
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ContentCopy,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.size(spacing.xs))
                                    Text("Copy Text")
                                }

                                Button(
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, uiState.rawPayload)
                                            type = "text/plain"
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, "Share QR Content")
                                        context.startActivity(shareIntent)
                                    },
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Share,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.size(spacing.xs))
                                    Text("Share")
                                }
                            }
                        }
                    }
                }

                // ── Input Fields Section ─────────────────────────────────────────────
                item {
                    Text(
                        text = "Content",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(spacing.md),
                            verticalArrangement = Arrangement.spacedBy(spacing.sm)
                        ) {
                            when (uiState.selectedType) {
                                QrType.TEXT -> {
                                    OutlinedTextField(
                                        value = uiState.textContent,
                                        onValueChange = { viewModel.onTextChanged(it) },
                                        label = { Text("Text Message / Note") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = OutlinedTextFieldDefaults.colors()
                                    )
                                }
                                QrType.URL -> {
                                    OutlinedTextField(
                                        value = uiState.urlContent,
                                        onValueChange = { viewModel.onUrlChanged(it) },
                                        label = { Text("Website URL (e.g. https://...)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.medium,
                                        colors = OutlinedTextFieldDefaults.colors()
                                    )
                                }
                                QrType.WIFI -> {
                                    OutlinedTextField(
                                        value = uiState.wifiSsid,
                                        onValueChange = { viewModel.onWifiChanged(it, uiState.wifiPassword, uiState.wifiSecurity) },
                                        label = { Text("Network Name (SSID)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    OutlinedTextField(
                                        value = uiState.wifiPassword,
                                        onValueChange = { viewModel.onWifiChanged(uiState.wifiSsid, it, uiState.wifiSecurity) },
                                        label = { Text("Password") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                }
                                QrType.EMAIL -> {
                                    OutlinedTextField(
                                        value = uiState.emailAddress,
                                        onValueChange = { viewModel.onEmailChanged(it, uiState.emailSubject, uiState.emailBody) },
                                        label = { Text("Recipient Email") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    OutlinedTextField(
                                        value = uiState.emailSubject,
                                        onValueChange = { viewModel.onEmailChanged(uiState.emailAddress, it, uiState.emailBody) },
                                        label = { Text("Subject (Optional)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    OutlinedTextField(
                                        value = uiState.emailBody,
                                        onValueChange = { viewModel.onEmailChanged(uiState.emailAddress, uiState.emailSubject, it) },
                                        label = { Text("Body (Optional)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 2,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                }
                                QrType.PHONE -> {
                                    OutlinedTextField(
                                        value = uiState.phoneNumber,
                                        onValueChange = { viewModel.onPhoneChanged(it, uiState.smsMessage) },
                                        label = { Text("Phone Number") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    OutlinedTextField(
                                        value = uiState.smsMessage,
                                        onValueChange = { viewModel.onPhoneChanged(uiState.phoneNumber, it) },
                                        label = { Text("SMS Message (Leave empty for call)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 2,
                                        shape = MaterialTheme.shapes.medium
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
