import re

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'r') as f:
    content = f.read()

# Fix duplicate annotations
content = content.replace("@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)\n\n@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)\n@Composable\nfun NewSaleBottomSheet(", "@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)\n@Composable\nfun NewSaleBottomSheet(")

# Ensure imports
imports_to_add = [
    "import androidx.compose.material3.Scaffold",
    "import androidx.compose.material.icons.filled.Close",
    "import androidx.compose.material.icons.filled.ShoppingCart",
    "import androidx.compose.foundation.layout.imePadding",
    "import androidx.compose.ui.window.Dialog",
    "import androidx.compose.ui.window.DialogProperties"
]

# find last import
last_import_match = list(re.finditer(r'^import .+$', content, re.MULTILINE))[-1]
insert_pos = last_import_match.end()

import_block = "\n" + "\n".join(imports_to_add)
content = content[:insert_pos] + import_block + content[insert_pos:]

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'w') as f:
    f.write(content)
