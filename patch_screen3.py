with open('app/src/main/java/com/example/ui/FinancialScreen.kt', 'r') as f:
    content = f.read()

old_list_start = """                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                ) {
                    SpreadsheetHeader()
                    
                    val filteredEntries = if (searchQuery.isNotBlank()) {"""

new_list_start = """                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                ) {
                    MaterialTheme(colorScheme = com.example.ui.theme.LightColorScheme) {
                        Column {
                            SpreadsheetHeader()
                            
                            val filteredEntries = if (searchQuery.isNotBlank()) {"""

content = content.replace(old_list_start, new_list_start)

old_list_end = """                                }
                            }
                        }
                    }
                }
            }
        } else if (currentTab == "Relatórios") {"""

new_list_end = """                                }
                            }
                        }
                    }
                        }
                    }
                }
            }
        } else if (currentTab == "Relatórios") {"""

content = content.replace(old_list_end, new_list_end)

with open('app/src/main/java/com/example/ui/FinancialScreen.kt', 'w') as f:
    f.write(content)
