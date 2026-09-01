import re

# 1. Update BerboxViewModel.kt to add SALES and REPORTS to MoreOption if we want, or just leave it.
# Actually, the user's Dashboard *has* a button for Sales. "المبيعات" button is right there in the Hero Card!
# "التقارير" might be missing, but the user's example text literally had:
# "التقارير المزيد" in the grid!
# So yes, the MORE screen is exactly what they mean by the rest of the options.

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

# Replace the 5 tabs with 3 tabs
target_nav_items = """                        CustomNavItem(
                            selected = currentTab == AppTab.DASHBOARD && activeMoreOption == null,
                            onClick = { viewModel.selectTab(AppTab.DASHBOARD) },
                            icon = Icons.Default.Dashboard,
                            label = "الرئيسية"
                        )
                        CustomNavItem(
                            selected = currentTab == AppTab.SALES && activeMoreOption == null,
                            onClick = { viewModel.selectTab(AppTab.SALES) },
                            icon = Icons.Default.PointOfSale,
                            label = "المبيعات"
                        )
                        CustomNavItem(
                            selected = currentTab == AppTab.PRODUCTS && activeMoreOption == null,
                            onClick = { viewModel.selectTab(AppTab.PRODUCTS) },
                            icon = Icons.Default.Inventory2,
                            label = "المنتجات"
                        )
                        CustomNavItem(
                            selected = currentTab == AppTab.REPORTS && activeMoreOption == null,
                            onClick = { viewModel.selectTab(AppTab.REPORTS) },
                            icon = Icons.Default.Assessment,
                            label = "التقارير"
                        )
                        CustomNavItem(
                            selected = currentTab == AppTab.MORE || activeMoreOption != null,
                            onClick = { viewModel.selectTab(AppTab.MORE) },
                            icon = Icons.Default.MoreHoriz,
                            label = "المزيد"
                        )"""

replacement_nav_items = """                        CustomNavItem(
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
                        )"""

content = content.replace(target_nav_items, replacement_nav_items)

# Add "SALES" and "REPORTS" to moreItems if they are not there, but we would need MoreOption.SALES which doesn't exist.
# Let's add them to MoreOption in BerboxViewModel.kt

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.write(content)
