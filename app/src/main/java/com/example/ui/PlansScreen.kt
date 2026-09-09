package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EntryType
import com.example.ui.theme.GeometricIncome
import com.example.ui.theme.GeometricExpense
import androidx.compose.foundation.layout.imePadding
import com.example.data.FinancialPlan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(viewModel: FinancialViewModel, uiState: FinancialUiState) {
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddPlanDialog(
            onDismiss = { showAddDialog = false },
            onSave = { plan ->
                viewModel.insertPlan(plan)
                showAddDialog = false
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.plans.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Nenhum plano cadastrado.\nCrie um para começar a guardar!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Meus Planos",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(uiState.plans) { plan ->
                    val currentAmount = uiState.entries
                        .filter { it.type == EntryType.PLAN && it.planId == plan.id }
                        .sumOf { it.amount }
                    
                    PlanCard(plan = plan, currentAmount = currentAmount)
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Plano")
        }
    }
}

@Composable
fun PlanCard(plan: FinancialPlan, currentAmount: Double) {
    val dateFormatter = remember { SimpleDateFormat("MMM yyyy", Locale.getDefault()) }
    val progress = if (plan.targetAmount > 0) (currentAmount / plan.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
    val isCompleted = currentAmount >= plan.targetAmount

    Card(
        modifier = Modifier.fillMaxWidth().imePadding(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().imePadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dateFormatter.format(Date(plan.targetDateMillis)),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().imePadding(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Guardado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatCurrency(currentAmount), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = if (isCompleted) GeometricIncome else MaterialTheme.colorScheme.primary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Meta", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatCurrency(plan.targetAmount), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth().imePadding(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (isCompleted) {
                        Text(text = "Meta Atingida! \uD83C\uDF89", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = GeometricIncome)
                    }
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().imePadding().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = if (isCompleted) GeometricIncome else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlanDialog(onDismiss: () -> Unit, onSave: (FinancialPlan) -> Unit) {
    var name by remember { mutableStateOf("") }
    var amountString by remember { mutableStateOf("") }
    var dateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dateMillis = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth().imePadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Novo Plano", style = MaterialTheme.typography.titleLarge)
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome do Plano (ex: Comprar Carro)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth().imePadding()
            )

            OutlinedTextField(
                value = amountString,
                onValueChange = { newValue -> amountString = newValue.filter { it.isDigit() }.take(15) },
                label = { Text("Valor da Meta") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth().imePadding()
            )

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth().imePadding()
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Data Alvo: ${dateFormatter.format(Date(dateMillis))}")
            }

            Button(
                onClick = {
                    val targetAmount = (amountString.toLongOrNull() ?: 0L) / 100.0
                    if (name.isNotBlank() && targetAmount > 0) {
                        onSave(FinancialPlan(name = name, targetAmount = targetAmount, targetDateMillis = dateMillis))
                    }
                },
                modifier = Modifier.fillMaxWidth().imePadding(),
                enabled = name.isNotBlank() && amountString.isNotBlank()
            ) {
                Text("Criar Plano")
            }
        }
    }
}
