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

data class FinancialUiState(
    val entries: List<FinancialEntry> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0
)

class FinancialViewModel(private val repository: FinancialRepository) : ViewModel() {

    val uiState: StateFlow<FinancialUiState> = repository.allEntries.map { entries ->
        val income = entries.filter { it.type == EntryType.INCOME }.sumOf { it.amount }
        val expense = entries.filter { it.type == EntryType.EXPENSE }.sumOf { it.amount }
        FinancialUiState(
            entries = entries,
            totalIncome = income,
            totalExpense = expense,
            balance = income - expense
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinancialUiState()
    )

    fun insertEntry(entry: FinancialEntry) = viewModelScope.launch {
        repository.insert(entry)
    }

    fun updateEntry(entry: FinancialEntry) = viewModelScope.launch {
        repository.update(entry)
    }

    fun deleteEntry(entry: FinancialEntry) = viewModelScope.launch {
        repository.delete(entry)
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
