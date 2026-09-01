import re

with open('app/src/main/java/com/example/ui/screens/ReportsScreen.kt', 'r') as f:
    content = f.read()

# Make it nicely formatted first
content = re.sub(r'(?<!\n)import ', '\nimport ', content)
content = re.sub(r'([A-Za-z0-9])import', r'\1\nimport', content)

# 1. Update Enum
content = content.replace("enum class ChartViewMode {\n    WEEKLY", "enum class ChartViewMode {\n    DAILY,\n    WEEKLY")

# 2. Update state variables
content = content.replace("var viewMode by remember { mutableStateOf(ChartViewMode.WEEKLY) }", 
                          "var viewMode by remember { mutableStateOf(ChartViewMode.DAILY) }\n    var selectedDateMs by remember { mutableStateOf(System.currentTimeMillis()) }")

# 3. Update summary metrics logic
old_totals = """    val totalSalesVal = chartDataPoints.sumOf { it.sales }
    val totalProfitVal = chartDataPoints.sumOf { it.profit }
    val totalInvoicesVal = chartDataPoints.sumOf { it.invoiceCount }"""

new_totals = """    val dailySales = remember(sales, selectedDateMs) {
        val startOfDay = com.example.ui.components.getStartOfDayForReports(selectedDateMs)
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000) - 1
        sales.filter { it.date in startOfDay..endOfDay }
    }
    
    val totalSalesVal = if (viewMode == ChartViewMode.DAILY) dailySales.sumOf { it.salePrice * it.quantity } else chartDataPoints.sumOf { it.sales }
    val totalProfitVal = if (viewMode == ChartViewMode.DAILY) dailySales.sumOf { (it.salePrice - it.costPrice) * it.quantity } else chartDataPoints.sumOf { it.profit }
    val totalInvoicesVal = if (viewMode == ChartViewMode.DAILY) dailySales.map { it.transactionId }.distinct().size else chartDataPoints.sumOf { it.invoiceCount }"""
content = content.replace(old_totals, new_totals)

# 4. Segmented Control Replacement
segmented_target = """            // 3-Way Segmented View Switcher (Weekly / Monthly / Yearly)"""
segmented_end = """        // Summary Metric Cards"""

start_idx = content.find(segmented_target)
end_idx = content.find(segmented_end)

if start_idx != -1 and end_idx != -1:
    new_segmented = """            // 4-Way Segmented View Switcher (Daily / Weekly / Monthly / Yearly)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    // Daily Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewMode = ChartViewMode.DAILY
                                selectedPointIndex = null
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (viewMode == ChartViewMode.DAILY) MaterialTheme.colorScheme.primary else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (viewMode == ChartViewMode.DAILY) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "يومي",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (viewMode == ChartViewMode.DAILY) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                    // Weekly Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewMode = ChartViewMode.WEEKLY
                                selectedPointIndex = null
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (viewMode == ChartViewMode.WEEKLY) MaterialTheme.colorScheme.primary else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (viewMode == ChartViewMode.WEEKLY) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "أسبوعي",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (viewMode == ChartViewMode.WEEKLY) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                    // Monthly Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewMode = ChartViewMode.MONTHLY
                                selectedPointIndex = null
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (viewMode == ChartViewMode.MONTHLY) MaterialTheme.colorScheme.primary else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (viewMode == ChartViewMode.MONTHLY) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "شهري",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (viewMode == ChartViewMode.MONTHLY) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                    // Yearly Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewMode = ChartViewMode.YEARLY
                                selectedPointIndex = null
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (viewMode == ChartViewMode.YEARLY) MaterialTheme.colorScheme.primary else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (viewMode == ChartViewMode.YEARLY) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "سنوي",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (viewMode == ChartViewMode.YEARLY) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
        
"""
    content = content[:start_idx] + new_segmented + content[end_idx:]


# 5. Insert Calendar and conditionally show Chart
old_chart = """        // Interactive Chart Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),"""
new_chart = """        // Interactive Chart Card or Calendar
        item {
            if (viewMode == ChartViewMode.DAILY) {
                com.example.ui.components.AnalyticsCalendar(
                    activeDatesMs = sales.map { it.date },
                    selectedDateMs = selectedDateMs,
                    onDateSelected = { selectedDateMs = it },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),"""
content = content.replace(old_chart, new_chart)

content = content.replace("""ChartViewMode.YEARLY -> "مبيعات وأرباح الأشهُر (سنوي)"
                                }""", """ChartViewMode.YEARLY -> "مبيعات وأرباح الأشهُر (سنوي)"
                                    else -> ""
                                }""")

# And close the else block for the chart
old_details = """                    // Tooltip Detail Popup
                    AnimatedVisibility(
                        visible = selectedPointIndex != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {"""
new_details = """                    } // End of else block for chart
                    
                    // Tooltip Detail Popup
                    AnimatedVisibility(
                        visible = selectedPointIndex != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {"""
content = content.replace(old_details, new_details)


with open('app/src/main/java/com/example/ui/screens/ReportsScreen.kt', 'w') as f:
    f.write(content)

