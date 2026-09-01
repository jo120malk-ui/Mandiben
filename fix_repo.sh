#!/bin/bash
sed -i 's/import kotlinx.coroutines.flow.firstpackage com.example.data/package com.example.data\nimport kotlinx.coroutines.flow.first/g' app/src/main/java/com/example/data/BerboxRepository.kt

# Check if I need to fix Dispatchers in ViewModel
if ! grep -q "import kotlinx.coroutines.Dispatchers" app/src/main/java/com/example/ui/BerboxViewModel.kt; then
    sed -i '/import kotlinx.coroutines.flow.MutableStateFlow/a \
import kotlinx.coroutines.Dispatchers\
' app/src/main/java/com/example/ui/BerboxViewModel.kt
fi

# Check AccountSettingsScreen missing args on MainScreen ? Wait, earlier error said:
# e: file:///app/src/main/java/com/example/ui/screens/AccountSettingsScreen.kt:160:48 Unresolved reference 'onSyncNow'.
# This means I might have missed declaring them in AccountSettingsScreen's signature.
