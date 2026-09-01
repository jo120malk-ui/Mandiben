import os

with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'r') as f:
    lines = f.readlines()

# 1. Remove the bad import at line 1 and put it correctly
if "aspectRatio" in lines[0]:
    lines.pop(0)

# Make sure it's imported
import_aspect_ratio = "import androidx.compose.foundation.layout.aspectRatio\n"
if not any("aspectRatio" in line for line in lines):
    lines.insert(2, import_aspect_ratio) # Put it after package

content = "".join(lines)

# 2. Fix the DashboardScreen signature. We can just use string replacement
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

new_sig = """fun DashboardScreen(
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

content = content.replace(old_sig, new_sig)

with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'w') as f:
    f.write(content)
