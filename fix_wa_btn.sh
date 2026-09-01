#!/bin/bash
sed -i 's/shape = RoundedCornerShape(18.dp)/shape = RoundedCornerShape(18.dp),\n                colors = CardDefaults.cardColors(containerColor = Color(0xFF25D366)),\n                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)/g' app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt

sed -i 's/Icon(androidx.compose.material.icons.Icons.Default.Phone, contentDescription = null, tint = Color(0xFF25D366))/Icon(androidx.compose.material.icons.Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))/g' app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt

sed -i 's/Text("لتفعيل Pro تحدث مع خدمة العملاء", fontWeight = FontWeight.Bold)/Text("لتفعيل Pro تحدث مع خدمة العملاء", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)/g' app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt

sed -i 's/Text("اضغط للتواصل عبر واتساب", style = MaterialTheme.typography.bodySmall)/Text("اضغط للتواصل عبر واتساب", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE0F7E9))/g' app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt

