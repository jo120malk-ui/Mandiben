with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'r') as f:
    content = f.read()

# Fix the broken first line
if content.startswith("package com.example.ui.screensimport"):
    content = content.replace("package com.example.ui.screensimport", "package com.example.ui.screens\nimport")
if "Colorimport" in content:
    content = content.replace("Colorimport", "Color\nimport")
if "Intentimport" in content:
    content = content.replace("Intentimport", "Intent\nimport")
if "backgroundimport" in content:
    content = content.replace("backgroundimport", "background\nimport")
if "clickableimport" in content:
    content = content.replace("clickableimport", "clickable\nimport")

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'w') as f:
    f.write(content)
