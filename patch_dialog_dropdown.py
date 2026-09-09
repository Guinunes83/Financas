import re

with open('app/src/main/java/com/example/ui/AddEditEntryDialog.kt', 'r') as f:
    content = f.read()

# Add planId state
if 'var planId by remember' not in content:
    content = content.replace('var category by remember { mutableStateOf(entry?.category ?: "") }', 
                              'var category by remember { mutableStateOf(entry?.category ?: "") }\n    var planId by remember { mutableStateOf(entry?.planId) }')

# Now the category ExposedDropdownMenuBox. Let's replace the whole ExposedDropdownMenuBox with a conditional one
old_dropdown = """                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { 
                            category = it
                            categoryExpanded = true
                        },
                        label = { Text("Categoria") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        val filteredCategories = categories.filter { it.name.contains(category, ignoreCase = true) }
                        
                        if (filteredCategories.isEmpty() && category.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Nenhuma categoria (Crie uma nova)") },
                                onClick = { categoryExpanded = false },
                                enabled = false
                            )
                        } else {
                            filteredCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = { 
                                        category = cat.name
                                        categoryExpanded = false 
                                    }
                                )
                            }
                        }
                        
                        Divider(modifier = Modifier.padding(horizontal = 8.dp))
                        DropdownMenuItem(
                            text = { Text("Criar nova categoria...", color = MaterialTheme.colorScheme.primary) },
                            onClick = { 
                                showNewCategoryDialog = true
                                categoryExpanded = false 
                            }
                        )
                    }
                }"""

new_dropdown = """                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = if (type == EntryType.PLAN) plans.find { it.id == planId }?.name ?: category else category,
                        onValueChange = { 
                            category = it
                            if (type == EntryType.PLAN) planId = null
                            categoryExpanded = true
                        },
                        label = { Text(if (type == EntryType.PLAN) "Plano" else "Categoria") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        if (type == EntryType.PLAN) {
                            val filteredPlans = plans.filter { it.name.contains(category, ignoreCase = true) }
                            if (filteredPlans.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Nenhum plano encontrado") },
                                    onClick = { categoryExpanded = false },
                                    enabled = false
                                )
                            } else {
                                filteredPlans.forEach { p ->
                                    DropdownMenuItem(
                                        text = { Text(p.name) },
                                        onClick = { 
                                            planId = p.id
                                            category = p.name
                                            categoryExpanded = false 
                                        }
                                    )
                                }
                            }
                        } else {
                            val filteredCategories = categories.filter { it.name.contains(category, ignoreCase = true) }
                            if (filteredCategories.isEmpty() && category.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Nenhuma categoria (Crie uma nova)") },
                                    onClick = { categoryExpanded = false },
                                    enabled = false
                                )
                            } else {
                                filteredCategories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.name) },
                                        onClick = { 
                                            category = cat.name
                                            categoryExpanded = false 
                                        }
                                    )
                                }
                            }
                            Divider(modifier = Modifier.padding(horizontal = 8.dp))
                            DropdownMenuItem(
                                text = { Text("Criar nova categoria...", color = MaterialTheme.colorScheme.primary) },
                                onClick = { 
                                    showNewCategoryDialog = true
                                    categoryExpanded = false 
                                }
                            )
                        }
                    }
                }"""

content = content.replace(old_dropdown, new_dropdown)

# Update FinancialEntry constructor
old_constructor = """                    val newEntry = FinancialEntry(
                        id = entry?.id ?: 0,
                        type = type,
                        dateMillis = dateMillis,
                        name = name,
                        category = category,
                        amount = amount,
                        notes = notes,
                        recurrenceType = recurrenceType,
                        installmentCount = if (recurrenceType == RecurrenceType.PARCELADO) installments else null,
                        status = status
                    )"""

new_constructor = """                    val newEntry = FinancialEntry(
                        id = entry?.id ?: 0,
                        type = type,
                        dateMillis = dateMillis,
                        name = name,
                        category = category,
                        amount = amount,
                        notes = notes,
                        recurrenceType = recurrenceType,
                        installmentCount = if (recurrenceType == RecurrenceType.PARCELADO) installments else null,
                        status = status,
                        planId = if (type == EntryType.PLAN) planId else null
                    )"""

content = content.replace(old_constructor, new_constructor)

# Adjust enabled check
old_enabled = """enabled = name.isNotBlank() && category.isNotBlank() && amountString.isNotBlank() && 
                    (recurrenceType != RecurrenceType.PARCELADO || installmentCountStr.isNotBlank())"""

new_enabled = """enabled = name.isNotBlank() && (category.isNotBlank() || type == EntryType.PLAN) && amountString.isNotBlank() && 
                    (recurrenceType != RecurrenceType.PARCELADO || installmentCountStr.isNotBlank())"""

content = content.replace(old_enabled, new_enabled)

with open('app/src/main/java/com/example/ui/AddEditEntryDialog.kt', 'w') as f:
    f.write(content)

print("Dialog logic updated.")
