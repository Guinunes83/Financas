with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace('FinancialRepository(database.financialEntryDao(), database.categoryDao())', 'FinancialRepository(database.financialEntryDao(), database.categoryDao(), database.planDao())')

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
print("MainActivity updated!")
