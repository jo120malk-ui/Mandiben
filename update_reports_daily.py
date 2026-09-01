import re

with open('app/src/main/java/com/example/ui/screens/ReportsScreen.kt', 'r') as f:
    content = f.read()

# Add items import if missing
if "import androidx.compose.foundation.lazy.items" not in content:
    content = content.replace("import androidx.compose.foundation.lazy.LazyColumn", "import androidx.compose.foundation.lazy.LazyColumn\nimport androidx.compose.foundation.lazy.items")

# Add the daily list section
target = "        item { Spacer(modifier = Modifier.height(60.dp)) }"
replacement = """
        if (viewMode == ChartViewMode.DAILY) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "حركات هذا اليوم",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
            
            if (dailySales.isEmpty()) {
                item {
                    Text(
                        text = "لا توجد مبيعات في هذا اليوم.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                    )
                }
            } else {
                items(dailySales.sortedByDescending { it.date }) { sale ->
                    val timeFormat = java.text.SimpleDateFormat("hh:mm a", Locale("ar"))
                    val timeStr = timeFormat.format(java.util.Date(sale.date))
                    val profit = (sale.salePrice - sale.costPrice) * sale.quantity
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sale.productName,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${sale.quantity}x • ${String.format(Locale.ENGLISH, "%.2f", sale.salePrice)} د.أ",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = timeStr,
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${String.format(Locale.ENGLISH, "%.2f", sale.salePrice * sale.quantity)} د.أ",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ربح: ${String.format(Locale.ENGLISH, "%.2f", profit)}",
                                    style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF027A48), fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(60.dp)) }"""

content = content.replace(target, replacement)

with open('app/src/main/java/com/example/ui/screens/ReportsScreen.kt', 'w') as f:
    f.write(content)
