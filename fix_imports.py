with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

imports_to_add = """
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.aspectRatio
"""

# I will just insert them below import androidx.compose.material.icons.filled.Notifications
content = content.replace("import androidx.compose.material.icons.filled.Notifications", "import androidx.compose.material.icons.filled.Notifications" + imports_to_add)

with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'w') as f:
    f.write(content)
