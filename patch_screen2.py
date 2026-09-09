import re

with open('app/src/main/java/com/example/ui/FinancialScreen.kt', 'r') as f:
    content = f.read()

dialog_code = """
    if (entryPendingUpdate != null) {
        AlertDialog(
            onDismissRequest = { entryPendingUpdate = null },
            title = { Text("Editar Lançamento Recorrente") },
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
            },
            containerColor = Color.White
        )
    }
"""

content = content.replace(
    '    entryToDelete?.let { entry ->',
    dialog_code + '\n    entryToDelete?.let { entry ->'
)

with open('app/src/main/java/com/example/ui/FinancialScreen.kt', 'w') as f:
    f.write(content)

