package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.aspectRatio

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CompanyEntity
import com.example.data.local.ProductEntity
import com.example.data.local.SaleEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    company: CompanyEntity?,
    products: List<ProductEntity>,
    lowStockProducts: List<ProductEntity>,
    sales: List<SaleEntity>,
    onOpenProductEdit: (ProductEntity) -> Unit,
    onNavigateToUpgrade: () -> Unit,
    onNavigateToSales: () -> Unit = {},
    onNavigateToProducts: () -> Unit = {},
    onNavigateToCustomers: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToReceipts: () -> Unit = {},
    onNavigateToDisbursements: () -> Unit = {},
    onNavigateToAccountStatement: () -> Unit = {},
    onNavigateToCommission: () -> Unit = {}
) {
    val totalSalesValue = sales.sumOf { it.salePrice * it.quantity }
    val recentSales = sales.sortedByDescending { it.date }.take(4)

    // Using #F5F5F3 as requested for the main background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F3))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HeroBalanceCard(
                    totalAmount = totalSalesValue,
                    onNavigateToSales = onNavigateToSales,
                    onNavigateToPurchases = onNavigateToProducts,
                    onAddAction = onNavigateToSales
                )
            }


            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "القائمة الرئيسية",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    val items = listOf(
                        Triple("التقارير", Icons.Default.Assessment, onNavigateToReports),
                        Triple("العملاء", Icons.Default.People, onNavigateToCustomers),
                        Triple("سندات القبض", Icons.Default.Payments, onNavigateToReceipts),
                        Triple("سندات الصرف", Icons.Default.MoneyOff, onNavigateToDisbursements),
                        Triple("كشف الحساب", Icons.Default.ReceiptLong, onNavigateToAccountStatement),
                        Triple("حسابة العمولة", Icons.Default.Calculate, onNavigateToCommission)
                    )
                    
                    items.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { (title, icon, action) ->
                                Card(
                                    modifier = Modifier.weight(1f).aspectRatio(1f).clickable { action() },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(0.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = title,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.Black,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            // Fill empty spaces if not multiple of 3
                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            
            item {
                // Recent Transactions
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "آخر العمليات",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.DarkGray
                    )

                    if (recentSales.isEmpty()) {
                        Text(
                            text = "لا توجد عمليات مبيعات بعد.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    } else {
                        recentSales.forEach { sale ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color(0xFFF5F5F3)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = sale.customerName.take(1).uppercase(),
                                                style = MaterialTheme.typography.titleMedium,
                                                color = Color.Black
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(
                                                text = sale.customerName,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = SimpleDateFormat("dd MMM", Locale.ENGLISH).format(Date(sale.date)),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${String.format(Locale.ENGLISH, "%.2f", sale.salePrice * sale.quantity)} د.أ",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun HeroBalanceCard(
    totalAmount: Double,
    onNavigateToSales: () -> Unit,
    onNavigateToPurchases: () -> Unit,
    onAddAction: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(36.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1C1E21), Color(0xFF090A0B))
                )
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.06f),
                shape = RoundedCornerShape(36.dp)
            )
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Name
                Text(
                    text = "Mandiben",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                // Bell
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "إجمالي المبيعات",
                color = Color(0xFFA8A8A8),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Big Price
            val formattedAmount = String.format(Locale.ENGLISH, "%.2f", totalAmount)
            val parts = formattedAmount.split(".")
            val integerPart = parts[0]
            val decimalPart = if (parts.size > 1) "." + parts[1] else ""
            
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.ExtraBold)) {
                        append(integerPart)
                    }
                    if (decimalPart.isNotEmpty()) {
                        withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.ExtraBold)) {
                            append(decimalPart)
                        }
                    }
                    withStyle(style = SpanStyle(color = Color(0xFFFF6A00), fontWeight = FontWeight.ExtraBold)) {
                        append("jo")
                    }
                },
                fontSize = 60.sp,
                letterSpacing = (-2).sp,
                lineHeight = 60.sp
            )
            
            Text(
                text = "دينار أردني",
                color = Color(0xFFA8A8A8),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sales Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF141517))
                        .border(1.dp, Color(0xFF282A2D), RoundedCornerShape(24.dp))
                        .clickable { onNavigateToSales() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("المبيعات", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Add Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF181A1D))
                        .border(1.dp, Color(0xFF2A2D31), CircleShape)
                        .clickable { onAddAction() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "إضافة",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Purchases Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF141517))
                        .border(1.dp, Color(0xFF282A2D), RoundedCornerShape(24.dp))
                        .clickable { onNavigateToPurchases() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("المشتريات", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Status Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF161819))
                    .border(1.dp, Color(0xFF292B2E), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "البيانات محدثة",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
