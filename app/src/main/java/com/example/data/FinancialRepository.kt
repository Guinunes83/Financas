package com.example.data

import kotlinx.coroutines.flow.Flow

class FinancialRepository(
    private val entryDao: FinancialEntryDao,
    private val categoryDao: CategoryDao
) {
    val allEntries: Flow<List<FinancialEntry>> = entryDao.getAllEntries()
    val allCategories: Flow<List<FinancialCategory>> = categoryDao.getAllCategories()

    suspend fun insert(entry: FinancialEntry) = entryDao.insertEntry(entry)
    suspend fun update(entry: FinancialEntry) = entryDao.updateEntry(entry)
    suspend fun delete(entry: FinancialEntry) = entryDao.deleteEntry(entry)
    suspend fun deleteFutureEntries(recurrenceId: String, dateMillis: Long) = entryDao.deleteFutureEntries(recurrenceId, dateMillis)

    suspend fun insertCategory(category: FinancialCategory) = categoryDao.insertCategory(category)
    
    suspend fun updateCategoryName(category: FinancialCategory, oldName: String) {
        categoryDao.updateCategory(category)
        entryDao.updateEntriesCategoryName(oldName, category.name)
    }

    suspend fun deleteCategory(category: FinancialCategory) = categoryDao.deleteCategory(category)
}
