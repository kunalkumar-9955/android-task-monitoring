package com.taskpulse.app.data.repository

import com.taskpulse.app.data.local.dao.UsageStatDao
import com.taskpulse.app.data.local.entity.UsageStatEntity
import com.taskpulse.app.domain.repository.IAppUsageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppUsageRepositoryImpl @Inject constructor(
    private val dao: UsageStatDao
) : IAppUsageRepository {
    override fun getUsageStats(): Flow<List<UsageStatEntity>> = dao.getAllUsageStats()
    override suspend fun saveUsageStats(stats: List<UsageStatEntity>) = dao.insertAll(stats)
    override suspend fun clearStats() = dao.clearAll()
}
