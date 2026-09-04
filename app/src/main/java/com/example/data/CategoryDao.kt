package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM financial_categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<FinancialCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: FinancialCategory)

    @Delete
    suspend fun deleteCategory(category: FinancialCategory)
}
