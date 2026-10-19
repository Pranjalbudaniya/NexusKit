package com.nexuskit.app.feature.tools.totp_authenticator

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
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
fun TotpScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: TotpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    DrawerNavigation(
        navController = navController,
        currentToolId = "totp_authenticator"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "2FA Authenticator",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.openAddDialog() }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Account")
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
                // Header Countdown Card
                item {
                    val progressColor = if (uiState.secondsRemaining <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = progressColor, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(spacing.xs))
                                    Text(
                                        text = "Offline RFC 6238 Generator",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "Expires in ${uiState.secondsRemaining}s",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = progressColor
                                )
                            }
                            Spacer(modifier = Modifier.height(spacing.xs))
                            LinearProgressIndicator(
                                progress = { uiState.progress },
                                modifier = Modifier.fillMaxWidth().height(4.dp),
                                color = progressColor,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }

                // Empty State
                if (uiState.items.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(spacing.xl),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(spacing.md))
                                Text(
                                    text = "No 2FA Accounts Yet",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(spacing.xs))
                                Text(
                                    text = "Keep your offline two-factor authorization codes safe on device with zero internet connection.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = spacing.md)
                                )

                                Spacer(modifier = Modifier.height(spacing.lg))

                                Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
                                    Button(onClick = { viewModel.openAddDialog() }) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(spacing.xs))
                                        Text("Add Account")
                                    }
                                    OutlinedButton(onClick = { viewModel.loadSample() }) {
                                        Text("Sample")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Accounts List
                    items(uiState.items, key = { it.account.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(item.currentCode))
                                    scope.launch { snackbarHostState.showSnackbar("Copied ${item.account.issuer} 2FA code!") }
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing.md),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                text = item.account.issuer,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(spacing.xs))
                                        Text(
                                            text = item.account.accountName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(spacing.xs))

                                    // Big Formatted OTP Code
                                    Text(
                                        text = item.formattedCode,
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 2.sp
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(item.currentCode))
                                            scope.launch { snackbarHostState.showSnackbar("Copied ${item.account.issuer} code!") }
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy code", modifier = Modifier.size(20.dp))
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteAccount(item.account.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Add Account Dialog
            if (uiState.isAddDialogOpen) {
                AddAccountDialog(
                    errorMessage = uiState.addError,
                    onDismiss = { viewModel.closeAddDialog() },
                    onAddManual = { name, issuer, secret ->
                        viewModel.addManualAccount(name, issuer, secret)
                    },
                    onAddUri = { uri ->
                        viewModel.addFromUri(uri)
                    }
                )
            }
        }
    }
}

@Composable
private fun AddAccountDialog(
    errorMessage: String?,
    onDismiss: () -> Unit,
    onAddManual: (String, String, String) -> Unit,
    onAddUri: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var accountName by remember { mutableStateOf("") }
    var issuer by remember { mutableStateOf("") }
    var secretKey by remember { mutableStateOf("") }
    var uriString by remember { mutableStateOf("") }
    val spacing = LocalSpacing.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add 2FA Account", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Manual") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("URI") })
                }

                Spacer(modifier = Modifier.height(spacing.sm))

                if (selectedTab == 0) {
                    OutlinedTextField(
                        value = issuer,
                        onValueChange = { issuer = it },
                        label = { Text("Service / Issuer (e.g. GitHub)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(spacing.xs))
                    OutlinedTextField(
                        value = accountName,
                        onValueChange = { accountName = it },
                        label = { Text("Account Name (e.g. user@email)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(spacing.xs))
                    OutlinedTextField(
                        value = secretKey,
                        onValueChange = { secretKey = it },
                        label = { Text("Base32 Secret Key") },
                        placeholder = { Text("JBSWY3DPEHPK3PXP") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                } else {
                    OutlinedTextField(
                        value = uriString,
                        onValueChange = { uriString = it },
                        label = { Text("otpauth://totp/... URI") },
                        placeholder = { Text("Paste otpauth:// URI") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(spacing.xs))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(spacing.xs))
                        Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedTab == 0) {
                        onAddManual(accountName, issuer, secretKey)
                    } else {
                        onAddUri(uriString)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
