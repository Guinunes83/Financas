sed -i '/TOTAL DE GASTOS MÊS/,$d' app/src/main/java/com/example/ui/FinancialScreen.kt
cat << 'INNER_EOF' >> app/src/main/java/com/example/ui/FinancialScreen.kt
            Text(
                text = "TOTAL PROGRAMADO GANHOS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatCurrency(totalScheduledIncome),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GeometricIncome.copy(alpha = 0.7f)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TOTAL PROGRAMADO GASTOS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatCurrency(totalScheduledExpense),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GeometricExpense.copy(alpha = 0.7f)
            )
        }
        
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TOTAL DE GASTOS MÊS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatCurrency(totalExpense),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GeometricExpense
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TOTAL DE GANHOS MÊS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatCurrency(totalIncome),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GeometricIncome
            )
        }
    }
}
INNER_EOF
