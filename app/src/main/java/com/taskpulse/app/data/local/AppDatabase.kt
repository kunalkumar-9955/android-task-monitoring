package com.taskpulse.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taskpulse.app.data.local.dao.UsageStatDao
import com.taskpulse.app.data.local.entity.UsageStatEntity

@Database(
    entities = [UsageStatEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usageStatDao(): UsageStatDao
}
