with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'r') as f:
    content = f.read()

# Make sure imports are present for OutlinedButton
if "import androidx.compose.material3.OutlinedButton" not in content:
    content = content.replace("import androidx.compose.material3.Button\n", "import androidx.compose.material3.Button\nimport androidx.compose.material3.OutlinedButton\n")
if "import androidx.compose.material3.HorizontalDivider" not in content:
    content = content.replace("import androidx.compose.material3.Divider\n", "import androidx.compose.material3.HorizontalDivider\nimport androidx.compose.material3.Divider\n")

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'w') as f:
    f.write(content)
