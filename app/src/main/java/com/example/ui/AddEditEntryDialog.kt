package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.EntryType
import com.example.data.FinancialEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEntryDialog(
    entry: FinancialEntry?,
    onDismiss: () -> Unit,
    onSave: (FinancialEntry) -> Unit
) {
    var type by remember { mutableStateOf(entry?.type ?: EntryType.EXPENSE) }
    var name by remember { mutableStateOf(entry?.name ?: "") }
    var category by remember { mutableStateOf(entry?.category ?: "") }
    var amountString by remember { mutableStateOf(entry?.amount?.toString() ?: "") }
    var notes by remember { mutableStateOf(entry?.notes ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (entry == null) "Novo Lançamento" else "Editar Lançamento",
                style = MaterialTheme.typography.titleLarge
            )

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
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoria") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = amountString,
                    onValueChange = { amountString = it },
                    label = { Text("Valor") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Observações (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Button(
                onClick = {
                    val amount = amountString.toDoubleOrNull() ?: 0.0
                    val newEntry = FinancialEntry(
                        id = entry?.id ?: 0,
                        type = type,
                        dateMillis = entry?.dateMillis ?: System.currentTimeMillis(),
                        name = name,
                        category = category,
                        amount = amount,
                        notes = notes
                    )
                    onSave(newEntry)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && category.isNotBlank() && amountString.isNotBlank()
            ) {
                Text("Salvar")
            }
        }
    }
}
