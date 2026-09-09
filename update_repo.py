with open('app/src/main/java/com/example/data/FinancialRepository.kt', 'r') as f:
    content = f.read()

if 'val allPlans' not in content:
    content = content.replace('private val categoryDao: CategoryDao', 'private val categoryDao: CategoryDao,\n    private val planDao: PlanDao')
    content = content.replace('val allCategories: Flow<List<FinancialCategory>> = categoryDao.getAllCategories()', 'val allCategories: Flow<List<FinancialCategory>> = categoryDao.getAllCategories()\n    val allPlans: Flow<List<FinancialPlan>> = planDao.getAllPlans()')
    
    methods = """
    suspend fun insertPlan(plan: FinancialPlan) = planDao.insertPlan(plan)
    suspend fun updatePlan(plan: FinancialPlan) = planDao.updatePlan(plan)
    suspend fun deletePlan(plan: FinancialPlan) = planDao.deletePlan(plan)
"""
    content = content.replace('suspend fun deleteCategory(category: FinancialCategory) = categoryDao.deleteCategory(category)', 'suspend fun deleteCategory(category: FinancialCategory) = categoryDao.deleteCategory(category)' + methods)

with open('app/src/main/java/com/example/data/FinancialRepository.kt', 'w') as f:
    f.write(content)
print("Repo updated!")
