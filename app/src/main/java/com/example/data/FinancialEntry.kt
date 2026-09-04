package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EntryType {
    INCOME, EXPENSE
}

enum class RecurrenceType(val displayName: String) {
    UNITARIO("Unitário"),
    PARCELADO("Parcelado"),
    RECORRENTE("Recorrente")
}

enum class EntryStatus {
    PENDING, COMPLETED
}

@Entity(tableName = "financial_entries")
data class FinancialEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: EntryType,
    val dateMillis: Long,
    val name: String,
    val category: String,
    val amount: Double,
    val notes: String = "",
    val recurrenceType: RecurrenceType = RecurrenceType.UNITARIO,
    val installmentCount: Int? = null,
    val status: EntryStatus = EntryStatus.COMPLETED,
    val recurrenceId: String? = null
)
