package com.taskpulse.app.domain.repository

import com.taskpulse.app.data.local.entity.UsageStatEntity
import kotlinx.coroutines.flow.Flow

interface IAppUsageRepository {
    fun getUsageStats(): Flow<List<UsageStatEntity>>
    suspend fun saveUsageStats(stats: List<UsageStatEntity>)
    suspend fun clearStats()
}
