import re

with open('app/src/main/java/com/example/ui/FinancialViewModel.kt', 'r') as f:
    content = f.read()

# Add import
content = content.replace('import com.example.data.FinancialCategory', 'import com.example.data.FinancialCategory\nimport com.example.data.FinancialPlan')

# Add to FinancialUiState
if 'val plans' not in content:
    content = content.replace('val categories: List<FinancialCategory> = emptyList(),', 'val categories: List<FinancialCategory> = emptyList(),\n    val plans: List<FinancialPlan> = emptyList(),')
    content = content.replace('categories = categories,', 'categories = categories,\n            plans = plans,')

    # Update stateIn combine
    combine_block = """    val uiState: StateFlow<FinancialUiState> = combine(
        repository.allEntries,
        repository.allCategories,
        repository.allPlans
    ) { entries, categories, plans ->"""
    content = re.sub(r'    val uiState: StateFlow<FinancialUiState> = combine\(\s*repository\.allEntries,\s*repository\.allCategories\s*\) \{ entries, categories ->', combine_block, content)

# Add plan methods
plan_methods = """
    fun insertPlan(plan: FinancialPlan) = viewModelScope.launch {
        repository.insertPlan(plan)
    }

    fun updatePlan(plan: FinancialPlan) = viewModelScope.launch {
        repository.updatePlan(plan)
    }

    fun deletePlan(plan: FinancialPlan) = viewModelScope.launch {
        repository.deletePlan(plan)
    }
"""
content = content.replace('fun insertCategory(category: FinancialCategory) = viewModelScope.launch {', plan_methods + '\n    fun insertCategory(category: FinancialCategory) = viewModelScope.launch {')

with open('app/src/main/java/com/example/ui/FinancialViewModel.kt', 'w') as f:
    f.write(content)

print("ViewModel updated!")
