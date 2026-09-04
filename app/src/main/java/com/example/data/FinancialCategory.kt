package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_categories")
data class FinancialCategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
