import re

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'r') as f:
    content = f.read()

# Just put a newline before every "import " if there isn't one.
content = re.sub(r'(?<!\n)import ', '\nimport ', content)

# Make sure package is at the top with a newline
content = content.replace("package com.example.ui.screens\nimport", "package com.example.ui.screens\n\nimport")

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'w') as f:
    f.write(content)
