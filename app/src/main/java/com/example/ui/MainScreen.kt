package com.example.ui
import androidx.compose.material3.Button
import androidx.compose.ui.text.style.TextAlign
import com.example.data.local.isProActive
import com.example.ui.components.ProGate

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AccountSettingsScreen
import com.example.ui.screens.AccountStatementScreen
import com.example.ui.screens.CommissionScreen
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DisbursementsScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OnboardingFormScreen
import com.example.ui.screens.ProductsScreen
import com.example.ui.screens.ReceiptsScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SalesScreen
import com.example.ui.screens.SplashOnboardingScreen
import com.example.ui.screens.InitialSplashScreen
import com.example.ui.screens.SubscriptionsScreen

data class MoreGridItem(
    val option: MoreOption,
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: BerboxViewModel) {
    val onboardingStep by viewModel.onboardingStep.collectAsState()
    val company by viewModel.company.collectAsState()
    val products by viewModel.products.collectAsState()
    val lowStockProducts by viewModel.lowStockProducts.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val sales by viewModel.sales.collectAsState()
    val receipts by viewModel.receipts.collectAsState()
    val disbursements by viewModel.disbursements.collectAsState()

    val currentTab by viewModel.currentTab.collectAsState()
    val activeMoreOption by viewModel.activeMoreOption.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val isNewSaleSheetOpen by viewModel.isNewSaleSheetOpen.collectAsState()

    val isAddProductDialogOpen by viewModel.isAddProductDialogOpen.collectAsState()
    val editingProduct by viewModel.editingProduct.collectAsState()
    val isAddCustomerDialogOpen by viewModel.isAddCustomerDialogOpen.collectAsState()
    val editingCustomer by viewModel.editingCustomer.collectAsState()
    val isAddReceiptDialogOpen by viewModel.isAddReceiptDialogOpen.collectAsState()
    val isAddDisbursementDialogOpen by viewModel.isAddDisbursementDialogOpen.collectAsState()
    val selectedStatementCustomer by viewModel.selectedStatementCustomer.collectAsState()

    val userMessage by viewModel.userMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val showWelcomeGift by viewModel.showWelcomeGift.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var isMoreMenuSheetOpen by remember { mutableStateOf(false) }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            if (onboardingStep == OnboardingStep.COMPLETED) {
                snackbarHostState.showSnackbar(msg)
                viewModel.clearUserMessage()
            }
        }
    }

    // Onboarding Flow Branching
    when (onboardingStep) {
        OnboardingStep.INITIAL_SPLASH -> {
            InitialSplashScreen(onSplashFinished = { viewModel.finishInitialSplash() })
            return
        }
        OnboardingStep.SPLASH_SWIPER -> {
            SplashOnboardingScreen(
                onSkipToLogin = { viewModel.skipSwiperToLogin() },
                onStartNow = { viewModel.skipSwiperToLogin() }
            )
            return
        }
        OnboardingStep.LOGIN -> {
            LoginScreen(
                isLoading = isLoading,
                errorMessage = userMessage,
                onLogin = { phone, pass ->
                    viewModel.performPhoneLogin(phone, pass)
                },
                onCreateAccount = {
                    viewModel.navigateToCreateAccount()
                },
                onGoogleLogin = { email, name ->
                    viewModel.performGoogleLogin(email, name)
                }
            )
            return
        }
        OnboardingStep.MANDATORY_FORM -> {
            val tempName by viewModel.tempGoogleName.collectAsState()
            OnboardingFormScreen(
                initialRepName = tempName,
                onSubmitForm = { cName, rName, rPhone, password, loc, lat, lng ->
                    viewModel.submitMandatoryOnboarding(cName, rName, rPhone, password, loc, lat, lng)
                }
            )
            return
        }
        OnboardingStep.COMPLETED -> {
            // Main Application Workspace
        }
    }

    val moreItems = listOf(
        MoreGridItem(MoreOption.REPORTS, "التقارير", Icons.Default.Assessment),
        MoreGridItem(MoreOption.CUSTOMERS, "العملاء", Icons.Default.People),
        MoreGridItem(MoreOption.RECEIPTS, "سندات القبض", Icons.Default.Payments),
        MoreGridItem(MoreOption.DISBURSEMENTS, "سندات الصرف", Icons.Default.MoneyOff),
        MoreGridItem(MoreOption.ACCOUNT_STATEMENT, "كشف الحساب", Icons.Default.ReceiptLong),
        MoreGridItem(MoreOption.COMMISSION, "حسابة العمولة", Icons.Default.Calculate)
    )

    Scaffold(
        bottomBar = {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(0.95f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp
                ) {
                    androidx.compose.foundation.layout.Row(
                        modifier = androidx.compose.ui.Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        CustomNavItem(
                            selected = currentTab == AppTab.DASHBOARD && activeMoreOption == null,
                            onClick = { viewModel.selectTab(AppTab.DASHBOARD) },
                            icon = Icons.Default.Dashboard,
                            label = "الرئيسية"
                        )
                        CustomNavItem(
                            selected = currentTab == AppTab.PRODUCTS && activeMoreOption == null,
                            onClick = { viewModel.selectTab(AppTab.PRODUCTS) },
                            icon = Icons.Default.Inventory2,
                            label = "المنتجات"
                        )
                        CustomNavItem(
                            selected = currentTab == AppTab.MORE || activeMoreOption != null,
                            onClick = { viewModel.selectTab(AppTab.MORE) },
                            icon = Icons.Default.Settings,
                            label = "الإعدادات"
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Crossfade(
                targetState = activeMoreOption ?: currentTab,
                label = "ScreenTransition"
            ) { target ->
                when (target) {
                    AppTab.DASHBOARD -> {
                        DashboardScreen(
                            company = company,
                            products = products,
                            lowStockProducts = lowStockProducts,
                            sales = sales,
                            onOpenProductEdit = { p ->
                                viewModel.selectTab(AppTab.PRODUCTS)
                                viewModel.openAddProductDialog(p)
                            },
                            onNavigateToUpgrade = {
                                viewModel.selectMoreOption(MoreOption.SUBSCRIPTIONS)
                            },
                            onNavigateToSales = {
                                viewModel.selectTab(AppTab.SALES)
                            },
                            onNavigateToProducts = {
                                viewModel.selectTab(AppTab.PRODUCTS)
                            },
                            onNavigateToCustomers = { viewModel.selectMoreOption(MoreOption.CUSTOMERS) },
                            onNavigateToReports = { viewModel.selectMoreOption(MoreOption.REPORTS) },
                            onNavigateToReceipts = { viewModel.selectMoreOption(MoreOption.RECEIPTS) },
                            onNavigateToDisbursements = { viewModel.selectMoreOption(MoreOption.DISBURSEMENTS) },
                            onNavigateToAccountStatement = { viewModel.selectMoreOption(MoreOption.ACCOUNT_STATEMENT) },
                            onNavigateToCommission = { viewModel.selectMoreOption(MoreOption.COMMISSION) }
                        )
                    }
                    AppTab.SALES, MoreOption.SALES -> {
                        SalesScreen(
                            sales = sales,
                            products = products,
                            customers = customers,
                            cartItems = cartItems,
                            company = company,
                            isNewSaleSheetOpen = isNewSaleSheetOpen,
                            onOpenNewSaleSheet = { viewModel.openNewSaleSheet() },
                            onCloseNewSaleSheet = { viewModel.closeNewSaleSheet() },
                            onAddToCart = { p -> viewModel.addProductToCart(p) },
                            onAddToCartWithQty = { p, qty, price -> viewModel.addProductToCartWithQty(p, qty, price) },
                            onUpdateCartQty = { id, delta -> viewModel.updateCartItemQuantity(id, delta) },
                            onConfirmSale = { name, status -> viewModel.confirmSale(name, status) },
                            onDeleteInvoice = { txId -> viewModel.deleteInvoice(txId) },
                            onProcessReturn = { item, qty, reason -> viewModel.processReturn(item, qty, reason) },
                            onOpenAddProductWithBarcode = { barcode ->
                                viewModel.selectTab(AppTab.PRODUCTS)
                                viewModel.openAddProductDialog(com.example.data.local.ProductEntity(name = "", barcode = barcode, quantity = 10, price = 1.0))
                            }
                        )
                    }
                    AppTab.PRODUCTS -> {
                        ProductsScreen(
                            products = products,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { query -> viewModel.setSearchQuery(query) },
                            onOpenAddDialog = { p -> viewModel.openAddProductDialog(p) },
                            onDeleteProduct = { id -> viewModel.deleteProduct(id) },
                            isDialogOpen = isAddProductDialogOpen,
                            editingProduct = editingProduct,
                            onCloseDialog = { viewModel.closeAddProductDialog() },
                            onSaveProduct = { name, desc, barcode, qty, price, cost, threshold ->
                                viewModel.saveProduct(name, desc, barcode, qty, price, cost, threshold)
                            }
                        )
                    }
                    AppTab.MORE -> {
                        AccountSettingsScreen(
                            onSyncNow = { viewModel.syncNow() },
                            onRestoreBackup = { viewModel.restoreBackup() },
                            company = company,
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = { viewModel.toggleDarkMode() },
                            onSaveProfile = { cName, rName, rPhone, loc ->
                                viewModel.updateProfile(cName, rName, rPhone, loc)
                            },
                            onLogout = {
                                viewModel.skipSwiperToLogin()
                            },
                            onNavigateToSubscriptions = { viewModel.selectMoreOption(MoreOption.SUBSCRIPTIONS) },
                        )
                    }

                    AppTab.REPORTS, MoreOption.REPORTS -> {
                        ReportsScreen(sales = sales)
                    }
                    MoreOption.CUSTOMERS -> {
                        CustomersScreen(
                            customers = customers,
                            onOpenAddDialog = { c -> viewModel.openAddCustomerDialog(c) },
                            onDeleteCustomer = { id -> viewModel.deleteCustomer(id) },
                            isDialogOpen = isAddCustomerDialogOpen,
                            editingCustomer = editingCustomer,
                            onCloseDialog = { viewModel.closeAddCustomerDialog() },
                            onSaveCustomer = { name, phone, loc -> viewModel.saveCustomer(name, phone, loc) },
                            onImportContacts = { contacts -> viewModel.importContacts(contacts) }
                        )
                    }
                    MoreOption.RECEIPTS -> {
                        ProGate(isPro = company.isProActive(), onUpgradeClick = { viewModel.selectMoreOption(MoreOption.SUBSCRIPTIONS) }) {
                            ReceiptsScreen(
                                receipts = receipts,
                                customers = customers,
                                company = company,
                                isAddDialogOpen = isAddReceiptDialogOpen,
                                onOpenAddDialog = { viewModel.openAddReceiptDialog() },
                                onCloseAddDialog = { viewModel.closeAddReceiptDialog() },
                                onSaveReceipt = { name, amount, notes -> viewModel.saveReceipt(name, amount, notes) }
                            )
                        }
                    }
                    MoreOption.DISBURSEMENTS -> {
                        DisbursementsScreen(
                            disbursements = disbursements,
                            isAddDialogOpen = isAddDisbursementDialogOpen,
                            onOpenAddDialog = { viewModel.openAddDisbursementDialog() },
                            onCloseAddDialog = { viewModel.closeAddDisbursementDialog() },
                            onSaveDisbursement = { purpose, amount, notes -> viewModel.saveDisbursement(purpose, amount, notes) }
                        )
                    }
                    MoreOption.ACCOUNT_STATEMENT -> {
                        ProGate(isPro = company.isProActive(), onUpgradeClick = { viewModel.selectMoreOption(MoreOption.SUBSCRIPTIONS) }) {
                            AccountStatementScreen(
                                customers = customers,
                                sales = sales,
                                receipts = receipts,
                                company = company,
                                selectedCustomerName = selectedStatementCustomer,
                                onSelectCustomer = { name -> viewModel.setSelectedStatementCustomer(name) }
                            )
                        }
                    }
                    MoreOption.SUBSCRIPTIONS -> {
                        SubscriptionsScreen(
                            company = company,
                            onSubscribePlan = { plan -> viewModel.updateSubscription(plan) },
                            onRedeemCode = { code -> viewModel.redeemActivationCode(code) },
                            onBack = { viewModel.clearMoreOption() }
                        )
                    }
                    MoreOption.ACCOUNT_SETTINGS -> {
                        AccountSettingsScreen(
                            onSyncNow = { viewModel.syncNow() },
                            onRestoreBackup = { viewModel.restoreBackup() },
                            company = company,
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = { viewModel.toggleDarkMode() },
                            onSaveProfile = { cName, rName, rPhone, loc ->
                                viewModel.updateProfile(cName, rName, rPhone, loc)
                            },
                            onLogout = {
                                viewModel.skipSwiperToLogin()
                            },
                            onNavigateToSubscriptions = { viewModel.selectMoreOption(MoreOption.SUBSCRIPTIONS) }
                        )
                    }
                    MoreOption.COMMISSION -> {
                        ProGate(isPro = company.isProActive(), onUpgradeClick = { viewModel.selectMoreOption(MoreOption.SUBSCRIPTIONS) }) {
                            CommissionScreen(
                                sales = sales
                            )
                        }
                    }
                    else -> {
                        DashboardScreen(
                            company = company,
                            products = products,
                            lowStockProducts = lowStockProducts,
                            sales = sales,
                            onOpenProductEdit = { p ->
                                viewModel.selectTab(AppTab.PRODUCTS)
                                viewModel.openAddProductDialog(p)
                            },
                            onNavigateToUpgrade = {
                                viewModel.selectMoreOption(MoreOption.SUBSCRIPTIONS)
                            },
                            onNavigateToSales = {
                                viewModel.selectTab(AppTab.SALES)
                            },
                            onNavigateToProducts = {
                                viewModel.selectTab(AppTab.PRODUCTS)
                            },
                            onNavigateToCustomers = { viewModel.selectMoreOption(MoreOption.CUSTOMERS) },
                            onNavigateToReports = { viewModel.selectMoreOption(MoreOption.REPORTS) },
                            onNavigateToReceipts = { viewModel.selectMoreOption(MoreOption.RECEIPTS) },
                            onNavigateToDisbursements = { viewModel.selectMoreOption(MoreOption.DISBURSEMENTS) },
                            onNavigateToAccountStatement = { viewModel.selectMoreOption(MoreOption.ACCOUNT_STATEMENT) },
                            onNavigateToCommission = { viewModel.selectMoreOption(MoreOption.COMMISSION) }
                        )
                    }
                }
            }
        }
    }

    // More Options Grid Bottom Sheet
    if (showWelcomeGift) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissWelcomeGift() },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("هدية لك 🎁", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                Text("لقد تم تفعيل اشتراك Berbox Pro لمدة 14 يوم مجاناً لأنك مستخدم جديد!", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { viewModel.dismissWelcomeGift() }, modifier = Modifier.fillMaxWidth()) {
                    Text("ابدأ الآن")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (isMoreMenuSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isMoreMenuSheetOpen = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "القائمة والخدمات الإضافية",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.height(300.dp)
                ) {
                    items(moreItems) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectMoreOption(item.option)
                                    isMoreMenuSheetOpen = false
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
fun CustomNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    val indicatorColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent
    )
    val contentColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
    )

    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null, // Disable default ripple for custom interaction
                onClick = onClick
            )
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .width(56.dp)
                .height(32.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .background(indicatorColor),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

