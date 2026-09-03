package com.example.data

import kotlinx.coroutines.flow.Flow

class FinancialRepository(private val dao: FinancialEntryDao) {
    val allEntries: Flow<List<FinancialEntry>> = dao.getAllEntries()

    suspend fun insert(entry: FinancialEntry) = dao.insertEntry(entry)
    suspend fun update(entry: FinancialEntry) = dao.updateEntry(entry)
    suspend fun delete(entry: FinancialEntry) = dao.deleteEntry(entry)
}
