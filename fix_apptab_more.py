with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

target_apptab_more = """                    AppTab.MORE -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(moreItems) { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable { viewModel.selectMoreOption(item.option) },
                                    shape = RoundedCornerShape(24.dp),
                                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }"""

replacement_apptab_more = """                    AppTab.MORE -> {
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
                    }"""

if target_apptab_more in content:
    content = content.replace(target_apptab_more, replacement_apptab_more)
else:
    print("Could not find target AppTab.MORE block")

# Also update the AccountSettings handling when selected from MoreOption (if they go back)
# Well, now they won't select MoreOption.ACCOUNT_SETTINGS since it's the main tab, but let's keep it or replace it.
content = content.replace("onLogout = {\n                                viewModel.skipSwiperToLogin()\n                            }", "onLogout = {\n                                viewModel.skipSwiperToLogin()\n                            },\n                            onNavigateToSubscriptions = { viewModel.selectMoreOption(MoreOption.SUBSCRIPTIONS) }")

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.write(content)
