with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

target = """                        DashboardScreen(
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
                            onNavigateToCustomers = {
                                viewModel.selectMoreOption(MoreOption.CUSTOMERS)
                            }
                        )"""

replacement = """                        DashboardScreen(
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
                        )"""

content = content.replace(target, replacement)

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.write(content)
