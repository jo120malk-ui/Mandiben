package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CompanyEntity
import com.example.data.local.CustomerEntity
import com.example.data.local.ReceiptEntity
import com.example.data.local.SaleEntity
import com.example.ui.components.CustomerAutoCompleteTextField
import com.example.util.PdfInvoiceGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class StatementMovement(
    val id: String,
    val type: String, // "invoice" or "receipt"
    val title: String,
    val date: Long,
    val amount: Double,
    var runningBalance: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountStatementScreen(
    customers: List<CustomerEntity>,
    sales: List<SaleEntity>,
    receipts: List<ReceiptEntity>,
    company: CompanyEntity? = null,
    selectedCustomerName: String,
    onSelectCustomer: (String) -> Unit
) {
    val context = LocalContext.current

    // Compute Customer Movements & Running Balance
    val customerSales = sales.filter { it.customerName == selectedCustomerName }
    val customerReceipts = receipts.filter { it.customerName == selectedCustomerName }

    // Group sales into invoices
    val invoiceGroups = customerSales.groupBy { it.transactionId }.map { (_, items) ->
        val first = items.first()
        val total = items.sumOf { it.salePrice * it.quantity }
        StatementMovement(
            id = first.transactionId,
            type = "invoice",
            title = "فاتورة مبيعات #${first.invoiceNumber}",
            date = first.date,
            amount = total
        )
    }

    val receiptMovements = customerReceipts.map { r ->
        StatementMovement(
            id = "rcpt_${r.id}",
            type = "receipt",
            title = "سند قبض #${r.receiptNumber}",
            date = r.date,
            amount = r.amount
        )
    }

    // Combine and sort chronologically (oldest first for running balance calculation)
    val chronologicalMovements = remember(invoiceGroups, receiptMovements) {
        val combined = (invoiceGroups + receiptMovements).sortedBy { it.date }
        var currentBalance = 0.0
        combined.map { m ->
            if (m.type == "invoice") {
                currentBalance += m.amount // Sale/Debt increases balance due
            } else {
                currentBalance -= m.amount // Receipt decreases balance due
            }
            m.copy(runningBalance = currentBalance)
        }.sortedByDescending { it.date } // Display newest first
    }

    val totalSalesVal = invoiceGroups.sumOf { it.amount }
    val totalReceiptsVal = receiptMovements.sumOf { it.amount }
    val remainingBalance = totalSalesVal - totalReceiptsVal

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Customer Selector with Smart Search/Autocomplete
            var customerInputQuery by remember(selectedCustomerName) { mutableStateOf(selectedCustomerName) }

            Text(
                text = "البحث السريع عن عميل (أدخل الحروف الأولى)",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            CustomerAutoCompleteTextField(
                value = customerInputQuery,
                onValueChange = { newQuery ->
                    customerInputQuery = newQuery
                    onSelectCustomer(newQuery)
                },
                customers = customers,
                label = "اكتب اسم أو رقم العميل...",
                onCustomerSelected = { selected ->
                    customerInputQuery = selected.name
                    onSelectCustomer(selected.name)
                }
            )
        }

        // Summary Header Card (Matches Screenshot exactly!)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "الرصيد المتبقي (له)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${String.format(Locale.ENGLISH, "%,.2f", remainingBalance)} د.أ",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (remainingBalance > 0) MaterialTheme.colorScheme.error else Color(0xFF027A48)
                        )
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "إجمالي المبيعات",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(
                                text = "${String.format(Locale.ENGLISH, "%,.2f", totalSalesVal)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "إجمالي المقبوضات",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(
                                text = "${String.format(Locale.ENGLISH, "%,.2f", totalReceiptsVal)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF027A48)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Account Movements List Section
        item {
            Text(
                text = "حركات الحساب",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (chronologicalMovements.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد حركات مالية مسجلة لهذا العميل",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        } else {
            items(chronologicalMovements) { item ->
                val isInvoice = item.type == "invoice"
                val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale("ar")).format(Date(item.date))

                Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isInvoice) MaterialTheme.colorScheme.errorContainer
                                            else Color(0xFFD1FADF)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isInvoice) Icons.Default.ReceiptLong else Icons.Default.Payments,
                                        contentDescription = null,
                                        tint = if (isInvoice) MaterialTheme.colorScheme.error else Color(0xFF027A48),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = dateStr,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "${if (isInvoice) "-" else "+"} ${String.format(Locale.ENGLISH, "%,.2f", item.amount)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isInvoice) MaterialTheme.colorScheme.error else Color(0xFF027A48)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Running Balance Strip
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "الرصيد المتراكم",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = "${String.format(Locale.ENGLISH, "%,.2f", item.runningBalance)}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Print PDF Button
                Button(
                    onClick = {
                        val pdfFile = PdfInvoiceGenerator.generateStatementPdf(
                            context = context,
                            customerName = selectedCustomerName,
                            remainingBalance = remainingBalance,
                            totalSales = totalSalesVal,
                            totalReceipts = totalReceiptsVal,
                            movements = chronologicalMovements,
                            company = company
                        )
                        PdfInvoiceGenerator.printPdf(context, pdfFile, "Statement_${selectedCustomerName}")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(imageVector = Icons.Default.Print, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "طباعة الكشف",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Share PDF Button
                Button(
                    onClick = {
                        val pdfFile = PdfInvoiceGenerator.generateStatementPdf(
                            context = context,
                            customerName = selectedCustomerName,
                            remainingBalance = remainingBalance,
                            totalSales = totalSalesVal,
                            totalReceipts = totalReceiptsVal,
                            movements = chronologicalMovements,
                            company = company
                        )
                        PdfInvoiceGenerator.sharePdf(context, pdfFile, "مشاركة كشف الحساب PDF")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "مشاركة PDF",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}
