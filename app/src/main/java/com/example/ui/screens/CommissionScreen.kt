package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SaleEntity
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommissionScreen(sales: List<SaleEntity>) {
    var selectedPeriod by remember { mutableStateOf(Period.TODAY) }
    var commissionPercentageStr by remember { mutableStateOf("") }
    
    val commissionPercentage = commissionPercentageStr.toDoubleOrNull() ?: 0.0
    
    // Filter sales based on period
    val filteredSales = remember(sales, selectedPeriod) {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = now
        
        val startTime = when (selectedPeriod) {
            Period.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            Period.WEEK -> {
                // Set to start of the week (usually Sunday)
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            Period.MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
        }
        
        // Exclude returned items (where quantity might be negative, or we just count actual sold amounts)
        // Usually, in SaleEntity, if it's a valid sale, quantity is positive. If it's a return, it might be handled differently, 
        // but here we just take valid sales and multiply quantity * salePrice.
        sales.filter { it.date >= startTime }
    }
    
    val totalSales = filteredSales.sumOf { it.quantity * it.salePrice }
    val commissionAmount = totalSales * (commissionPercentage / 100.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Period selection using standard TabRow or Row of FilterChips for compatibility
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = selectedPeriod == Period.TODAY,
                onClick = { selectedPeriod = Period.TODAY },
                label = { Text("اليوم", modifier = Modifier.padding(horizontal = 8.dp)) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = selectedPeriod == Period.WEEK,
                onClick = { selectedPeriod = Period.WEEK },
                label = { Text("هذا الأسبوع", modifier = Modifier.padding(horizontal = 8.dp)) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = selectedPeriod == Period.MONTH,
                onClick = { selectedPeriod = Period.MONTH },
                label = { Text("هذا الشهر", modifier = Modifier.padding(horizontal = 8.dp)) },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Sales total card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "إجمالي مبيعات الفترة",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format("%.2f د.أ", totalSales),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Percentage input
        OutlinedTextField(
            value = commissionPercentageStr,
            onValueChange = { commissionPercentageStr = it },
            label = { Text("نسبة العمولة (%)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Commission amount card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "قيمة العمولة المستحقة",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format("%.2f د.أ", commissionAmount),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

enum class Period {
    TODAY, WEEK, MONTH
}
