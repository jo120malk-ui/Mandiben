import re

with open('app/src/main/java/com/example/ui/screens/ReportsScreen.kt', 'r') as f:
    content = f.read()

# Replace 'import ' with '\nimport ' everywhere, but safely.
content = content.replace("import ", "\nimport ")
content = content.replace("\n\nimport ", "\nimport ")
content = content.replace("package com.example.ui.screens\nimport ", "package com.example.ui.screens\n\nimport ")
content = re.sub(r'([A-Za-z0-9])import androidx', r'\1\nimport androidx', content)
content = re.sub(r'([A-Za-z0-9])import android', r'\1\nimport android', content)
content = re.sub(r'([A-Za-z0-9])import java', r'\1\nimport java', content)
content = re.sub(r'([A-Za-z0-9])import com', r'\1\nimport com', content)
content = re.sub(r'screensimport', r'screens\nimport', content)

with open('app/src/main/java/com/example/ui/screens/ReportsScreen.kt', 'w') as f:
    f.write(content)

