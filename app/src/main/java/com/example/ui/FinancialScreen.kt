package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.EntryType
import com.example.data.FinancialEntry
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialScreen(
    viewModel: FinancialViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<FinancialEntry?>(null) }
    var currentTab by remember { mutableStateOf("Principal") }

    Scaffold(
        modifier = modifier,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Financeiro",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFEADDFF), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "EF",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF21005D)
                    )
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavButton(icon = Icons.Default.List, label = "Principal", selected = currentTab == "Principal") { currentTab = "Principal" }
                    BottomNavButton(icon = Icons.Default.TrendingUp, label = "Relatórios", selected = currentTab == "Relatórios") { currentTab = "Relatórios" }
                    
                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp),
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
                        modifier = Modifier.size(52.dp).testTag("add_entry_fab")
                    ) {
                        Icon(
                            Icons.Default.Add, 
                            contentDescription = "Adicionar Lançamento", 
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    BottomNavButton(icon = Icons.Default.Savings, label = "Planos", selected = currentTab == "Planos") { currentTab = "Planos" }
                    BottomNavButton(icon = Icons.Default.Settings, label = "Ajustes", selected = currentTab == "Ajustes") { currentTab = "Ajustes" }
                }
            }
        }
    ) { innerPadding ->
        if (currentTab == "Principal") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                ) {
                    SpreadsheetHeader()

                    if (uiState.entries.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Nenhum lançamento. Adicione um clicando no botão +", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        val monthFormatter = remember { SimpleDateFormat("MMMM yyyy", Locale("pt", "BR")) }
                        val groupedEntries = uiState.entries
                            .sortedByDescending { it.dateMillis }
                            .groupBy { 
                                monthFormatter.format(Date(it.dateMillis)).replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() } 
                            }
                        
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            groupedEntries.forEach { (monthString, entriesForMonth) ->
                                item(key = "header_$monthString") {
                                    MonthSeparator(monthString)
                                }
                                items(entriesForMonth, key = { it.id }) { entry ->
                                    SpreadsheetRow(
                                        entry = entry,
                                        onEdit = { entryToEdit = it; showAddDialog = true },
                                        onDelete = { viewModel.deleteEntry(it) },
                                        onStatusChange = { viewModel.updateEntry(it) }
                                    )
                                }
                                item(key = "footer_$monthString") {
                                    val monthIncome = entriesForMonth.filter { it.type == EntryType.INCOME }.sumOf { it.amount }
                                    val monthExpense = entriesForMonth.filter { it.type == EntryType.EXPENSE }.sumOf { it.amount }
                                    val monthBalance = monthIncome - monthExpense
                                    MonthTotalRow(monthBalance)
                                }
                            }
                        }
                    }
                }
            }
        } else if (currentTab == "Ajustes") {
            Box(modifier = Modifier.padding(innerPadding)) {
                SettingsScreen(viewModel, uiState)
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Em breve: $currentTab", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    if (showAddDialog) {
        AddEditEntryDialog(
            entry = entryToEdit,
            categories = uiState.categories,
            onDismiss = {
                showAddDialog = false
                entryToEdit = null
            },
            onSave = { entry ->
                if (entry.id == 0) viewModel.insertEntry(entry)
                else viewModel.updateEntry(entry)
                showAddDialog = false
                entryToEdit = null
            },
            onSaveCategory = { categoryName ->
                viewModel.insertCategory(com.example.data.FinancialCategory(name = categoryName))
            }
        )
    }
}


@Composable
fun SpreadsheetHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("DATA", modifier = Modifier.weight(1.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("ITEM", modifier = Modifier.weight(2.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("CAT.", modifier = Modifier.weight(2f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("VALOR", modifier = Modifier.weight(2.2f), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, textAlign = TextAlign.End, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(24.dp))
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpreadsheetRow(
    entry: FinancialEntry,
    onEdit: (FinancialEntry) -> Unit,
    onDelete: (FinancialEntry) -> Unit,
    onStatusChange: (FinancialEntry) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
    val amountColor = if (entry.type == EntryType.INCOME) GeometricIncome else GeometricExpense
    val amountPrefix = if (entry.type == EntryType.INCOME) "" else "-"
    
    val pillBgColor = if (entry.type == EntryType.INCOME) GeometricIncomeBg else GeometricExpenseBg
    val pillTextColor = if (entry.type == EntryType.INCOME) GeometricIncomeText else GeometricExpenseText

    val isCompletedRow = entry.status == com.example.data.EntryStatus.COMPLETED
    val rowBackgroundColor = when {
        expanded -> GeometricSurfaceVariantExpanded
        isCompletedRow -> GeometricIncome.copy(alpha = 0.08f)
        else -> Color.Transparent
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .background(rowBackgroundColor)
            .testTag("entry_row_${entry.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatter.format(Date(entry.dateMillis)),
                modifier = Modifier.weight(1.5f),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = entry.name,
                modifier = Modifier.weight(2.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Box(
                modifier = Modifier.weight(2f),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = entry.category,
                    fontSize = 10.sp,
                    color = pillTextColor,
                    modifier = Modifier
                        .background(pillBgColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "$amountPrefix${formatCurrency(entry.amount)}",
                modifier = Modifier.weight(2.2f),
                fontSize = 11.sp,
                color = amountColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 16.dp, top = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                                Text(
                                    text = "RECORRÊNCIA:",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = entry.recurrenceType.displayName + if (entry.recurrenceType == com.example.data.RecurrenceType.PARCELADO) " (${entry.installmentCount}x)" else "",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                                Text(
                                    text = "STATUS:",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                )
                                Spacer(Modifier.width(8.dp))
                                
                                val isCompleted = entry.status == com.example.data.EntryStatus.COMPLETED
                                val completedText = if (entry.type == EntryType.INCOME) "Recebido" else "Pago"
                                
                                FilterChip(
                                    selected = !isCompleted,
                                    onClick = { onStatusChange(entry.copy(status = com.example.data.EntryStatus.PENDING)) },
                                    label = { Text("Pendente", fontSize = 10.sp) },
                                    modifier = Modifier.height(24.dp)
                                )
                                
                                Spacer(Modifier.width(8.dp))
                                
                                FilterChip(
                                    selected = isCompleted,
                                    onClick = { onStatusChange(entry.copy(status = com.example.data.EntryStatus.COMPLETED)) },
                                    label = { Text(completedText, fontSize = 10.sp) },
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                            
                            Text(
                                text = "OBSERVAÇÕES",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = entry.notes.ifBlank { "Sem observações." },
                                fontSize = 11.sp,
                                lineHeight = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "ID: #${entry.id}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onEdit(entry) },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("EDITAR", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { onDelete(entry) },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("EXCLUIR", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    }
}

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(amount)
}

@Composable
fun BottomNavButton(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    val bgColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(bgColor, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        }
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun MonthSeparator(month: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = month,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun MonthTotalRow(balance: Double) {
    val isPositive = balance >= 0
    val color = if (isPositive) GeometricIncome else GeometricExpense
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "SALDO LÍQUIDO DO MÊS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = formatCurrency(balance),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
