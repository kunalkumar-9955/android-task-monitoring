package com.taskpulse.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskpulse.app.data.local.entity.UsageStatEntity
import com.taskpulse.app.domain.repository.IAppUsageRepository
import com.taskpulse.app.util.BackupRestoreHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: IAppUsageRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleDarkMode() {
        _uiState.update { it.copy(darkModeEnabled = !it.darkModeEnabled) }
    }

    fun toggleNotifications() {
        _uiState.update { it.copy(notificationsEnabled = !it.notificationsEnabled) }
    }

    fun toggleLanguage() {
        _uiState.update { it.copy(language = if (it.language == "English") "Español" else "English") }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearStats()
            _uiState.update { it.copy(historyCleared = true, exportStatusMessage = "History cleared successfully.") }
            kotlinx.coroutines.delay(500)
            _uiState.update { it.copy(historyCleared = false) }
        }
    }

    fun exportData() {
        viewModelScope.launch {
            _uiState.update { it.copy(exportStatusMessage = null) }
            val stats = withContext(Dispatchers.IO) { repository.getUsageStats().first() }
            val success = withContext(Dispatchers.IO) { BackupRestoreHelper.exportUsageStats(context, stats) }
            _uiState.update { it.copy(exportStatusMessage = if (success) "Data exported to internal backup." else "Failed to export data.") }
        }
    }

    fun backupData() {
        viewModelScope.launch {
            _uiState.update { it.copy(backupStatusMessage = null) }
            val stats = withContext(Dispatchers.IO) { repository.getUsageStats().first() }
            val success = withContext(Dispatchers.IO) { BackupRestoreHelper.exportUsageStats(context, stats) }
            _uiState.update { it.copy(backupStatusMessage = if (success) "Backup completed." else "Backup failed.") }
        }
    }

    fun restoreData() {
        viewModelScope.launch {
            _uiState.update { it.copy(restoreStatusMessage = null) }
            val restored = withContext(Dispatchers.IO) { BackupRestoreHelper.restoreUsageStats(context) }
            if (restored.isNotEmpty()) {
                repository.saveUsageStats(restored)
                _uiState.update { it.copy(restoreStatusMessage = "Restore completed. Usage stats updated.") }
            } else {
                _uiState.update { it.copy(restoreStatusMessage = "No backup file found or restore failed.") }
            }
        }
    }
}

data class SettingsUiState(
    val darkModeEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val historyCleared: Boolean = false,
    val language: String = "English",
    val exportStatusMessage: String? = null,
    val backupStatusMessage: String? = null,
    val restoreStatusMessage: String? = null
)
