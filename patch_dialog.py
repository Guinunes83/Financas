import re

with open('app/src/main/java/com/example/ui/AddEditEntryDialog.kt', 'r') as f:
    content = f.read()

target = """    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {"""

replacement = """    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets.ime
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {"""

if target in content:
    content = content.replace(target, replacement)
    with open('app/src/main/java/com/example/ui/AddEditEntryDialog.kt', 'w') as f:
        f.write(content)
    print("Replaced successfully")
else:
    print("Target not found. Looking for similar...")
    import difflib
    print(difflib.get_close_matches(target, [content[i:i+len(target)] for i in range(len(content)-len(target))], n=1))
