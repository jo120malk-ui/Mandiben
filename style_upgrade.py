import os
import re

def update_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()
    
    # Replace RoundedCornerShape(18.dp) or 16 or 12 with 24.dp for extreme sleekness
    content = re.sub(r'RoundedCornerShape\(\s*[0-9]+\.dp\s*\)', 'RoundedCornerShape(24.dp)', content)
    # Exclude small things like Search bars or small chips? 
    # Actually, 24.dp is great for everything except maybe 8.dp items. Let's be careful.
    
    # A better approach: 
    # Add tonal elevation to Cards
    content = re.sub(r'(CardDefaults\.cardColors\([^)]+\))', r'\1,\n                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)', content)

    # Make OutlinedTextFields fully rounded
    # In Compose, OutlinedTextField shape = RoundedCornerShape(24.dp)
    
    with open(filepath, 'w') as f:
        f.write(content)

# style_upgrade.py will be executed on a few key screens
screens = [
    'app/src/main/java/com/example/ui/screens/DashboardScreen.kt',
    'app/src/main/java/com/example/ui/screens/SalesScreen.kt',
    'app/src/main/java/com/example/ui/screens/ProductsScreen.kt',
    'app/src/main/java/com/example/ui/screens/CustomersScreen.kt',
    'app/src/main/java/com/example/ui/screens/AccountSettingsScreen.kt'
]

for s in screens:
    if os.path.exists(s):
        update_file(s)
