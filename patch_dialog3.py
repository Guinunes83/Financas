with open('app/src/main/java/com/example/ui/AddEditEntryDialog.kt', 'r') as f:
    content = f.read()

target = """    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets.ime
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 32.dp),"""

replacement = """    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 32.dp),"""

if target in content:
    content = content.replace(target, replacement)
    
    if "import androidx.compose.foundation.layout.imePadding" not in content:
        content = content.replace("import androidx.compose.foundation.layout.*", "import androidx.compose.foundation.layout.*\nimport androidx.compose.foundation.layout.imePadding")
        
    with open('app/src/main/java/com/example/ui/AddEditEntryDialog.kt', 'w') as f:
        f.write(content)
    print("Replaced successfully")
else:
    print("Target not found again.")
