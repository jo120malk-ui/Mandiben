import re

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'r') as f:
    content = f.read()

# Replace 'import ' with '\nimport ' everywhere, but safely.
content = content.replace("import ", "\nimport ")
content = content.replace("\n\nimport ", "\nimport ")
content = content.replace("package com.example.ui.screens\nimport ", "package com.example.ui.screens\n\nimport ")

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'w') as f:
    f.write(content)
