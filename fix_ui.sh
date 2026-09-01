#!/bin/bash
sed -i 's/fun SubscriptionsScreen(/fun SubscriptionsScreen(\n    onRedeemCode: (String) -> Unit = {},\n/g' app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt

sed -i '/val context = androidx.compose.ui.platform.LocalContext.current/i \
            var activationCode by remember { mutableStateOf("") }\
            Card(\
                modifier = Modifier.fillMaxWidth(),\
                shape = RoundedCornerShape(18.dp),\
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)\
            ) {\
                Column(modifier = Modifier.padding(18.dp)) {\
                    Text("لديك كود تفعيل؟", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)\
                    Spacer(modifier = Modifier.height(8.dp))\
                    androidx.compose.material3.OutlinedTextField(\
                        value = activationCode,\
                        onValueChange = { activationCode = it },\
                        placeholder = { Text("أدخل الكود هنا") },\
                        modifier = Modifier.fillMaxWidth(),\
                        singleLine = true,\
                        shape = RoundedCornerShape(12.dp)\
                    )\
                    Spacer(modifier = Modifier.height(12.dp))\
                    Button(\
                        onClick = { if (activationCode.isNotBlank()) onRedeemCode(activationCode) },\
                        modifier = Modifier.fillMaxWidth(),\
                        shape = RoundedCornerShape(12.dp)\
                    ) {\
                        Text("تفعيل الكود")\
                    }\
                }\
            }\
            Spacer(modifier = Modifier.height(16.dp))\
' app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt
