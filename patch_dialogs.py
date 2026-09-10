import re

with open('app/src/main/java/com/example/ui/FinancialScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('containerColor = Color.White', '')

with open('app/src/main/java/com/example/ui/FinancialScreen.kt', 'w') as f:
    f.write(content)
