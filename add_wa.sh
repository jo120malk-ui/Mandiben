#!/bin/bash
sed -i '/\/\/ Upgrade \/ Subscribe Action/i \
        item {\
            val context = androidx.compose.ui.platform.LocalContext.current\
            Card(\
                modifier = Modifier.fillMaxWidth().clickable {\
                    val phone = "962776255805"\
                    val msg = "أرغب بتفعيل اشتراك Pro"\
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW,\
                        android.net.Uri.parse("https://wa.me/$phone?text=${android.net.Uri.encode(msg)}"))\
                    context.startActivity(intent)\
                },\
                shape = RoundedCornerShape(18.dp)\
            ) {\
                Row(\
                    modifier = Modifier.padding(18.dp).fillMaxWidth(),\
                    verticalAlignment = Alignment.CenterVertically\
                ) {\
                    Icon(androidx.compose.material.icons.Icons.Default.Call, contentDescription = null, tint = Color(0xFF25D366))\
                    Spacer(Modifier.width(12.dp))\
                    Column {\
                        Text("لتفعيل Pro تحدث مع خدمة العملاء", fontWeight = FontWeight.Bold)\
                        Text("اضغط للتواصل عبر واتساب", style = MaterialTheme.typography.bodySmall)\
                    }\
                }\
            }\
            Spacer(modifier = Modifier.height(16.dp))\
        }' app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt
