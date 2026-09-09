import re

with open('app/src/main/java/com/example/ui/AddEditEntryDialog.kt', 'r') as f:
    content = f.read()

# Let's write the whole file since we need to inject uiState.entries and current month balance logic.
# But actually, I'd rather just replace the function body.

new_file = """package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import java.util.Calendar
import com.example.data.EntryType
import com.example.data.FinancialEntry
import com.example.data.FinancialCategory
import com.example.data.FinancialPlan
import com.example.data.RecurrenceType
import com.example.data.EntryStatus
import com.example.ui.theme.GeometricIncome
import com.example.ui.theme.GeometricExpense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEntryDialog(
    entry: FinancialEntry?,
    categories: List<FinancialCategory>,
    plans: List<FinancialPlan>,
    entries: List<FinancialEntry>,
    onDismiss: () -> Unit,
    onSave: (FinancialEntry) -> Unit,
    onSaveCategory: (String) -> Unit
) {
    var type by remember { mutableStateOf(entry?.type ?: EntryType.EXPENSE) }
    var name by remember { mutableStateOf(entry?.name ?: "") }
    var category by remember { mutableStateOf(entry?.category ?: "") }
    var planId by remember { mutableStateOf(entry?.planId) }
    var amountString by remember { mutableStateOf(entry?.amount?.let { (it * 100).toLong().toString() } ?: "") }
    var notes by remember { mutableStateOf(entry?.notes ?: "") }
    var recurrenceType by remember { mutableStateOf(entry?.recurrenceType ?: RecurrenceType.UNITARIO) }
    var installmentCountStr by remember { mutableStateOf(entry?.installmentCount?.toString() ?: "") }
    var status by remember { mutableStateOf(entry?.status ?: EntryStatus.PENDING) }
    
    var categoryExpanded by remember { mutableStateOf(false) }
    var showNewCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    
    var dateMillis by remember { mutableStateOf(entry?.dateMillis ?: System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val currentMonth = remember(dateMillis) { Calendar.getInstance().apply { timeInMillis = dateMillis }.get(Calendar.MONTH) }
    val currentYear = remember(dateMillis) { Calendar.getInstance().apply { timeInMillis = dateMillis }.get(Calendar.YEAR) }
    
    val currentMonthNetTotal = remember(entries, currentMonth, currentYear) {
        val monthEntries = entries.filter { 
            val cal = Calendar.getInstance().apply { timeInMillis = it.dateMillis }
            cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
        }
        val inc = monthEntries.filter { it.type == EntryType.INCOME && it.status == EntryStatus.COMPLETED }.sumOf { it.amount }
        val exp = monthEntries.filter { it.type == EntryType.EXPENSE && it.status == EntryStatus.COMPLETED }.sumOf { it.amount }
        val plansSaved = monthEntries.filter { it.type == EntryType.PLAN }.sumOf { it.amount }
        inc - exp - plansSaved
    }

    if (showNewCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showNewCategoryDialog = false },
            title = { Text("Nova Categoria") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Nome da categoria") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            onSaveCategory(newCategoryName.trim())
                            category = newCategoryName.trim()
                            showNewCategoryDialog = false
                            newCategoryName = ""
                        }
                    },
                    enabled = newCategoryName.isNotBlank()
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewCategoryDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dateMillis = it }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (entry == null) "Novo Lançamento" else "Editar Lançamento",
                    style = MaterialTheme.typography.titleLarge
                )
                
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Selecionar Data", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(dateFormatter.format(Date(dateMillis)), fontSize = 12.sp)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = type == EntryType.EXPENSE,
                    onClick = { type = EntryType.EXPENSE },
                    label = { Text("Despesa") }
                )
                FilterChip(
                    selected = type == EntryType.INCOME,
                    onClick = { type = EntryType.INCOME },
                    label = { Text("Receita") }
                )
                FilterChip(
                    selected = type == EntryType.PLAN,
                    onClick = { type = EntryType.PLAN },
                    label = { Text("Plano") }
                )
            }

            if (type != EntryType.PLAN) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Item") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (type == EntryType.PLAN) {
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = plans.find { it.id == planId }?.name ?: category,
                        onValueChange = { 
                            category = it
                            planId = null
                            categoryExpanded = true
                        },
                        label = { Text("Selecione o Plano") },
                        singleLine = true,
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        if (plans.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Nenhum plano criado") },
                                onClick = { categoryExpanded = false },
                                enabled = false
                            )
                        } else {
                            plans.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p.name) },
                                    onClick = { 
                                        planId = p.id
                                        category = p.name
                                        categoryExpanded = false 
                                    }
                                )
                            }
                        }
                    }
                }
                
                OutlinedTextField(
                    value = amountString,
                    onValueChange = { newValue -> 
                        amountString = newValue.filter { it.isDigit() }.take(15) 
                    },
                    label = { Text("Valor a Transferir") },
                    placeholder = { Text("R$ 0,00") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = CurrencyVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                
                val currentAmount = (amountString.toLongOrNull() ?: 0L) / 100.0
                val color = if (currentMonthNetTotal < 0 || currentAmount > currentMonthNetTotal) GeometricExpense else MaterialTheme.colorScheme.onSurfaceVariant
                Text(
                    text = "Líquido disponível neste mês: ${formatCurrency(currentMonthNetTotal)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = color
                )
                
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { 
                                category = it
                                categoryExpanded = true
                            },
                            label = { Text("Categoria") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            val filteredCategories = categories.filter { it.name.contains(category, ignoreCase = true) }
                            
                            if (filteredCategories.isEmpty() && category.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Nenhuma categoria (Crie uma nova)") },
                                    onClick = { categoryExpanded = false },
                                    enabled = false
                                )
                            } else {
                                filteredCategories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.name) },
                                        onClick = { 
                                            category = cat.name
                                            categoryExpanded = false 
                                        }
                                    )
                                }
                            }
                            
                            Divider(modifier = Modifier.padding(horizontal = 8.dp))
                            DropdownMenuItem(
                                text = { Text("Criar nova categoria...", color = MaterialTheme.colorScheme.primary) },
                                onClick = { 
                                    showNewCategoryDialog = true
                                    categoryExpanded = false 
                                }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = amountString,
                        onValueChange = { newValue -> 
                            amountString = newValue.filter { it.isDigit() }.take(15) 
                        },
                        label = { Text("Valor Total") },
                        placeholder = { Text("R$ 0,00") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = CurrencyVisualTransformation(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Tipo de Lançamento", style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = recurrenceType == RecurrenceType.UNITARIO,
                        onClick = { recurrenceType = RecurrenceType.UNITARIO },
                        label = { Text("Unitário") }
                    )
                    FilterChip(
                        selected = recurrenceType == RecurrenceType.PARCELADO,
                        onClick = { recurrenceType = RecurrenceType.PARCELADO },
                        label = { Text("Parcelado") }
                    )
                    FilterChip(
                        selected = recurrenceType == RecurrenceType.RECORRENTE,
                        onClick = { recurrenceType = RecurrenceType.RECORRENTE },
                        label = { Text("Recorrente") }
                    )
                }
                
                if (recurrenceType == RecurrenceType.PARCELADO) {
                    OutlinedTextField(
                        value = installmentCountStr,
                        onValueChange = { installmentCountStr = it },
                        label = { Text("Quantidade de parcelas") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (recurrenceType == RecurrenceType.RECORRENTE) {
                    Text(
                        text = "Lançamento recorrente será replicado no mesmo dia dos próximos meses.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text("Status", style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = status == EntryStatus.PENDING,
                        onClick = { status = EntryStatus.PENDING },
                        label = { Text("Pendente") }
                    )
                    FilterChip(
                        selected = status == EntryStatus.COMPLETED,
                        onClick = { status = EntryStatus.COMPLETED },
                        label = { Text(if (type == EntryType.INCOME) "Recebido" else "Pago") }
                    )
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Observações (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                minLines = 2
            )

            val amount = (amountString.toLongOrNull() ?: 0L) / 100.0
            
            val isSaveEnabled = if (type == EntryType.PLAN) {
                planId != null && amount > 0 && currentMonthNetTotal >= 0 && amount <= currentMonthNetTotal
            } else {
                name.isNotBlank() && category.isNotBlank() && amount > 0 && 
                (recurrenceType != RecurrenceType.PARCELADO || installmentCountStr.isNotBlank())
            }

            Button(
                onClick = {
                    val installments = installmentCountStr.toIntOrNull()
                    val finalName = if (type == EntryType.PLAN) {
                        val planName = plans.find { it.id == planId }?.name ?: ""
                        "Transferência para os planos \"$planName\""
                    } else name

                    val finalRecurrence = if (type == EntryType.PLAN) RecurrenceType.UNITARIO else recurrenceType
                    val finalStatus = if (type == EntryType.PLAN) EntryStatus.COMPLETED else status
                    val finalCategory = if (type == EntryType.PLAN) "Plano" else category

                    val newEntry = FinancialEntry(
                        id = entry?.id ?: 0,
                        type = type,
                        dateMillis = dateMillis,
                        name = finalName,
                        category = finalCategory,
                        amount = amount,
                        notes = notes,
                        recurrenceType = finalRecurrence,
                        installmentCount = if (finalRecurrence == RecurrenceType.PARCELADO) installments else null,
                        status = finalStatus,
                        planId = if (type == EntryType.PLAN) planId else null
                    )
                    onSave(newEntry)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isSaveEnabled
            ) {
                Text(if (type == EntryType.PLAN) "Transferir" else "Salvar")
            }
        }
    }
}
"""

with open('app/src/main/java/com/example/ui/AddEditEntryDialog.kt', 'w') as f:
    f.write(new_file)
