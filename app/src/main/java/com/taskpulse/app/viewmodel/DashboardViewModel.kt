package com.taskpulse.app.viewmodel

import android.app.ActivityManager
import android.content.Context
import android.net.TrafficStats
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskpulse.app.data.local.entity.UsageStatEntity
import com.taskpulse.app.domain.repository.IAppUsageRepository
import com.taskpulse.app.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.RandomAccessFile
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: IAppUsageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refreshMetrics()
        viewModelScope.launch {
            repository.getUsageStats().collect { stats ->
                _uiState.update { it.copy(usageStats = stats) }
            }
        }
    }

    fun refreshMetrics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val battery = withContext(Dispatchers.IO) { queryBattery() }
            val ram = withContext(Dispatchers.IO) { queryRamUsage() }
            val storage = withContext(Dispatchers.IO) { queryStorageUsage() }
            val cpu = withContext(Dispatchers.IO) { queryCpuUsage() }
            val networkSpeed = withContext(Dispatchers.IO) { queryNetworkSpeed() }
            val deviceTemp = withContext(Dispatchers.IO) { queryDeviceTemperature() }
            val runningApps = withContext(Dispatchers.IO) { queryRunningAppCount() }
            val health = batteryHealthLabel(battery)
            val score = calculatePerformanceScore(battery, ram, storage, cpu)
            _uiState.update {
                it.copy(
                    batteryPercentage = battery,
                    batteryHealth = health,
                    memoryUsagePercent = ram,
                    storageUsedPercent = storage,
                    cpuUsagePercent = cpu,
                    networkSpeed = networkSpeed,
                    deviceTemperature = deviceTemp,
                    runningAppCount = runningApps,
                    performanceScore = score,
                    isLoading = false
                )
            }
            if (battery < 20) {
                NotificationHelper.sendNotification(context, "Low battery alert", "Battery is below 20%. Consider charging soon.")
            }
            if (ram > 85) {
                NotificationHelper.sendNotification(context, "High RAM usage", "RAM usage is above 85%. Close background apps to improve performance.")
            }
        }
    }

    fun generateSampleUsage() {
        viewModelScope.launch {
            val sample = listOf(
                UsageStatEntity(appName = "FocusFlow", packageName = "com.focusflow", minutesUsed = 52, lastUsed = System.currentTimeMillis(), category = "Productivity", productivityScore = 90),
                UsageStatEntity(appName = "StreamGrid", packageName = "com.streamgrid", minutesUsed = 105, lastUsed = System.currentTimeMillis(), category = "Entertainment", productivityScore = 28),
                UsageStatEntity(appName = "StudyWave", packageName = "com.studywave", minutesUsed = 70, lastUsed = System.currentTimeMillis(), category = "Education", productivityScore = 85),
                UsageStatEntity(appName = "ChatClub", packageName = "com.chatclub", minutesUsed = 115, lastUsed = System.currentTimeMillis(), category = "Social", productivityScore = 20)
            )
            repository.saveUsageStats(sample)
        }
    }

    private fun queryBattery(): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else 76
    }

    private fun queryRamUsage(): Int {
        val runtime = Runtime.getRuntime()
        val used = runtime.totalMemory() - runtime.freeMemory()
        val total = runtime.maxMemory().coerceAtLeast(runtime.totalMemory())
        return ((used.toDouble() / total) * 100).toInt().coerceIn(15, 95)
    }

    private fun queryStorageUsage(): Int {
        val stat = StatFs(Environment.getDataDirectory().absolutePath)
        val total = stat.totalBytes.toDouble()
        val free = stat.availableBytes.toDouble()
        return (((total - free) / total) * 100).toInt().coerceIn(20, 98)
    }

    private fun queryCpuUsage(): Int {
        return try {
            val reader = RandomAccessFile("/proc/stat", "r")
            val load = reader.readLine().split(" +".toRegex()).drop(1).map { it.toLong() }
            reader.close()
            val idle = load[3]
            val total = load.sum()
            val work1 = total - idle
            Thread.sleep(120)
            val reader2 = RandomAccessFile("/proc/stat", "r")
            val load2 = reader2.readLine().split(" +".toRegex()).drop(1).map { it.toLong() }
            reader2.close()
            val idle2 = load2[3]
            val total2 = load2.sum()
            val work2 = total2 - idle2
            (((work2 - work1).toDouble() / (total2 - total)) * 100).toInt().coerceIn(10, 95)
        } catch (exception: Exception) {
            38
        }
    }

    private fun batteryHealthLabel(level: Int): String {
        return when {
            level >= 80 -> "Excellent"
            level >= 55 -> "Good"
            level >= 30 -> "Fair"
            else -> "Low"
        }
    }

    private fun queryNetworkSpeed(): Int {
        return try {
            val initial = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()
            Thread.sleep(250)
            val later = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()
            ((later - initial) / 1024).toInt().coerceIn(1, 180)
        } catch (exception: Exception) {
            24
        }
    }

    private fun queryDeviceTemperature(): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val temp = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_TEMPERATURE)
            if (temp > 0) temp / 10 else 34
        } else 34
    }

    private fun queryRunningAppCount(): Int {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.runningAppProcesses?.size ?: 0
        } catch (exception: Exception) {
            0
        }
    }

    private fun calculatePerformanceScore(battery: Int, ram: Int, storage: Int, cpu: Int): Int {
        val base = battery * 0.2 + (100 - ram) * 0.25 + (100 - storage) * 0.25 + (100 - cpu) * 0.3
        return base.toInt().coerceIn(45, 98)
    }
}

data class DashboardUiState(
    val batteryPercentage: Int = 76,
    val batteryHealth: String = "Good",
    val memoryUsagePercent: Int = 48,
    val storageUsedPercent: Int = 56,
    val cpuUsagePercent: Int = 32,
    val networkSpeed: Int = 24,
    val deviceTemperature: Int = 34,
    val performanceScore: Int = 84,
    val runningAppCount: Int = 0,
    val isLoading: Boolean = true,
    val usageStats: List<UsageStatEntity> = emptyList()
)
