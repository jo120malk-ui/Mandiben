#!/bin/bash
sed -i '1s/^/import androidx.compose.material3.Button\nimport androidx.compose.ui.text.style.TextAlign\nimport com.example.data.local.isProActive\nimport com.example.ui.components.ProGate\n/' app/src/main/java/com/example/ui/MainScreen.kt
sed -i 's/Icons.Default.Call/androidx.compose.material.icons.Icons.Default.Call/g' app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt
