with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'r') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if line.startswith('fun NewSaleBottomSheet('):
        start_idx = i
        break

new_bottom_sheet = """fun NewSaleBottomSheet(
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
"""

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'w') as f:
    f.writelines(lines[:start_idx])
    f.write(new_bottom_sheet)

