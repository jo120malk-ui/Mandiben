import os

with open('app/src/main/java/com/example/ui/theme/Color.kt', 'w') as f:
    f.write("""package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Fintech Minimal UI Colors (Light Mode primarily)
val FintechBackground = Color(0xFFF6F6F7)
val FintechSurface = Color(0xFFFFFFFF)
val FintechSurfaceVariant = Color(0xFFF3F4F6)

val FintechHeroDark = Color(0xFF0E0E10)
val FintechOnHeroDark = Color(0xFFFFFFFF)

val FintechAccent = Color(0xFFD7FF3E) // Neon Green
val FintechOnAccent = Color(0xFF000000)

val FintechTextPrimary = Color(0xFF111111)
val FintechTextSecondary = Color(0xFF6B7280)

val FintechError = Color(0xFFFF3B30)

// Map to Material 3
val PrimaryLight = FintechAccent
val OnPrimaryLight = FintechOnAccent
val PrimaryContainerLight = FintechHeroDark
val OnPrimaryContainerLight = FintechOnHeroDark
val SecondaryLight = FintechHeroDark
val OnSecondaryLight = FintechOnHeroDark
val BackgroundLight = FintechBackground
val OnBackgroundLight = FintechTextPrimary
val SurfaceLight = FintechSurface
val OnSurfaceLight = FintechTextPrimary
val SurfaceVariantLight = FintechSurfaceVariant
val OnSurfaceVariantLight = FintechTextSecondary
val OutlineLight = Color(0xFFE5E7EB)

// Dark mode (optional, but keep it minimal)
val BackgroundDark = Color(0xFF000000)
val SurfaceDark = Color(0xFF111111)
val SurfaceVariantDark = Color(0xFF1C1C1E)
val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFFA1A1AA)
val OutlineDark = Color(0xFF27272A)
""")

with open('app/src/main/java/com/example/ui/theme/Theme.kt', 'r') as f:
    theme_code = f.read()

# I will just write a new Theme.kt completely to be safe and clean.
