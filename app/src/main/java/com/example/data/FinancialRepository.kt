package com.example.data

import kotlinx.coroutines.flow.Flow

class FinancialRepository(
    private val entryDao: FinancialEntryDao,
    private val categoryDao: CategoryDao,
    private val planDao: PlanDao
) {
    val allEntries: Flow<List<FinancialEntry>> = entryDao.getAllEntries()
    val allCategories: Flow<List<FinancialCategory>> = categoryDao.getAllCategories()
    val allPlans: Flow<List<FinancialPlan>> = planDao.getAllPlans()

    suspend fun insert(entry: FinancialEntry) = entryDao.insertEntry(entry)
    suspend fun update(entry: FinancialEntry) = entryDao.updateEntry(entry)
    suspend fun delete(entry: FinancialEntry) = entryDao.deleteEntry(entry)
    suspend fun deleteFutureEntries(recurrenceId: String, dateMillis: Long) = entryDao.deleteFutureEntries(recurrenceId, dateMillis)

    suspend fun updateFutureEntries(entry: FinancialEntry) {
        if (entry.recurrenceId != null) {
            entryDao.updateFutureEntries(
                recurrenceId = entry.recurrenceId,
                dateMillis = entry.dateMillis,
                type = entry.type,
                name = entry.name,
                category = entry.category,
                amount = entry.amount,
                notes = entry.notes,
                planId = entry.planId
            )
        }
    }


    suspend fun insertCategory(category: FinancialCategory) = categoryDao.insertCategory(category)
    
    suspend fun updateCategoryName(category: FinancialCategory, oldName: String) {
        categoryDao.updateCategory(category)
        entryDao.updateEntriesCategoryName(oldName, category.name)
    }

    suspend fun deleteCategory(category: FinancialCategory) = categoryDao.deleteCategory(category)
    suspend fun insertPlan(plan: FinancialPlan) = planDao.insertPlan(plan)
    suspend fun updatePlan(plan: FinancialPlan) = planDao.updatePlan(plan)
    suspend fun deletePlan(plan: FinancialPlan) = planDao.deletePlan(plan)

}
