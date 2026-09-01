import os

with open('app/src/main/java/com/example/ui/theme/Color.kt', 'w') as f:
    f.write("""package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val BerboxBackground = Color(0xFFF7F7F7)
val BerboxSurface = Color(0xFFFFFFFF)
val BerboxSurfaceVariant = Color(0xFFF0F0F0)

val BerboxDark = Color(0xFF151515)
val BerboxOnDark = Color(0xFFFFFFFF)

val BerboxTextPrimary = Color(0xFF111111)
val BerboxTextSecondary = Color(0xFF8E8E93)

val BerboxError = Color(0xFFFF3B30)
val BerboxErrorContainer = Color(0xFFFFE5E5)

// Material 3 mapping
val PrimaryLight = BerboxDark
val OnPrimaryLight = BerboxOnDark
val PrimaryContainerLight = BerboxSurface
val OnPrimaryContainerLight = BerboxTextPrimary
val SecondaryLight = BerboxSurfaceVariant
val OnSecondaryLight = BerboxTextPrimary
val BackgroundLight = BerboxBackground
val OnBackgroundLight = BerboxTextPrimary
val SurfaceLight = BerboxSurface
val OnSurfaceLight = BerboxTextPrimary
val SurfaceVariantLight = BerboxSurfaceVariant
val OnSurfaceVariantLight = BerboxTextSecondary
val OutlineLight = Color(0xFFE5E7EB)

val BackgroundDark = Color(0xFF000000)
val SurfaceDark = Color(0xFF111111)
val SurfaceVariantDark = Color(0xFF1C1C1E)
val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFFA1A1AA)
val OutlineDark = Color(0xFF27272A)
""")

with open('app/src/main/java/com/example/ui/theme/Type.kt', 'w') as f:
    f.write("""package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 48.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
    )
)
""")

