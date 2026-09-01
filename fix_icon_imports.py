import re

files = [
    'app/src/main/java/com/example/ui/MainScreen.kt',
    'app/src/main/java/com/example/ui/screens/DashboardScreen.kt'
]

for file in files:
    with open(file, 'r') as f:
        content = f.read()

    # Find what icons are used with Icons.Outlined.X
    outlined_icons = re.findall(r'Icons\.Outlined\.(\w+)', content)
    outlined_icons = list(set(outlined_icons))

    # Replace any filled imports for these icons with outlined imports
    # or just add the outlined imports if they don't exist
    for icon in outlined_icons:
        filled_import = f"import androidx.compose.material.icons.filled.{icon}"
        outlined_import = f"import androidx.compose.material.icons.outlined.{icon}"
        
        if filled_import in content:
            content = content.replace(filled_import, outlined_import)
        elif outlined_import not in content:
            # add after import androidx.compose.material.icons.Icons
            content = content.replace(
                "import androidx.compose.material.icons.Icons",
                f"import androidx.compose.material.icons.Icons\n{outlined_import}"
            )
            
    with open(file, 'w') as f:
        f.write(content)

