package com.example.ui

import androidx.compose.foundation.layout.*
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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import com.example.data.EntryType
import com.example.data.FinancialEntry
import com.example.data.FinancialCategory
import com.example.data.RecurrenceType

import com.example.data.EntryStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEntryDialog(
    entry: FinancialEntry?,
    categories: List<FinancialCategory>,
    onDismiss: () -> Unit,
    onSave: (FinancialEntry) -> Unit,
    onSaveCategory: (String) -> Unit
) {
    var type by remember { mutableStateOf(entry?.type ?: EntryType.EXPENSE) }
    var name by remember { mutableStateOf(entry?.name ?: "") }
    var category by remember { mutableStateOf(entry?.category ?: "") }
    var amountString by remember { mutableStateOf(entry?.amount?.toString() ?: "") }
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
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome do Item") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )
            
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
                    onValueChange = { amountString = it },
                    label = { Text("Valor Total") },
                    placeholder = { Text("R$ 0,00") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Observações (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                minLines = 2
            )

            Button(
                onClick = {
                    val amount = amountString.toDoubleOrNull() ?: 0.0
                    val installments = installmentCountStr.toIntOrNull()
                    val newEntry = FinancialEntry(
                        id = entry?.id ?: 0,
                        type = type,
                        dateMillis = dateMillis,
                        name = name,
                        category = category,
                        amount = amount,
                        notes = notes,
                        recurrenceType = recurrenceType,
                        installmentCount = if (recurrenceType == RecurrenceType.PARCELADO) installments else null,
                        status = status
                    )
                    onSave(newEntry)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && category.isNotBlank() && amountString.isNotBlank() && 
                    (recurrenceType != RecurrenceType.PARCELADO || installmentCountStr.isNotBlank())
            ) {
                Text("Salvar")
            }
        }
    }
}
