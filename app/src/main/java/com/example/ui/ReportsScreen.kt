package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EntryType
import com.example.data.FinancialEntry
import com.example.data.EntryStatus
import com.example.ui.theme.GeometricIncome
import com.example.ui.theme.GeometricExpense


@Composable
fun ReportsScreen(uiState: FinancialUiState) {
    val scrollState = rememberScrollState()

    val expenses = uiState.entries.filter { it.type == EntryType.EXPENSE && it.status == EntryStatus.COMPLETED }
    val incomes = uiState.entries.filter { it.type == EntryType.INCOME && it.status == EntryStatus.COMPLETED }

    val expenseByCategory = expenses.groupBy { it.category }.mapValues { it.value.sumOf { e -> e.amount } }.toList().sortedByDescending { it.second }
    val incomeByCategory = incomes.groupBy { it.category }.mapValues { it.value.sumOf { e -> e.amount } }.toList().sortedByDescending { it.second }

    val maxExpense = expenseByCategory.maxOfOrNull { it.second } ?: 1.0
    val maxIncome = incomeByCategory.maxOfOrNull { it.second } ?: 1.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Relatórios de Categorias",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        ChartSection(
            title = "Gastos por Categoria",
            data = expenseByCategory,
            maxAmount = maxExpense,
            color = GeometricExpense
        )

        ChartSection(
            title = "Ganhos por Categoria",
            data = incomeByCategory,
            maxAmount = maxIncome,
            color = GeometricIncome
        )
    }
}

@Composable
fun ChartSection(title: String, data: List<Pair<String, Double>>, maxAmount: Double, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (data.isEmpty()) {
            Text(
                text = "Nenhum dado encontrado.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            data.forEach { (category, amount) ->
                val fraction = (amount / maxAmount).toFloat().coerceIn(0.01f, 1f)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = category.ifBlank { "Sem categoria" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = formatCurrency(amount),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .height(8.dp)
                                .background(color, RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }
    }
}
