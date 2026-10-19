package com.nexuskit.app.feature.tools.jwt_decoder

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
fun JwtScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: JwtViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val spacing = LocalSpacing.current
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    DrawerNavigation(
        navController = navController,
        currentToolId = "jwt_decoder"
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "JWT Decoder",
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
                // Token Input Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(spacing.md)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Encoded JWT Token",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Row {
                                    OutlinedButton(
                                        onClick = { viewModel.loadSampleToken() },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Sample", style = MaterialTheme.typography.labelSmall)
                                    }
                                    if (uiState.tokenInput.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(spacing.xs))
                                        IconButton(
                                            onClick = { viewModel.clear() },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = "Clear",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.sm))

                            OutlinedTextField(
                                value = uiState.tokenInput,
                                onValueChange = { viewModel.onTokenChanged(it) },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Paste eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                                    )
                                },
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                minLines = 3,
                                maxLines = 6,
                                isError = uiState.errorMessage != null
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

                // If decoded successfully
                val result = uiState.decodeResult
                if (result != null) {
                    // Token Status Overview Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(spacing.md)) {
                                Text(
                                    text = "Token Overview",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Spacer(modifier = Modifier.height(spacing.sm))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                                ) {
                                    result.algorithm?.let { alg ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(spacing.sm)) {
                                                Text("Algorithm", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                                Text(alg, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimaryContainer)
                                            }
                                        }
                                    }
                                    result.tokenType?.let { typ ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(spacing.sm)) {
                                                Text("Type", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                                                Text(typ, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSecondaryContainer)
                                            }
                                        }
                                    }
                                }

                                result.claims.expiresStatusText?.let { expStatus ->
                                    Spacer(modifier = Modifier.height(spacing.sm))
                                    val isExp = result.claims.isExpired == true
                                    val statusColor = if (isExp) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)
                                    val statusBg = if (isExp) MaterialTheme.colorScheme.errorContainer else Color(0xFFE8F5E9)

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = statusBg,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(spacing.sm),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Schedule,
                                                contentDescription = null,
                                                tint = statusColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(spacing.xs))
                                            Text(
                                                text = expStatus,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                                color = statusColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Standard Claims Breakdown Card
                    val claims = result.claims
                    if (claims.subject != null || claims.issuer != null || claims.audience != null || claims.jwtId != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(modifier = Modifier.padding(spacing.md)) {
                                    Text(
                                        text = "Standard Claims",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Spacer(modifier = Modifier.height(spacing.sm))

                                    claims.subject?.let {
                                        ClaimRow(label = "Subject (sub)", value = it)
                                    }
                                    claims.issuer?.let {
                                        ClaimRow(label = "Issuer (iss)", value = it)
                                    }
                                    claims.audience?.let {
                                        ClaimRow(label = "Audience (aud)", value = it)
                                    }
                                    claims.jwtId?.let {
                                        ClaimRow(label = "JWT ID (jti)", value = it)
                                    }
                                }
                            }
                        }
                    }

                    // Header Card
                    item {
                        JsonViewerCard(
                            title = "Header: Algorithm & Type",
                            tagColor = Color(0xFFD32F2F),
                            jsonText = result.prettyHeaderJson,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(result.prettyHeaderJson))
                                scope.launch { snackbarHostState.showSnackbar("Header copied to clipboard") }
                            }
                        )
                    }

                    // Payload Card
                    item {
                        JsonViewerCard(
                            title = "Payload: Data & Claims",
                            tagColor = Color(0xFF7B1FA2),
                            jsonText = result.prettyPayloadJson,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(result.prettyPayloadJson))
                                scope.launch { snackbarHostState.showSnackbar("Payload copied to clipboard") }
                            }
                        )
                    }

                    // Signature & Verification Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(spacing.md)) {
                                Text(
                                    text = "Signature & HS256 Verification",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Spacer(modifier = Modifier.height(spacing.xs))
                                Text(
                                    text = "Verify HMAC-SHA256 signature offline using your secret key.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(spacing.sm))

                                OutlinedTextField(
                                    value = uiState.secretKeyInput,
                                    onValueChange = { viewModel.onSecretKeyChanged(it) },
                                    label = { Text("HMAC Secret Key") },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text("your-256-bit-secret") },
                                    singleLine = true,
                                    leadingIcon = {
                                        Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                                    }
                                )

                                if (uiState.isSignatureVerified != null) {
                                    Spacer(modifier = Modifier.height(spacing.sm))
                                    val verified = uiState.isSignatureVerified == true
                                    val badgeBg = if (verified) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.errorContainer
                                    val badgeFg = if (verified) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                                    val icon = if (verified) Icons.Default.CheckCircle else Icons.Default.Error
                                    val msg = if (verified) "Signature Verified: Valid secret key!" else "Signature Invalid: Secret key does not match signature."

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = badgeBg,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(spacing.sm),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(icon, contentDescription = null, tint = badgeFg, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(spacing.xs))
                                            Text(msg, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = badgeFg)
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
}

@Composable
private fun ClaimRow(label: String, value: String) {
    val spacing = LocalSpacing.current
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = spacing.xs)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
private fun JsonViewerCard(
    title: String,
    tagColor: Color,
    jsonText: String,
    onCopy: () -> Unit
) {
    val spacing = LocalSpacing.current
    val scrollState = rememberScrollState()

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = tagColor
                    ) {}
                    Spacer(modifier = Modifier.width(spacing.xs))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
                IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy JSON",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Text(
                    text = jsonText,
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
