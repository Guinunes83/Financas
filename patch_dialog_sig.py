with open('app/src/main/java/com/example/ui/AddEditEntryDialog.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'categories: List<FinancialCategory>,',
    'categories: List<FinancialCategory>,\n    plans: List<com.example.data.FinancialPlan>,'
)

content = content.replace(
    'FilterChip(\n                    selected = type == EntryType.INCOME,\n                    onClick = { type = EntryType.INCOME },\n                    label = { Text("Receita") }\n                )',
    'FilterChip(\n                    selected = type == EntryType.INCOME,\n                    onClick = { type = EntryType.INCOME },\n                    label = { Text("Receita") }\n                )\n                FilterChip(\n                    selected = type == EntryType.PLAN,\n                    onClick = { type = EntryType.PLAN },\n                    label = { Text("Plano") }\n                )'
)

with open('app/src/main/java/com/example/ui/AddEditEntryDialog.kt', 'w') as f:
    f.write(content)
