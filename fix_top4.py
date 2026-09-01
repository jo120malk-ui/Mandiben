import re

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'r') as f:
    content = f.read()

# Replace all occurrences of "import " with "\nimport "
content = content.replace("import ", "\nimport ")
# Fix package
content = content.replace("package com.example.ui.screens", "package com.example.ui.screens\n")
content = content.replace("\n\nimport", "\nimport")

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'w') as f:
    f.write(content)
