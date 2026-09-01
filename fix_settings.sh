#!/bin/bash
sed -i 's/onSaveProfile: (String, String, String, String) -> Unit/onSaveProfile: (String, String, String, String) -> Unit,\n    onSyncNow: () -> Unit = {},\n    onRestoreBackup: () -> Unit = {}/g' app/src/main/java/com/example/ui/screens/AccountSettingsScreen.kt

sed -i '/import androidx.compose.material.icons.filled.DarkMode/a \
import androidx.compose.material.icons.filled.CloudUpload\
import androidx.compose.material.icons.filled.CloudDownload\
' app/src/main/java/com/example/ui/screens/AccountSettingsScreen.kt

sed -i '/\/\/ System\/App Settings section/i \
        item {\
            val isPro = company?.subscriptionPlan in listOf("monthly", "yearly", "three_years")\
            Text(\
                text = "المزامنة السحابية (Pro)",\
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),\
                color = MaterialTheme.colorScheme.onSurfaceVariant,\
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)\
            )\
            Surface(\
                modifier = Modifier.fillMaxWidth(),\
                shape = RoundedCornerShape(24.dp),\
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)\
            ) {\
                Column {\
                    SettingsRowItem(\
                        icon = Icons.Default.CloudUpload,\
                        title = "النسخ الاحتياطي الآن ☁️",\
                        onClick = { if (isPro) onSyncNow() },\
                        trailingContent = {\
                            if (!isPro) {\
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))\
                            } else {\
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)\
                            }\
                        }\
                    )\
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = 20.dp))\
                    SettingsRowItem(\
                        icon = Icons.Default.CloudDownload,\
                        title = "استرجاع البيانات 🔄",\
                        onClick = { if (isPro) onRestoreBackup() },\
                        trailingContent = {\
                            if (!isPro) {\
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))\
                            } else {\
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)\
                            }\
                        }\
                    )\
                }\
            }\
        }\
        item { Spacer(modifier = Modifier.height(24.dp)) }\
' app/src/main/java/com/example/ui/screens/AccountSettingsScreen.kt

sed -i 's/AccountSettingsScreen(/AccountSettingsScreen(\n                            onSyncNow = { viewModel.syncNow() },\n                            onRestoreBackup = { viewModel.restoreBackup() },/g' app/src/main/java/com/example/ui/MainScreen.kt
