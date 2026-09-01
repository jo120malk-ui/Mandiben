package com.example.ui.screens


import androidx.compose.ui.graphics.Color

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.QrCodeScanner
import com.example.ui.components.BarcodeScannerDialog
import com.example.ui.components.CustomerAutoCompleteTextField
import com.example.ui.components.ProductNotFoundDialog
import com.example.ui.components.ScannedProductDetailDialog
import com.example.util.PdfInvoiceGenerator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CartItem
import com.example.data.local.CompanyEntity
import com.example.data.local.CustomerEntity
import com.example.data.local.ProductEntity
import com.example.data.local.SaleEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class InvoiceGroup(
    val transactionId: String,
    val invoiceNumber: Int,
    val customerName: String,
    val status: String,
    val date: Long,
    val items: List<SaleEntity>,
    val totalAmount: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    sales: List<SaleEntity>,
    products: List<ProductEntity>,
    customers: List<CustomerEntity>,
    cartItems: List<CartItem>,
    company: CompanyEntity? = null,
    isNewSaleSheetOpen: Boolean,
    onOpenNewSaleSheet: () -> Unit,
    onCloseNewSaleSheet: () -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onAddToCartWithQty: ((ProductEntity, Int, Double) -> Unit)? = null,
    onUpdateCartQty: (Int, Int) -> Unit,
    onConfirmSale: (customerName: String, status: String) -> Unit,
    onDeleteInvoice: (String) -> Unit,
    onProcessReturn: (SaleEntity, Int, String) -> Unit,
    onOpenAddProductWithBarcode: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedInvoiceGroup by remember { mutableStateOf<InvoiceGroup?>(null) }
    var returnSaleItem by remember { mutableStateOf<SaleEntity?>(null) }

    // Group sales into invoices by transactionId
    val invoiceGroups = remember(sales) {
        sales.groupBy { it.transactionId }.map { (txId, items) ->
            val first = items.first()
            val total = items.sumOf { it.salePrice * it.quantity }
            InvoiceGroup(
                transactionId = txId,
                invoiceNumber = first.invoiceNumber,
                customerName = first.customerName,
                status = first.status,
                date = first.date,
                items = items,
                totalAmount = total
            )
        }.sortedByDescending { it.date }
    }

    val filteredInvoices = remember(invoiceGroups, searchQuery) {
        if (searchQuery.isBlank()) invoiceGroups
        else {
            val q = searchQuery.trim()
            invoiceGroups.filter { inv ->
                inv.customerName.contains(q, ignoreCase = true) ||
                inv.invoiceNumber.toString().contains(q) ||
                inv.items.any { it.productName.contains(q, ignoreCase = true) }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Search Filter
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("البحث برقم الفاتورة أو اسم المتجر/الزبون...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredInvoices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outlineVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد فواتير مبيعات مسجلة",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredInvoices) { invoice ->
                        val isPaid = invoice.status == "paid"
                        val dateFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(Date(invoice.date))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedInvoiceGroup = invoice },
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
                                                    if (isPaid) MaterialTheme.colorScheme.secondaryContainer
                                                    else MaterialTheme.colorScheme.errorContainer
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ReceiptLong,
                                                contentDescription = null,
                                                tint = if (isPaid) MaterialTheme.colorScheme.onSecondaryContainer
                                                else MaterialTheme.colorScheme.onErrorContainer,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = "فاتورة مبيعات #${invoice.invoiceNumber}",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Text(
                                                text = "المتجر: ${invoice.customerName}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${String.format(Locale.ENGLISH, "%.2f", invoice.totalAmount)} د.أ",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isPaid) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                                            )
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(24.dp),
                                            color = if (isPaid) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer
                                        ) {
                                            Text(
                                                text = if (isPaid) "مدفوع" else "ذمم",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isPaid) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "تاريخ الفاتورة: $dateFormatted",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    )
                                    if (company?.repName != null) {
                                        Text(
                                            text = "المندوب: ${company.repName}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // FAB for New Sale
        FloatingActionButton(
            onClick = onOpenNewSaleSheet,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 90.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "عملية بيع جديدة")
        }
    }

    // New Sale Bottom Sheet Modal
    if (isNewSaleSheetOpen) {
        NewSaleBottomSheet(
            products = products,
            customers = customers,
            cartItems = cartItems,
            company = company,
            onDismiss = onCloseNewSaleSheet,
            onAddToCart = onAddToCart,
            onAddToCartWithQty = onAddToCartWithQty,
            onUpdateCartQty = onUpdateCartQty,
            onConfirmSale = onConfirmSale,
            onOpenAddProductWithBarcode = onOpenAddProductWithBarcode
        )
    }

    // Invoice Detail Dialog
    selectedInvoiceGroup?.let { invoice ->
        val dateFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(Date(invoice.date))
        val isPaid = invoice.status == "paid"

        AlertDialog(
            onDismissRequest = { selectedInvoiceGroup = null },
            title = {
                Column {
                    Text(
                        text = "فاتورة مبيعات #${invoice.invoiceNumber}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = company?.companyName ?: "شركة Berbox التجاري",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Company & Sales Rep Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "اسم الشركة: ${company?.companyName ?: "-"}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "رقم الشركة: ${company?.repPhone ?: "-"}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "اسم المندوب: ${company?.repName ?: "-"}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                                Text(
                                    text = "تاريخ الفاتورة: $dateFormatted",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }

                    // Customer / Store Info & Payment Status
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "المتجر / اسم الزبون:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
                                )
                                Text(
                                    text = invoice.customerName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = if (isPaid) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = if (isPaid) "مدفوعة" else "ذمم (على الحساب)",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (isPaid) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

                    // Products Header
                    Text(
                        text = "المنتجات المباعة:",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    // Products Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(24.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("المنتج", modifier = Modifier.weight(1.8f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        Text("الكمية", modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        Text("سعر المنتج", modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        Text("سعر الإجمالي", modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    // Product Items List
                    invoice.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.productName,
                                modifier = Modifier.weight(1.8f),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = "${item.quantity}",
                                modifier = Modifier.weight(0.8f),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${String.format(Locale.ENGLISH, "%.2f", item.salePrice)}",
                                modifier = Modifier.weight(1.2f),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "${String.format(Locale.ENGLISH, "%.2f", item.quantity * item.salePrice)} د.أ",
                                modifier = Modifier.weight(1.2f),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                            IconButton(
                                onClick = { returnSaleItem = item },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "إرجاع",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // Grand Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سعر الإجمالي النهائي:",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${String.format(Locale.ENGLISH, "%.2f", invoice.totalAmount)} د.أ",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    // Recipient Signature Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "توقيع المستلم (استلام البضاعة):",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(0.85f),
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "التوقيع والختم: ✍️ _______________________",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.outline,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Print PDF Button
                    Button(
                        onClick = {
                            val pdfFile = PdfInvoiceGenerator.generateInvoicePdf(context, invoice, company)
                            PdfInvoiceGenerator.printInvoicePdf(context, pdfFile, "Invoice_${invoice.invoiceNumber}")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("طباعة PDF", fontSize = 12.sp)
                    }

                    // Share PDF Button
                    OutlinedButton(
                        onClick = {
                            val pdfFile = PdfInvoiceGenerator.generateInvoicePdf(context, invoice, company)
                            PdfInvoiceGenerator.shareInvoicePdf(context, pdfFile)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مشاركة PDF", fontSize = 12.sp)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDeleteInvoice(invoice.transactionId)
                        selectedInvoiceGroup = null
                    }
                ) {
                    Text("حذف الفاتورة", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    // Sales Return Dialog
    returnSaleItem?.let { saleItem ->
        var returnQtyStr by remember { mutableStateOf("1") }
        var returnReason by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { returnSaleItem = null },
            title = { Text("إرجاع مبيعات - ${saleItem.productName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "الكمية المباعة أصلًا: ${saleItem.quantity}")

                    OutlinedTextField(
                        value = returnQtyStr,
                        onValueChange = { returnQtyStr = it },
                        label = { Text("الكمية المرتجعة") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = returnReason,
                        onValueChange = { returnReason = it },
                        label = { Text("سبب الإرجاع (اختياري)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = returnQtyStr.toIntOrNull() ?: 1
                        val finalQty = qty.coerceAtMost(saleItem.quantity)
                        onProcessReturn(saleItem, finalQty, returnReason)
                        returnSaleItem = null
                        selectedInvoiceGroup = null
                    }
                ) {
                    Text("تأكيد الإرجاع")
                }
            },
            dismissButton = {
                TextButton(onClick = { returnSaleItem = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)


@Composable
fun NewSaleBottomSheet(
    products: List<ProductEntity>,
    customers: List<CustomerEntity>,
    cartItems: List<CartItem>,
    company: CompanyEntity? = null,
    onDismiss: () -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onAddToCartWithQty: ((ProductEntity, Int, Double) -> Unit)? = null,
    onUpdateCartQty: (Int, Int) -> Unit,
    onConfirmSale: (customerName: String, status: String) -> Unit,
    onOpenAddProductWithBarcode: ((String) -> Unit)? = null
) {
    var customerName by remember { mutableStateOf("") }
    var paymentStatus by remember { mutableStateOf("paid") }
    var productSearchQuery by remember { mutableStateOf("") }
    
    val totalAmount = cartItems.sumOf { it.salePrice * it.quantity }
    
    val filteredProducts = remember(products, productSearchQuery) {
        if (productSearchQuery.isBlank()) products
        else products.filter {
            it.name.contains(productSearchQuery, ignoreCase = true) || 
            it.barcode.contains(productSearchQuery, ignoreCase = true)
        }
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = { Text("فاتورة مبيعات جديدة", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                    },
                    actions = {
                        Button(
                            onClick = { onConfirmSale(customerName, paymentStatus) },
                            enabled = cartItems.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("حفظ (${String.format(java.util.Locale.ENGLISH, "%.2f", totalAmount)})", fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Customer & Payment (Compact)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        com.example.ui.components.CustomerAutoCompleteTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            customers = customers,
                            label = "اسم الزبون (اختياري)"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { paymentStatus = "paid" },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (paymentStatus == "paid") MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (paymentStatus == "paid") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                            ) {
                                Text("نقدي", color = if (paymentStatus == "paid") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface)
                            }
                            OutlinedButton(
                                onClick = { paymentStatus = "debt" },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (paymentStatus == "debt") MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (paymentStatus == "debt") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                            ) {
                                Text("ذمم", color = if (paymentStatus == "debt") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
                
                // Cart Items (Horizontal Scroll or Compact List)
                if (cartItems.isNotEmpty()) {
                    Text(
                        "السلة (${cartItems.size})", 
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cartItems) { item ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${item.quantity} × ${item.salePrice}", fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { onUpdateCartQty(item.product.id, -1) }, modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.surface, CircleShape)) {
                                            Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp))
                                        }
                                        Text("${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                                        IconButton(onClick = { onUpdateCartQty(item.product.id, 1) }, modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.primary, CircleShape)) {
                                            Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                HorizontalDivider()
                
                // Products List for quick add
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    OutlinedTextField(
                        value = productSearchQuery,
                        onValueChange = { productSearchQuery = it },
                        placeholder = { Text("ابحث عن منتج لإضافته...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredProducts) { product ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onAddToCart(product) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text("المخزون: ${product.quantity}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${String.format(java.util.Locale.ENGLISH, "%.2f", product.price)} د.أ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
