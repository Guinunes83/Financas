import re

with open('app/src/main/java/com/example/ui/SettingsScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'colors = CardDefaults.cardColors(containerColor = Color.White),',
    'colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),'
)

with open('app/src/main/java/com/example/ui/SettingsScreen.kt', 'w') as f:
    f.write(content)

