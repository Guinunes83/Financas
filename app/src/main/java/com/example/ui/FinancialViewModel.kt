package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.EntryType
import com.example.data.FinancialEntry
import com.example.data.FinancialRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.data.FinancialCategory
import com.example.data.FinancialPlan
import kotlinx.coroutines.flow.combine

import com.example.data.EntryStatus

import kotlinx.coroutines.flow.first

data class FinancialUiState(
    val entries: List<FinancialEntry> = emptyList(),
    val categories: List<FinancialCategory> = emptyList(),
    val plans: List<FinancialPlan> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val scheduledIncome: Double = 0.0,
    val scheduledExpense: Double = 0.0,
    val balance: Double = 0.0
)

class FinancialViewModel(private val repository: FinancialRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            try {
                val entries = repository.allEntries.first()
                val calendar = java.util.Calendar.getInstance()
                calendar.add(java.util.Calendar.MONTH, 11)
                val maxDateLimit = calendar.timeInMillis
                
                val recurringGroups = entries.filter { it.recurrenceType == com.example.data.RecurrenceType.RECORRENTE && it.recurrenceId != null }
                    .groupBy { it.recurrenceId!! }
                    
                recurringGroups.forEach { (recurrenceId, groupEntries) ->
                    val latestEntry = groupEntries.maxByOrNull { it.dateMillis }
                    if (latestEntry != null) {
                        val latestCal = java.util.Calendar.getInstance().apply { timeInMillis = latestEntry.dateMillis }
                        
                        while (true) {
                            latestCal.add(java.util.Calendar.MONTH, 1)
                            if (latestCal.timeInMillis <= maxDateLimit) {
                                val newEntry = latestEntry.copy(
                                    id = 0,
                                    dateMillis = latestCal.timeInMillis,
                                    status = com.example.data.EntryStatus.PENDING
                                )
                                repository.insert(newEntry)
                            } else {
                                break
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val uiState: StateFlow<FinancialUiState> = combine(
        repository.allEntries,
        repository.allCategories,
        repository.allPlans
    ) { entries, categories, plans ->
        val income = entries.filter { it.type == EntryType.INCOME }.sumOf { it.amount }
        val expense = entries.filter { it.type == EntryType.EXPENSE && it.status == EntryStatus.COMPLETED }.sumOf { it.amount }
        
        val scheduledIncome = entries.filter { it.type == EntryType.INCOME && it.status == EntryStatus.PENDING }.sumOf { it.amount }
        val scheduledExpense = entries.filter { it.type == EntryType.EXPENSE && it.status == EntryStatus.PENDING }.sumOf { it.amount }
        
        val plansSaved = entries.filter { it.type == EntryType.PLAN }.sumOf { it.amount }
        
        FinancialUiState(
            entries = entries,
            categories = categories,
            plans = plans,
            totalIncome = income,
            totalExpense = expense,
            scheduledIncome = scheduledIncome,
            scheduledExpense = scheduledExpense,
            balance = income - expense - plansSaved
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinancialUiState()
    )

    fun insertEntry(entry: FinancialEntry) = viewModelScope.launch {
        if (entry.recurrenceType == com.example.data.RecurrenceType.RECORRENTE) {
            val recurrenceId = entry.recurrenceId ?: java.util.UUID.randomUUID().toString()
            val baseDate = java.util.Calendar.getInstance().apply { timeInMillis = entry.dateMillis }
            for (i in 0..11) {
                val nextDate = baseDate.clone() as java.util.Calendar
                nextDate.add(java.util.Calendar.MONTH, i)
                val newEntry = entry.copy(
                    id = 0,
                    dateMillis = nextDate.timeInMillis,
                    recurrenceId = recurrenceId,
                    status = if (i == 0) entry.status else com.example.data.EntryStatus.PENDING
                )
                repository.insert(newEntry)
            }
        } else {
            repository.insert(entry)
        }
    }

        fun updateFutureEntries(entry: FinancialEntry) = viewModelScope.launch {
        repository.update(entry)
        repository.updateFutureEntries(entry)
    }

    fun updateEntry(entry: FinancialEntry) = viewModelScope.launch {
        if (entry.recurrenceType == com.example.data.RecurrenceType.RECORRENTE && entry.recurrenceId == null) {
            val recurrenceId = java.util.UUID.randomUUID().toString()
            val entryWithId = entry.copy(recurrenceId = recurrenceId)
            repository.update(entryWithId)
            
            val baseDate = java.util.Calendar.getInstance().apply { timeInMillis = entry.dateMillis }
            for (i in 1..11) {
                val nextDate = baseDate.clone() as java.util.Calendar
                nextDate.add(java.util.Calendar.MONTH, i)
                val newEntry = entry.copy(
                    id = 0,
                    dateMillis = nextDate.timeInMillis,
                    recurrenceId = recurrenceId,
                    status = com.example.data.EntryStatus.PENDING
                )
                repository.insert(newEntry)
            }
        } else {
            repository.update(entry)
        }
    }

    fun deleteEntry(entry: FinancialEntry) = viewModelScope.launch {
        repository.delete(entry)
    }
    
    fun deleteFutureEntries(entry: FinancialEntry) = viewModelScope.launch {
        if (entry.recurrenceId != null) {
            repository.deleteFutureEntries(entry.recurrenceId, entry.dateMillis)
        } else {
            repository.delete(entry)
        }
    }
    
    
    fun insertPlan(plan: FinancialPlan) = viewModelScope.launch {
        repository.insertPlan(plan)
    }

    fun updatePlan(plan: FinancialPlan) = viewModelScope.launch {
        repository.updatePlan(plan)
    }

    fun deletePlan(plan: FinancialPlan) = viewModelScope.launch {
        repository.deletePlan(plan)
    }

    fun insertCategory(category: FinancialCategory) = viewModelScope.launch {
        repository.insertCategory(category)
    }
    
    fun updateCategoryName(category: FinancialCategory, oldName: String) = viewModelScope.launch {
        repository.updateCategoryName(category, oldName)
    }
    
    fun deleteCategory(category: FinancialCategory) = viewModelScope.launch {
        repository.deleteCategory(category)
    }
}

class FinancialViewModelFactory(private val repository: FinancialRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinancialViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinancialViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
