import re

with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

# Update signature
old_sig = """fun DashboardScreen(
    company: CompanyEntity?,
    products: List<ProductEntity>,
    lowStockProducts: List<ProductEntity>,
    sales: List<SaleEntity>,
    onOpenProductEdit: (ProductEntity) -> Unit,
    onNavigateToUpgrade: () -> Unit,
    onNavigateToSales: () -> Unit = {},
    onNavigateToProducts: () -> Unit = {},
    onNavigateToCustomers: () -> Unit = {}
) {"""

new_sig = """import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CompanyEntity
import com.example.data.local.ProductEntity
import com.example.data.local.SaleEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    company: CompanyEntity?,
    products: List<ProductEntity>,
    lowStockProducts: List<ProductEntity>,
    sales: List<SaleEntity>,
    onOpenProductEdit: (ProductEntity) -> Unit,
    onNavigateToUpgrade: () -> Unit,
    onNavigateToSales: () -> Unit = {},
    onNavigateToProducts: () -> Unit = {},
    onNavigateToCustomers: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToReceipts: () -> Unit = {},
    onNavigateToDisbursements: () -> Unit = {},
    onNavigateToAccountStatement: () -> Unit = {},
    onNavigateToCommission: () -> Unit = {}
) {"""

# Replace imports and signature manually because we added imports inside new_sig
# Actually let's just do a regex replace for the fun signature

content = re.sub(r'fun DashboardScreen\([^)]+\)\s*\{', new_sig.split('@Composable\n')[1], content)

grid_items_code = """
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "القائمة الرئيسية",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    val items = listOf(
                        Triple("التقارير", Icons.Default.Assessment, onNavigateToReports),
                        Triple("العملاء", Icons.Default.People, onNavigateToCustomers),
                        Triple("سندات القبض", Icons.Default.Payments, onNavigateToReceipts),
                        Triple("سندات الصرف", Icons.Default.MoneyOff, onNavigateToDisbursements),
                        Triple("كشف الحساب", Icons.Default.ReceiptLong, onNavigateToAccountStatement),
                        Triple("حسابة العمولة", Icons.Default.Calculate, onNavigateToCommission)
                    )
                    
                    items.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { (title, icon, action) ->
                                Card(
                                    modifier = Modifier.weight(1f).aspectRatio(1f).clickable { action() },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(0.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = title,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.Black,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            // Fill empty spaces if not multiple of 3
                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            
            item {
"""

content = content.replace("            item {\n                // Recent Transactions", grid_items_code + "                // Recent Transactions")

with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'w') as f:
    f.write(content)
