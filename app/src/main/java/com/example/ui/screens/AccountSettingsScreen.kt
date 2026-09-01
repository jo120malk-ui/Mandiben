package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CompanyEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    company: CompanyEntity?,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onSaveProfile: (companyName: String, repName: String, repPhone: String, locationText: String) -> Unit,
    onLogout: () -> Unit,
    onSyncNow: () -> Unit = {},
    onRestoreBackup: () -> Unit = {},
    onNavigateToSubscriptions: () -> Unit = {}
) {
    var showProfileEditDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
        
        // Profile Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showProfileEditDialog = true },
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = company?.repName.takeIf { !it.isNullOrBlank() } ?: "Alfred Daniel",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = company?.companyName.takeIf { !it.isNullOrBlank() } ?: "Product/UI Designer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Edit Profile",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Other Settings section
        item {
            Text(
                text = "Other settings",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Default.Person,
                        title = "Profile details",
                        onClick = { showProfileEditDialog = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsRowItem(
                        icon = Icons.Default.Lock,
                        title = "Password",
                        onClick = { /* TODO */ }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsRowItem(
                        icon = Icons.Default.CardMembership,
                        title = "باقات الاشتراك",
                        onClick = { onNavigateToSubscriptions() }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsRowItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        onClick = { /* TODO */ }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsRowItem(
                        icon = Icons.Default.DarkMode,
                        title = "Dark mode",
                        onClick = { onToggleDarkMode() },
                        trailingContent = {
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { onToggleDarkMode() },
                                modifier = Modifier.scale(0.8f) // Make it slightly smaller to fit nicely
                            )
                        }
                    )
                }
            }
        }

        item {
            val isPro = company?.subscriptionPlan in listOf("monthly", "yearly", "three_years")
            Text(
                text = "المزامنة السحابية (Pro)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Default.CloudUpload,
                        title = "النسخ الاحتياطي الآن ☁️",
                        onClick = { if (isPro) onSyncNow() },
                        trailingContent = {
                            if (!isPro) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            } else {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsRowItem(
                        icon = Icons.Default.CloudDownload,
                        title = "استرجاع البيانات 🔄",
                        onClick = { if (isPro) onRestoreBackup() },
                        trailingContent = {
                            if (!isPro) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            } else {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }

        // System/App Settings section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Column {
                    SettingsRowItem(
                        icon = Icons.Default.Info,
                        title = "About application",
                        onClick = { /* TODO */ }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsRowItem(
                        icon = Icons.Default.HelpOutline,
                        title = "Help/FAQ",
                        onClick = { /* TODO */ }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = 20.dp))
                    SettingsRowItem(
                        icon = Icons.Default.DeleteForever,
                        title = "Deactivate my account",
                        onClick = onLogout,
                        isDestructive = true
                    )
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }

    if (showProfileEditDialog) {
        ProfileEditDialog(
            company = company,
            onDismiss = { showProfileEditDialog = false },
            onSave = { cName, rName, rPhone, loc ->
                onSaveProfile(cName, rName, rPhone, loc)
                showProfileEditDialog = false
            }
        )
    }
}

@Composable
fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
    trailingContent: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
) {
    val contentColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    val iconColor = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = contentColor,
            modifier = Modifier.weight(1f)
        )
        trailingContent()
    }
}

@Composable
fun ProfileEditDialog(
    company: CompanyEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var companyName by remember { mutableStateOf(company?.companyName ?: "") }
    var repName by remember { mutableStateOf(company?.repName ?: "") }
    var repPhone by remember { mutableStateOf(company?.repPhone ?: "") }
    var locationText by remember { mutableStateOf(company?.locationText ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تحديث البيانات") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("اسم الشركة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = repName,
                    onValueChange = { repName = it },
                    label = { Text("اسم المندوب") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = repPhone,
                    onValueChange = { repPhone = it },
                    label = { Text("رقم الهاتف") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = locationText,
                    onValueChange = { locationText = it },
                    label = { Text("العنوان") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(companyName, repName, repPhone, locationText) }) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
