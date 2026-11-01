package com.taskpulse.app.viewmodel

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskpulse.app.data.local.entity.UsageStatEntity
import com.taskpulse.app.domain.repository.IAppUsageRepository
import com.taskpulse.app.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class UsageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: IAppUsageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UsageUiState())
    val uiState: StateFlow<UsageUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getUsageStats().collectLatest { stats ->
                _uiState.update { it.copy(usageStats = stats) }
            }
        }
        refreshUsageStats()
    }

    fun refreshUsageStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val stats = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                queryUsageStats().ifEmpty { sampleUsage() }
            } else {
                sampleUsage()
            }
            repository.saveUsageStats(stats)
            if (stats.any { it.minutesUsed > 120 }) {
                NotificationHelper.sendNotification(context, "App overuse warning", "One or more apps have been used over 2 hours today.")
            }
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun sampleUsage(): List<UsageStatEntity> {
        return listOf(
            UsageStatEntity(appName = "ChatClub", packageName = "com.chatclub", minutesUsed = 115, lastUsed = System.currentTimeMillis(), category = "Social", productivityScore = 22),
            UsageStatEntity(appName = "FocusFlow", packageName = "com.focusflow", minutesUsed = 52, lastUsed = System.currentTimeMillis(), category = "Productivity", productivityScore = 88),
            UsageStatEntity(appName = "StudyWave", packageName = "com.studywave", minutesUsed = 70, lastUsed = System.currentTimeMillis(), category = "Education", productivityScore = 83),
            UsageStatEntity(appName = "StreamGrid", packageName = "com.streamgrid", minutesUsed = 105, lastUsed = System.currentTimeMillis(), category = "Entertainment", productivityScore = 24)
        )
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun queryUsageStats(): List<UsageStatEntity> {
        val usageManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val start = calendar.timeInMillis
        val end = System.currentTimeMillis()
        return usageManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
            .sortedByDescending { it.totalTimeInForeground }
            .map { raw ->
                UsageStatEntity(
                    packageName = raw.packageName,
                    appName = raw.packageName.substringAfterLast('.'),
                    minutesUsed = (raw.totalTimeInForeground / 60000).toInt().coerceAtLeast(1),
                    lastUsed = raw.lastTimeUsed,
                    category = inferCategory(raw.packageName),
                    productivityScore = inferProductivity(raw.totalTimeInForeground)
                )
            }
    }

    private fun inferCategory(packageName: String) = when {
        packageName.contains("game") || packageName.contains("play") -> "Gaming"
        packageName.contains("study") || packageName.contains("edu") -> "Education"
        packageName.contains("chat") || packageName.contains("social") -> "Social"
        packageName.contains("video") || packageName.contains("stream") -> "Entertainment"
        else -> "Utility"
    }

    private fun inferProductivity(totalTimeMs: Long): Int {
        return when {
            totalTimeMs > 90 * 60_000 -> 25
            totalTimeMs > 60 * 60_000 -> 45
            totalTimeMs > 30 * 60_000 -> 65
            else -> 80
        }
    }
}

data class UsageUiState(
    val isRefreshing: Boolean = false,
    val usageStats: List<UsageStatEntity> = emptyList()
)
