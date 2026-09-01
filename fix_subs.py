with open('app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt', 'r') as f:
    content = f.read()

# Add import if needed
if "import androidx.compose.ui.graphics.SolidColor" not in content:
    content = content.replace("import androidx.compose.ui.graphics.Color", "import androidx.compose.ui.graphics.Color\nimport androidx.compose.ui.graphics.SolidColor")

content = content.replace("Brush.solidColor(Color.LightGray.copy(alpha = 0.3f))", "SolidColor(Color.LightGray.copy(alpha = 0.3f))")

with open('app/src/main/java/com/example/ui/screens/SubscriptionsScreen.kt', 'w') as f:
    f.write(content)
