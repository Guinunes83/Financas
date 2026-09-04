package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialEntryDao {
    @Query("SELECT * FROM financial_entries ORDER BY dateMillis DESC")
    fun getAllEntries(): Flow<List<FinancialEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: FinancialEntry)

    @Update
    suspend fun updateEntry(entry: FinancialEntry)

    @Query("UPDATE financial_entries SET category = :newName WHERE category = :oldName")
    suspend fun updateEntriesCategoryName(oldName: String, newName: String)

    @Delete
    suspend fun deleteEntry(entry: FinancialEntry)

    @Query("DELETE FROM financial_entries WHERE recurrenceId = :recurrenceId AND dateMillis >= :dateMillis")
    suspend fun deleteFutureEntries(recurrenceId: String, dateMillis: Long)
}
