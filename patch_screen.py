with open('app/src/main/java/com/example/ui/FinancialScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'AddEditEntryDialog(\n            entry = entryToEdit,\n            categories = uiState.categories,',
    'AddEditEntryDialog(\n            entry = entryToEdit,\n            categories = uiState.categories,\n            plans = uiState.plans,'
)

with open('app/src/main/java/com/example/ui/FinancialScreen.kt', 'w') as f:
    f.write(content)

print("Screen updated!")
