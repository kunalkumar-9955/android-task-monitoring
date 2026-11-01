package com.taskpulse.app.util

import android.content.Context
import com.taskpulse.app.data.local.entity.UsageStatEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object BackupRestoreHelper {
    private const val BACKUP_FILE = "taskpulse_backup.json"

    fun exportUsageStats(context: Context, stats: List<UsageStatEntity>): Boolean {
        return try {
            val json = JSONArray()
            stats.forEach { stat ->
                val entry = JSONObject().apply {
                    put("id", stat.id)
                    put("packageName", stat.packageName)
                    put("appName", stat.appName)
                    put("minutesUsed", stat.minutesUsed)
                    put("lastUsed", stat.lastUsed)
                    put("category", stat.category)
                    put("productivityScore", stat.productivityScore)
                }
                json.put(entry)
            }
            val file = File(context.filesDir, BACKUP_FILE)
            file.writeText(json.toString())
            true
        } catch (exception: Exception) {
            false
        }
    }

    fun restoreUsageStats(context: Context): List<UsageStatEntity> {
        return try {
            val file = File(context.filesDir, BACKUP_FILE)
            if (!file.exists()) return emptyList()
            val content = file.readText()
            val json = JSONArray(content)
            val stats = mutableListOf<UsageStatEntity>()
            for (index in 0 until json.length()) {
                val entry = json.getJSONObject(index)
                stats.add(
                    UsageStatEntity(
                        id = entry.optLong("id", 0),
                        packageName = entry.optString("packageName", "unknown"),
                        appName = entry.optString("appName", "Unknown"),
                        minutesUsed = entry.optInt("minutesUsed", 0),
                        lastUsed = entry.optLong("lastUsed", System.currentTimeMillis()),
                        category = entry.optString("category", "Utility"),
                        productivityScore = entry.optInt("productivityScore", 0)
                    )
                )
            }
            stats
        } catch (exception: Exception) {
            emptyList()
        }
    }
}
