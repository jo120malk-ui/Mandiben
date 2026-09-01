import re

with open('app/src/main/java/com/example/ui/screens/AccountSettingsScreen.kt', 'r') as f:
    content = f.read()

# Update signature
old_sig = """fun AccountSettingsScreen(
    company: CompanyEntity?,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onSaveProfile: (companyName: String, repName: String, repPhone: String, locationText: String) -> Unit,
    onLogout: () -> Unit,
    onSyncNow: () -> Unit = {},
    onRestoreBackup: () -> Unit = {}
) {"""

new_sig = """fun AccountSettingsScreen(
    company: CompanyEntity?,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onSaveProfile: (companyName: String, repName: String, repPhone: String, locationText: String) -> Unit,
    onLogout: () -> Unit,
    onSyncNow: () -> Unit = {},
    onRestoreBackup: () -> Unit = {},
    onNavigateToSubscriptions: () -> Unit = {}
) {"""

content = content.replace(old_sig, new_sig)

# Add "باقات الاشتراك" to settings items
target_settings = """                    SettingsRowItem(
                        icon = Icons.Default.Lock,
                        title = "Password",
                        onClick = { /* TODO */ }
                    )"""
replacement_settings = """                    SettingsRowItem(
                        icon = Icons.Default.Lock,
                        title = "Password",
                        onClick = { /* TODO */ }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsRowItem(
                        icon = Icons.Default.CardMembership,
                        title = "باقات الاشتراك",
                        onClick = { onNavigateToSubscriptions() }
                    )"""

content = content.replace(target_settings, replacement_settings)

with open('app/src/main/java/com/example/ui/screens/AccountSettingsScreen.kt', 'w') as f:
    f.write(content)
