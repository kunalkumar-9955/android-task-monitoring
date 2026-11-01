package com.taskpulse.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taskpulse.app.data.local.entity.UsageStatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageStatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stats: List<UsageStatEntity>)

    @Query("SELECT * FROM usage_stats ORDER BY minutesUsed DESC")
    fun getAllUsageStats(): Flow<List<UsageStatEntity>>

    @Query("DELETE FROM usage_stats")
    suspend fun clearAll()
}
