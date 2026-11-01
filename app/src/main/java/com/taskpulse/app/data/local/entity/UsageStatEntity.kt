package com.taskpulse.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usage_stats")
data class UsageStatEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appName: String,
    val minutesUsed: Int,
    val lastUsed: Long,
    val category: String,
    val productivityScore: Int
)
