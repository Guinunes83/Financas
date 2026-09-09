import re

with open('app/src/main/java/com/example/ui/FinancialViewModel.kt', 'r') as f:
    content = f.read()

old_balance = """        val scheduledExpense = entries.filter { it.type == EntryType.EXPENSE && it.status == EntryStatus.PENDING }.sumOf { it.amount }
        
        FinancialUiState(
            entries = entries,
            categories = categories,
            plans = plans,
            totalIncome = income,
            totalExpense = expense,
            scheduledIncome = scheduledIncome,
            scheduledExpense = scheduledExpense,
            balance = income - expense
        )"""

new_balance = """        val scheduledExpense = entries.filter { it.type == EntryType.EXPENSE && it.status == EntryStatus.PENDING }.sumOf { it.amount }
        
        val plansSaved = entries.filter { it.type == EntryType.PLAN }.sumOf { it.amount }
        
        FinancialUiState(
            entries = entries,
            categories = categories,
            plans = plans,
            totalIncome = income,
            totalExpense = expense,
            scheduledIncome = scheduledIncome,
            scheduledExpense = scheduledExpense,
            balance = income - expense - plansSaved
        )"""

content = content.replace(old_balance, new_balance)

with open('app/src/main/java/com/example/ui/FinancialViewModel.kt', 'w') as f:
    f.write(content)

print("Balance logic updated.")
