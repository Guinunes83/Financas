import re

with open('app/src/main/java/com/example/ui/FinancialViewModel.kt', 'r') as f:
    content = f.read()

new_method = """    fun updateFutureEntries(entry: FinancialEntry) = viewModelScope.launch {
        repository.update(entry)
        repository.updateFutureEntries(entry)
    }
"""

content = content.replace(
    'fun updateEntry(entry: FinancialEntry) = viewModelScope.launch {',
    new_method + '\n    fun updateEntry(entry: FinancialEntry) = viewModelScope.launch {'
)

with open('app/src/main/java/com/example/ui/FinancialViewModel.kt', 'w') as f:
    f.write(content)
