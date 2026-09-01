import re

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

# Replace the NavigationBar block
pattern = r'bottomBar = \{\s*NavigationBar\([\s\S]*?\}\s*\}'

replacement = """bottomBar = {
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
                    shadowElevation = 8.dp,
                    tonalElevation = 8.dp
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
                        )
                    }
                }
            }
        }"""

new_content = re.sub(pattern, replacement, content)
with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.write(new_content)
