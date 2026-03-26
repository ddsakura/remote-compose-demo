package com.example.remotecompose.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.remote.player.view.RemoteComposePlayer

// Action ID 與 ui-remote 模組約定一致
private const val ACTION_PRIMARY_BUTTON = 1001
private const val ACTION_SECONDARY_BUTTON = 1002

@Composable
fun RemoteUiScreen(
    viewModel: RemoteUiViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentVersion by viewModel.currentVersion.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.statusBars)
    ) {

        // 頂部控制列：切換 v1 / v2，模擬 server 不同版本
        VersionSwitchBar(
            currentVersion = currentVersion,
            onSwitchVersion = { version -> viewModel.loadUi(version) },
            onReload = { viewModel.reload() }
        )

        // 主要內容區
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.reload() }
                    )
                }

                is UiState.Success -> {
                    RemoteComposeView(
                        bytes = state.bytes,
                        onAction = { actionId ->
                            when (actionId) {
                                ACTION_PRIMARY_BUTTON ->
                                    Toast.makeText(context, "主要按鈕點擊！", Toast.LENGTH_SHORT).show()
                                ACTION_SECONDARY_BUTTON ->
                                    Toast.makeText(context, "次要按鈕點擊！", Toast.LENGTH_SHORT).show()
                                else ->
                                    Toast.makeText(context, "Action ID: $actionId", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
private fun RemoteComposeView(
    bytes: ByteArray,
    onAction: (Int) -> Unit
) {
    AndroidView(
        factory = { context ->
            RemoteComposePlayer(context).apply {
                addIdActionListener { actionId, _ -> onAction(actionId) }
            }
        },
        update = { view ->
            // bytes 更新時（切換版本）自動重渲染
            view.setDocument(bytes)
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun VersionSwitchBar(
    currentVersion: String,
    onSwitchVersion: (String) -> Unit,
    onReload: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Server UI:",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f)
        )

        // V1 按鈕
        OutlinedButton(
            onClick = { onSwitchVersion("v1") },
            colors = if (currentVersion == "v1")
                ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            else ButtonDefaults.outlinedButtonColors()
        ) {
            Text("V1")
        }

        // V2 按鈕
        OutlinedButton(
            onClick = { onSwitchVersion("v2") },
            colors = if (currentVersion == "v2")
                ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            else ButtonDefaults.outlinedButtonColors()
        ) {
            Text("V2")
        }

        // 重載
        OutlinedButton(onClick = onReload) {
            Text("↺")
        }
    }

    HorizontalDivider()
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            text = "⚠️ 連線失敗",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall
        )
        Button(onClick = onRetry) {
            Text("重試")
        }
    }
}
