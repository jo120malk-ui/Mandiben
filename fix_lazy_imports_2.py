with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'r') as f:
    content = f.read()

if "import androidx.compose.material3.HorizontalDivider" not in content:
    content = content.replace("import androidx.compose.material3.Divider\n", "import androidx.compose.material3.HorizontalDivider\nimport androidx.compose.material3.Divider\n")

# if the first one failed we just ensure it's there
if "import androidx.compose.material3.HorizontalDivider" not in content:
    content = "import androidx.compose.material3.HorizontalDivider\n" + content

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'w') as f:
    f.write(content)
