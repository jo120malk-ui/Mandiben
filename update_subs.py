import re

with open('app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt', 'r') as f:
    content = f.read()

import_str = """
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.filled.WorkspacePremium
"""
if "import java.text.SimpleDateFormat" not in content:
    content = content.replace('import androidx.compose.ui.unit.sp', 'import androidx.compose.ui.unit.sp' + import_str)

logic_insert = """
    var showPricing by remember { mutableStateOf(false) }
    val isPro = company?.subscriptionPlan in listOf("monthly", "yearly", "three_years")
    val shouldShowDisplay = isPro && !showPricing

    if (shouldShowDisplay) {
        val planName = when (company?.subscriptionPlan) {
            "monthly" -> "اشتراك شهري (Pro)"
            "yearly" -> "اشتراك سنوي (Pro)"
            "three_years" -> "اشتراك 3 سنوات (Pro)"
            else -> "Pro"
        }
        val expiresAt = company?.subscriptionExpiresAt ?: 0L
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val expireDateStr = if (expiresAt > 0) sdf.format(Date(expiresAt)) else "غير محدد"
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "أنت مشترك في باقة Pro",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("الباقة الحالية:", style = MaterialTheme.typography.bodyMedium)
                    Text(planName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("تاريخ الانتهاء:", style = MaterialTheme.typography.bodyMedium)
                    Text(expireDateStr, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { showPricing = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("تجديد أو تغيير الاشتراك", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    LazyColumn(
"""

content = content.replace('    LazyColumn(', logic_insert)

with open('app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt', 'w') as f:
    f.write(content)
