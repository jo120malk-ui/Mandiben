#!/bin/bash
sed -i 's/    onLogout: () -> Unit/    onLogout: () -> Unit,\n    onSyncNow: () -> Unit = {},\n    onRestoreBackup: () -> Unit = {}/g' app/src/main/java/com/example/ui/screens/AccountSettingsScreen.kt
