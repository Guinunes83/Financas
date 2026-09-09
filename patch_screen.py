import re

with open('app/src/main/java/com/example/ui/FinancialScreen.kt', 'r') as f:
    content = f.read()

# Add entryPendingUpdate state
content = content.replace(
    'var entryToDelete by remember { mutableStateOf<FinancialEntry?>(null) }',
    'var entryToDelete by remember { mutableStateOf<FinancialEntry?>(null) }\n    var entryPendingUpdate by remember { mutableStateOf<FinancialEntry?>(null) }'
)

# Modify onSave
old_on_save = """            onSave = { entry ->
                if (entry.id == 0) viewModel.insertEntry(entry)
                else viewModel.updateEntry(entry)
                showAddDialog = false
                entryToEdit = null
            },"""

new_on_save = """            onSave = { entry ->
                if (entry.id == 0) {
                    viewModel.insertEntry(entry)
                    showAddDialog = false
                    entryToEdit = null
                } else {
                    if (entry.recurrenceType == com.example.data.RecurrenceType.RECORRENTE && entry.recurrenceId != null) {
                        entryPendingUpdate = entry
                    } else {
                        viewModel.updateEntry(entry)
                        showAddDialog = false
                        entryToEdit = null
                    }
                }
            },"""

content = content.replace(old_on_save, new_on_save)

# Add the AlertDialog at the end of FinancialScreen, maybe right before entryToDelete
dialog_code = """
    if (entryPendingUpdate != null) {
        AlertDialog(
            onDismissRequest = { entryPendingUpdate = null },
            title = { Text("Editar Recorrência") },
            text = { Text("Deseja aplicar esta alteração apenas a este lançamento ou a este e a todos os próximos lançamentos recorrentes?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateFutureEntries(entryPendingUpdate!!)
                        entryPendingUpdate = null
                        showAddDialog = false
                        entryToEdit = null
                    }
                ) {
                    Text("Este e os próximos")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.updateEntry(entryPendingUpdate!!)
                        entryPendingUpdate = null
                        showAddDialog = false
                        entryToEdit = null
                    }
                ) {
                    Text("Apenas este")
                }
            }
        )
    }
"""

content = content.replace(
    'if (entryToDelete != null) {',
    dialog_code + '\n    if (entryToDelete != null) {'
)

with open('app/src/main/java/com/example/ui/FinancialScreen.kt', 'w') as f:
    f.write(content)

