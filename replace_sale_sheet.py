import re

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'r') as f:
    content = f.read()

new_code = """
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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
    var showProductPicker by remember { mutableStateOf(false) }

    val totalAmount = cartItems.sumOf { it.salePrice * it.quantity }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = { Text("إنشاء فاتورة جديدة", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق")
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp).imePadding()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("الإجمالي الكلي:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text(
                                "${String.format(java.util.Locale.ENGLISH, "%.2f", totalAmount)} د.أ",
                                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onConfirmSale(customerName, paymentStatus) },
                            enabled = cartItems.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("تأكيد وحفظ الفاتورة", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Section 1: Customer Details
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("بيانات العميل والدفع", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            com.example.ui.components.CustomerAutoCompleteTextField(
                                value = customerName,
                                onValueChange = { customerName = it },
                                customers = customers,
                                label = "اسم العميل / المتجر (اختياري)"
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Payment Status segmented button / chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // النقدي
                                Card(
                                    modifier = Modifier.weight(1f).clickable { paymentStatus = "paid" },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (paymentStatus == "paid") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                        contentColor = if (paymentStatus == "paid") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    ),
                                    border = if (paymentStatus != "paid") androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        Text("مدفوعة (نقدي)", fontWeight = FontWeight.Bold)
                                    }
                                }
                                // الذمم
                                Card(
                                    modifier = Modifier.weight(1f).clickable { paymentStatus = "debt" },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (paymentStatus == "debt") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                        contentColor = if (paymentStatus == "debt") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    ),
                                    border = if (paymentStatus != "debt") androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null
                                ) {
                                    Box(modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        Text("ذمم (على الحساب)", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 2: Cart Items
                item {
                    Text("المنتجات (${cartItems.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }

                if (cartItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(150.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("الفاتورة فارغة، أضف منتجات.", color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                } else {
                    items(cartItems) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(
                                        "السعر: ${String.format(java.util.Locale.ENGLISH, "%.2f", item.salePrice)} د.أ",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onUpdateCartQty(item.product.id, -1) },
                                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape).size(36.dp)
                                    ) { Icon(Icons.Default.Remove, null) }
                                    
                                    Text("${item.quantity}", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    
                                    IconButton(
                                        onClick = { onUpdateCartQty(item.product.id, 1) },
                                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape).size(36.dp)
                                    ) { Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onPrimaryContainer) }
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { showProductPicker = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("إضافة منتج للفاتورة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    if (showProductPicker) {
        ProductPickerDialog(
            products = products,
            onDismiss = { showProductPicker = false },
            onProductSelected = { prod, qty, price ->
                if (onAddToCartWithQty != null) {
                    onAddToCartWithQty(prod, qty, price)
                } else {
                    repeat(qty) { onAddToCart(prod) }
                }
                showProductPicker = false
            },
            onOpenAddProductWithBarcode = { barcode ->
                showProductPicker = false
                onOpenAddProductWithBarcode?.invoke(barcode)
            }
        )
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ProductPickerDialog(
    products: List<ProductEntity>,
    onDismiss: () -> Unit,
    onProductSelected: (ProductEntity, Int, Double) -> Unit,
    onOpenAddProductWithBarcode: ((String) -> Unit)?
) {
    var searchQuery by remember { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(false) }
    var selectedProductForDetail by remember { mutableStateOf<ProductEntity?>(null) }
    var notFoundCode by remember { mutableStateOf<String?>(null) }

    val filteredProducts = remember(products, searchQuery) {
        if (searchQuery.isBlank()) products
        else products.filter {
            it.name.contains(searchQuery, ignoreCase = true) || 
            it.barcode.contains(searchQuery, ignoreCase = true)
        }
    }

    if (isScanning) {
        com.example.ui.components.BarcodeScannerDialog(
            title = "مسح QR للمنتج",
            onBarcodeScanned = { code ->
                val trimmed = code.trim()
                val match = products.find { 
                    (it.barcode.isNotBlank() && it.barcode.equals(trimmed, ignoreCase = true)) || 
                    it.id.toString() == trimmed
                }
                if (match != null) {
                    selectedProductForDetail = match
                } else {
                    notFoundCode = trimmed
                }
                isScanning = false
            },
            onDismiss = { isScanning = false }
        )
    }

    notFoundCode?.let { code ->
        ProductNotFoundDialog(
            scannedCode = code,
            onAddNewProduct = { barcodeToAdd ->
                notFoundCode = null
                onOpenAddProductWithBarcode?.invoke(barcodeToAdd)
            },
            onRescan = {
                notFoundCode = null
                isScanning = true
            },
            onDismiss = { notFoundCode = null }
        )
    }

    selectedProductForDetail?.let { product ->
        ScannedProductDetailDialog(
            product = product,
            initialQuantity = 1,
            onConfirmAddToCart = { prod, qty, unitPrice ->
                onProductSelected(prod, qty, unitPrice)
                selectedProductForDetail = null
            },
            onDismiss = { selectedProductForDetail = null }
        )
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("اختر منتجاً", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("ابحث بالاسم أو الباركود...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp)
                    )
                    IconButton(
                        onClick = { isScanning = true },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape).size(52.dp)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredProducts) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { selectedProductForDetail = product },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("المخزون: ${product.quantity}", style = MaterialTheme.typography.bodySmall)
                                }
                                Text("${String.format(java.util.Locale.ENGLISH, "%.2f", product.price)} د.أ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
"""

pattern = re.compile(r'fun NewSaleBottomSheet\(.*', re.DOTALL)
# It's better to find the `@Composable\nfun NewSaleBottomSheet` and replace until EOF since it's the end of file.
pattern = re.compile(r'@Composable\s*fun NewSaleBottomSheet.*', re.DOTALL)

content = re.sub(pattern, new_code, content)

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'w') as f:
    f.write(content)
