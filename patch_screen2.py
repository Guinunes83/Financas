import re

with open('app/src/main/java/com/example/ui/FinancialScreen.kt', 'r') as f:
    content = f.read()

# Replace Reports placeholder
old_reports = """        } else if (currentTab == "Relatórios") {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Em breve: $currentTab", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }"""

new_reports = """        } else if (currentTab == "Relatórios") {
            Box(modifier = Modifier.padding(innerPadding)) {
                ReportsScreen(uiState)
            }"""

content = content.replace(old_reports, new_reports)

# Replace Plans placeholder
old_plans = """        } else if (currentTab == "Planos") {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Em breve: $currentTab", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }"""

new_plans = """        } else if (currentTab == "Planos") {
            Box(modifier = Modifier.padding(innerPadding)) {
                PlansScreen(viewModel, uiState)
            }"""

content = content.replace(old_plans, new_plans)

with open('app/src/main/java/com/example/ui/FinancialScreen.kt', 'w') as f:
    f.write(content)

print("Screen updated with Reports and Plans!")
