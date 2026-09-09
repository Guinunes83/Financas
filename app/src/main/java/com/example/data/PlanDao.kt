package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Query("SELECT * FROM financial_plans ORDER BY targetDateMillis ASC")
    fun getAllPlans(): Flow<List<FinancialPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: FinancialPlan)

    @Update
    suspend fun updatePlan(plan: FinancialPlan)

    @Delete
    suspend fun deletePlan(plan: FinancialPlan)
}
