#!/bin/bash
# Remove the standard NavigationBar block from MainScreen.kt and replace with a custom floating one.
# First, extract the bottomBar block lines. We know it's around line 256.

sed -i '/bottomBar = {/c \
        bottomBar = {\
            Box(\
                modifier = Modifier\
                    .fillMaxWidth()\
                    .padding(16.dp)\
                    .navigationBarsPadding(),\
                contentAlignment = Alignment.Center\
            ) {\
                Surface(\
                    modifier = Modifier.fillMaxWidth(0.9f),\
                    shape = RoundedCornerShape(24.dp),\
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),\
                    shadowElevation = 8.dp,\
                    tonalElevation = 8.dp\
                ) {\
                    Row(\
                        modifier = Modifier\
                            .fillMaxWidth()\
                            .padding(horizontal = 8.dp, vertical = 8.dp),\
                        horizontalArrangement = Arrangement.SpaceEvenly,\
                        verticalAlignment = Alignment.CenterVertically\
                    ) {\
                        CustomNavItem(\
                            selected = currentTab == AppTab.DASHBOARD && activeMoreOption == null,\
                            onClick = { viewModel.selectTab(AppTab.DASHBOARD) },\
                            icon = Icons.Default.Dashboard,\
                            label = "الرئيسية"\
                        )\
                        CustomNavItem(\
                            selected = currentTab == AppTab.SALES && activeMoreOption == null,\
                            onClick = { viewModel.selectTab(AppTab.SALES) },\
                            icon = Icons.Default.PointOfSale,\
                            label = "المبيعات"\
                        )\
                        CustomNavItem(\
                            selected = currentTab == AppTab.PRODUCTS && activeMoreOption == null,\
                            onClick = { viewModel.selectTab(AppTab.PRODUCTS) },\
                            icon = Icons.Default.Inventory2,\
                            label = "المنتجات"\
                        )\
                        CustomNavItem(\
                            selected = currentTab == AppTab.REPORTS && activeMoreOption == null,\
                            onClick = { viewModel.selectTab(AppTab.REPORTS) },\
                            icon = Icons.Default.Assessment,\
                            label = "التقارير"\
                        )\
                        CustomNavItem(\
                            selected = currentTab == AppTab.MORE || activeMoreOption != null,\
                            onClick = { viewModel.selectTab(AppTab.MORE) },\
                            icon = Icons.Default.MoreHoriz,\
                            label = "المزيد"\
                        )\
                    }\
                }\
            }\
        }' app/src/main/java/com/example/ui/MainScreen.kt

# Now delete the original NavigationBar content up to the matching brace.
# To be safe, I'll use a python script to replace the bottomBar definition accurately.
