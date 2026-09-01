import re

with open('app/src/main/java/com/example/ui/screens/ReportsScreen.kt', 'r') as f:
    content = f.read()

imports_end = content.find("data class ChartDataPoint")
imports = content[:imports_end]
imports = re.sub(r'(?<!\n)import ', '\nimport ', imports)
imports = re.sub(r'([A-Za-z0-9])import', r'\1\nimport', imports)

new_content = imports + """data class ChartDataPoint(
    val label: String,
    val sales: Double,
    val profit: Double,
    val cost: Double,
    val itemCount: Int,
    val invoiceCount: Int
)

enum class ChartViewMode {
    DAILY,
    WEEKLY,   // أسبوعي (7 أيام)
    MONTHLY,  // شهري (أيام الشهر)
    YEARLY    // سنوي (12 شهر)
}

@Composable
fun ReportsScreen(
    sales: List<SaleEntity>
) {
    var viewMode by remember { mutableStateOf(ChartViewMode.DAILY) }
    var selectedDateMs by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    // Generate Chart Aggregations based on Weekly, Monthly, or Yearly mode
    val chartDataPoints = remember(sales, viewMode) {
        val cal = Calendar.getInstance()
        val list = mutableListOf<ChartDataPoint>()
        
        if (viewMode == ChartViewMode.DAILY) {
            // We don't generate data points for the line chart when in DAILY mode
        } else {
            when (viewMode) {
                ChartViewMode.WEEKLY -> {
                    // 7 Days (Last 7 Days)
                    val dayFormat = SimpleDateFormat("EEE\\ndd/MM", Locale("ar"))
                    for (i in 6 downTo 0) {
                        val dayCal = (cal.clone() as Calendar).apply {
                            add(Calendar.DAY_OF_YEAR, -i)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val startMs = dayCal.timeInMillis
                        val endMs = startMs + (24 * 60 * 60 * 1000) - 1

                        val label = dayFormat.format(Date(startMs))
                        val daySales = sales.filter { it.date in startMs..endMs }

                        val salesVal = daySales.sumOf { it.salePrice * it.quantity }
                        val costVal = daySales.sumOf { it.costPrice * it.quantity }
                        val profitVal = salesVal - costVal
                        val items = daySales.sumOf { it.quantity }
                        val invs = daySales.map { it.transactionId }.distinct().size

                        list.add(ChartDataPoint(label, salesVal, profitVal, costVal, items, invs))
                    }
                }
                ChartViewMode.MONTHLY -> {
                    // Last 30 Days (Grouped every 3 days approx to fit UI, or 15 days)
                    val dayFormat = SimpleDateFormat("dd/MM", Locale("ar"))
                    for (i in 29 downTo 0 step 3) {
                        val startDayCal = (cal.clone() as Calendar).apply {
                            add(Calendar.DAY_OF_YEAR, -i)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val endDayCal = (cal.clone() as Calendar).apply {
                            add(Calendar.DAY_OF_YEAR, -(i - 2))
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                            set(Calendar.MILLISECOND, 999)
                        }

                        val startMs = startDayCal.timeInMillis
                        val endMs = endDayCal.timeInMillis

                        val label = dayFormat.format(Date(startMs))
                        val periodSales = sales.filter { it.date in startMs..endMs }

                        val salesVal = periodSales.sumOf { it.salePrice * it.quantity }
                        val costVal = periodSales.sumOf { it.costPrice * it.quantity }
                        val profitVal = salesVal - costVal
                        val items = periodSales.sumOf { it.quantity }
                        val invs = periodSales.map { it.transactionId }.distinct().size

                        list.add(ChartDataPoint(label, salesVal, profitVal, costVal, items, invs))
                    }
                }
                ChartViewMode.YEARLY -> {
                    // Last 12 Months
                    val monthFormat = SimpleDateFormat("MMM", Locale("ar"))
                    for (i in 11 downTo 0) {
                        val monthCal = (cal.clone() as Calendar).apply {
                            add(Calendar.MONTH, -i)
                            set(Calendar.DAY_OF_MONTH, 1)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val startMs = monthCal.timeInMillis
                        monthCal.add(Calendar.MONTH, 1)
                        val endMs = monthCal.timeInMillis - 1

                        val label = monthFormat.format(Date(startMs))
                        val monthSales = sales.filter { it.date in startMs..endMs }

                        val salesVal = monthSales.sumOf { it.salePrice * it.quantity }
                        val costVal = monthSales.sumOf { it.costPrice * it.quantity }
                        val profitVal = salesVal - costVal
                        val items = monthSales.sumOf { it.quantity }
                        val invs = monthSales.map { it.transactionId }.distinct().size

                        list.add(ChartDataPoint(label, salesVal, profitVal, costVal, items, invs))
                    }
                }
                else -> {}
            }
        }
        
        list
    }

    val dailySales = remember(sales, selectedDateMs) {
        val startOfDay = com.example.ui.components.getStartOfDayForReports(selectedDateMs)
        val endOfDay = startOfDay + (24 * 60 * 60 * 1000) - 1
        sales.filter { it.date in startOfDay..endOfDay }
    }
    
    val totalSalesVal = if (viewMode == ChartViewMode.DAILY) dailySales.sumOf { it.salePrice * it.quantity } else chartDataPoints.sumOf { it.sales }
    val totalProfitVal = if (viewMode == ChartViewMode.DAILY) dailySales.sumOf { (it.salePrice - it.costPrice) * it.quantity } else chartDataPoints.sumOf { it.profit }
    val totalInvoicesVal = if (viewMode == ChartViewMode.DAILY) dailySales.map { it.transactionId }.distinct().size else chartDataPoints.sumOf { it.invoiceCount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            // Header & Subtitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "التقارير والتحليلات",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "تتبع أداء المبيعات والأرباح",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4-Way Segmented View Switcher (Daily / Weekly / Monthly / Yearly)
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
                                style = MaterialTheme.typography.bodySmall.copy(
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
                                style = MaterialTheme.typography.bodySmall.copy(
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
                                style = MaterialTheme.typography.bodySmall.copy(
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
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (viewMode == ChartViewMode.YEARLY) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }

        // Summary Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PointOfSale,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "إجمالي المبيعات",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${String.format(Locale.ENGLISH, "%,.2f", totalSalesVal)} د.أ",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = Color(0xFF027A48),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "صافي الأرباح",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${String.format(Locale.ENGLISH, "%,.2f", totalProfitVal)} د.أ",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF027A48)
                            )
                        )
                    }
                }
            }
        }

        // Interactive Chart Card or Calendar
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
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Header with Legend
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Assessment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (viewMode) {
                                        ChartViewMode.WEEKLY -> "مبيعات وأرباح الأيام (أسبوعي)"
                                        ChartViewMode.MONTHLY -> "مبيعات وأرباح الأيام (شهري)"
                                        ChartViewMode.YEARLY -> "مبيعات وأرباح الأشهُر (سنوي)"
                                        else -> ""
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            // Legend
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "مبيعات", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF027A48))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "أرباح", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "اضغط على أي يوم أو شهر لعرض التفاصيل الكاملة",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Canvas Bar Chart
                        val salesColor = MaterialTheme.colorScheme.primary
                        val profitColor = Color(0xFF027A48)
                        val maxSales = chartDataPoints.maxOfOrNull { it.sales }?.takeIf { it > 0 } ?: 1.0
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .width((chartDataPoints.size * 60).dp.coerceAtLeast(400.dp))
                                    .fillMaxHeight()
                                    .pointerInput(chartDataPoints) {
                                        detectTapGestures { offset ->
                                            val barWidth = size.width / chartDataPoints.size
                                            val clickedIndex = (offset.x / barWidth).toInt()
                                            if (clickedIndex in chartDataPoints.indices) {
                                                selectedPointIndex = if (selectedPointIndex == clickedIndex) null else clickedIndex
                                            }
                                        }
                                    }
                            ) {
                                val chartWidth = size.width
                                val chartHeight = size.height - 30.dp.toPx()
                                val barGroupWidth = chartWidth / chartDataPoints.size
                                val barWidth = (barGroupWidth * 0.4f).coerceAtMost(30.dp.toPx())
                                val barSpacing = barGroupWidth * 0.05f

                                chartDataPoints.forEachIndexed { index, data ->
                                    val xOffset = index * barGroupWidth + (barGroupWidth / 2f)

                                    // Sales Bar
                                    val salesBarHeight = ((data.sales / maxSales) * chartHeight).toFloat()
                                    drawRoundRect(
                                        color = salesColor.copy(alpha = if (selectedPointIndex == null || selectedPointIndex == index) 1f else 0.3f),
                                        topLeft = Offset(xOffset - barWidth - barSpacing, chartHeight - salesBarHeight),
                                        size = Size(barWidth, salesBarHeight),
                                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                    )

                                    // Profit Bar
                                    val profitBarHeight = ((data.profit / maxSales) * chartHeight).toFloat()
                                    drawRoundRect(
                                        color = profitColor.copy(alpha = if (selectedPointIndex == null || selectedPointIndex == index) 1f else 0.3f),
                                        topLeft = Offset(xOffset + barSpacing, chartHeight - profitBarHeight),
                                        size = Size(barWidth, profitBarHeight),
                                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                    )

                                    // Draw Label at bottom
                                    val lines = data.label.split("\\n")
                                    val paint = android.graphics.Paint().apply {
                                        color = android.graphics.Color.GRAY
                                        textSize = 10.sp.toPx()
                                        textAlign = android.graphics.Paint.Align.CENTER
                                        isAntiAlias = true
                                    }
                                    
                                    var yTextOffset = chartHeight + 16.dp.toPx()
                                    lines.forEach { line ->
                                        drawContext.canvas.nativeCanvas.drawText(
                                            line,
                                            xOffset,
                                            yTextOffset,
                                            paint
                                        )
                                        yTextOffset += 12.dp.toPx()
                                    }
                                }
                            }
                        }

                        // Tooltip Detail Popup
                        AnimatedVisibility(
                            visible = selectedPointIndex != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            selectedPointIndex?.let { index ->
                                val data = chartDataPoints.getOrNull(index)
                                if (data != null) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Info,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "تفاصيل فترة: ${data.label.replace("\\n", " ")}",
                                                        style = MaterialTheme.typography.titleMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    )
                                                }

                                                Text(
                                                    text = "إغلاق ✕",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                    modifier = Modifier.clickable { selectedPointIndex = null }
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text(text = "المبيعات", style = MaterialTheme.typography.labelSmall)
                                                    Text(
                                                        text = "${String.format(Locale.ENGLISH, "%,.2f", data.sales)} د.أ",
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    )
                                                }

                                                Column {
                                                    Text(text = "الربح الصافي", style = MaterialTheme.typography.labelSmall)
                                                    Text(
                                                        text = "${String.format(Locale.ENGLISH, "%,.2f", data.profit)} د.أ",
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFF027A48)
                                                        )
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Text(
                                                text = "• عدد الفواتير: ${data.invoiceCount} فاتورة | القطع المباعة: ${data.itemCount} قطعة",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Overview Metric Summary
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "نظرة عامة على البيانات المعروضة",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "• إجمالي عدد الفواتير الصادرة: $totalInvoicesVal فاتورة",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                    Text(
                        text = "• إجمالي حجم المبيعات (الإيرادات): ${String.format(Locale.ENGLISH, "%,.2f", totalSalesVal)} د.أ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                    Text(
                        text = "• إجمالي الأرباح المحققة: ${String.format(Locale.ENGLISH, "%,.2f", totalProfitVal)} د.أ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}
"""

with open('app/src/main/java/com/example/ui/screens/ReportsScreen.kt', 'w') as f:
    f.write(new_content)
