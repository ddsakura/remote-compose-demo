package com.example.remotecompose.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remotecompose.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val bytes: ByteArray) : UiState()
    data class Error(val message: String) : UiState()
}

class RemoteUiViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    // 目前載入的版本，方便 UI 顯示
    private val _currentVersion = MutableStateFlow("v1")
    val currentVersion: StateFlow<String> = _currentVersion

    init {
        loadUi("v1")
    }

    fun loadUi(version: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _currentVersion.value = version
            try {
                val filename = "home-$version.rc"  // "v1" → "home-v1.rc"
                val responseBody = ApiClient.api.getUiDocument(filename)
                val bytes = responseBody.bytes()
                _uiState.value = UiState.Success(bytes)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    "載入失敗：${e.message}\n\n請確認 Server 有在 localhost:8080 執行"
                )
            }
        }
    }

    fun reload() {
        loadUi(_currentVersion.value)
    }
}
