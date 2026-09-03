package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EntryType {
    INCOME, EXPENSE
}

@Entity(tableName = "financial_entries")
data class FinancialEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: EntryType,
    val dateMillis: Long,
    val name: String,
    val category: String,
    val amount: Double,
    val notes: String = ""
)
