import re

def fix_plans():
    with open('app/src/main/java/com/example/ui/PlansScreen.kt', 'r') as f:
        content = f.read()
        
    imports = """import com.example.ui.theme.GeometricIncome
import com.example.ui.theme.GeometricExpense
import androidx.compose.foundation.layout.imePadding
"""
    content = content.replace("import com.example.data.FinancialPlan", imports + "import com.example.data.FinancialPlan")

    with open('app/src/main/java/com/example/ui/PlansScreen.kt', 'w') as f:
        f.write(content)

def fix_reports():
    with open('app/src/main/java/com/example/ui/ReportsScreen.kt', 'r') as f:
        content = f.read()

    imports = """import com.example.ui.theme.GeometricIncome
import com.example.ui.theme.GeometricExpense
"""
    content = content.replace("import com.example.data.EntryStatus", "import com.example.data.EntryStatus\n" + imports)

    with open('app/src/main/java/com/example/ui/ReportsScreen.kt', 'w') as f:
        f.write(content)

fix_plans()
fix_reports()
