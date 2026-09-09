with open('app/src/main/java/com/example/ui/FinancialScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'categories = uiState.categories,\n            plans = uiState.plans,',
    'categories = uiState.categories,\n            plans = uiState.plans,\n            entries = uiState.entries,'
)

with open('app/src/main/java/com/example/ui/FinancialScreen.kt', 'w') as f:
    f.write(content)
