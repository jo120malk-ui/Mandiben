with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

target_more_items = """    val moreItems = listOf(
        MoreGridItem(MoreOption.CUSTOMERS, "العملاء", Icons.Default.People),
        MoreGridItem(MoreOption.RECEIPTS, "سندات القبض", Icons.Default.Payments),
        MoreGridItem(MoreOption.DISBURSEMENTS, "سندات الصرف", Icons.Default.MoneyOff),
        MoreGridItem(MoreOption.ACCOUNT_STATEMENT, "كشف الحساب", Icons.Default.ReceiptLong),
        MoreGridItem(MoreOption.SUBSCRIPTIONS, "باقات الاشتراك", Icons.Default.CardMembership),
        MoreGridItem(MoreOption.ACCOUNT_SETTINGS, "إعدادات الحساب", Icons.Default.Settings),
        MoreGridItem(MoreOption.COMMISSION, "حسابة العمولة", Icons.Default.Calculate)
    )"""

replacement_more_items = """    val moreItems = listOf(
        MoreGridItem(MoreOption.SALES, "المبيعات", Icons.Default.PointOfSale),
        MoreGridItem(MoreOption.REPORTS, "التقارير", Icons.Default.Assessment),
        MoreGridItem(MoreOption.CUSTOMERS, "العملاء", Icons.Default.People),
        MoreGridItem(MoreOption.RECEIPTS, "سندات القبض", Icons.Default.Payments),
        MoreGridItem(MoreOption.DISBURSEMENTS, "سندات الصرف", Icons.Default.MoneyOff),
        MoreGridItem(MoreOption.ACCOUNT_STATEMENT, "كشف الحساب", Icons.Default.ReceiptLong),
        MoreGridItem(MoreOption.SUBSCRIPTIONS, "باقات الاشتراك", Icons.Default.CardMembership),
        MoreGridItem(MoreOption.ACCOUNT_SETTINGS, "إعدادات الحساب", Icons.Default.Settings),
        MoreGridItem(MoreOption.COMMISSION, "حسابة العمولة", Icons.Default.Calculate)
    )"""
content = content.replace(target_more_items, replacement_more_items)

target_when = """                    activeMoreOption == MoreOption.ACCOUNT_SETTINGS -> {
                        AccountSettingsScreen(
                            onBack = { viewModel.clearMoreOption() },
                            onLogout = { viewModel.logout() }
                        )
                    }
                    activeMoreOption == MoreOption.COMMISSION -> {
                        CommissionScreen(
                            onBack = { viewModel.clearMoreOption() }
                        )
                    }"""

replacement_when = """                    activeMoreOption == MoreOption.ACCOUNT_SETTINGS -> {
                        AccountSettingsScreen(
                            onBack = { viewModel.clearMoreOption() },
                            onLogout = { viewModel.logout() }
                        )
                    }
                    activeMoreOption == MoreOption.COMMISSION -> {
                        CommissionScreen(
                            onBack = { viewModel.clearMoreOption() }
                        )
                    }
                    activeMoreOption == MoreOption.SALES -> {
                        SalesScreen(
                            sales = sales,
                            onBack = { viewModel.clearMoreOption() },
                            onAddSale = { /* open add sale */ }
                        )
                    }
                    activeMoreOption == MoreOption.REPORTS -> {
                        ReportsScreen(
                            sales = sales,
                            onBack = { viewModel.clearMoreOption() }
                        )
                    }"""
content = content.replace(target_when, replacement_when)

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.write(content)
