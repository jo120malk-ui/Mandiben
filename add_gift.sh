#!/bin/bash
sed -i '/if (isMoreMenuSheetOpen) {/i \
    if (showWelcomeGift) {\
        ModalBottomSheet(\
            onDismissRequest = { viewModel.dismissWelcomeGift() },\
            sheetState = rememberModalBottomSheetState()\
        ) {\
            Column(\
                modifier = Modifier.fillMaxWidth().padding(24.dp),\
                horizontalAlignment = Alignment.CenterHorizontally\
            ) {\
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))\
                Spacer(modifier = Modifier.height(16.dp))\
                Text("هدية لك 🎁", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))\
                Spacer(modifier = Modifier.height(8.dp))\
                Text("لقد تم تفعيل اشتراك Berbox Pro لمدة 14 يوم مجاناً لأنك مستخدم جديد!", textAlign = androidx.compose.ui.text.style.TextAlign.Center)\
                Spacer(modifier = Modifier.height(24.dp))\
                Button(onClick = { viewModel.dismissWelcomeGift() }, modifier = Modifier.fillMaxWidth()) {\
                    Text("ابدأ الآن")\
                }\
                Spacer(modifier = Modifier.height(24.dp))\
            }\
        }\
    }\
' app/src/main/java/com/example/ui/MainScreen.kt
