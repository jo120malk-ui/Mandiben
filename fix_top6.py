import re

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'r') as f:
    content = f.read()

# Replace any word character followed directly by 'import '
content = re.sub(r'([A-Za-z0-9])import androidx', r'\1\nimport androidx', content)
content = re.sub(r'([A-Za-z0-9])import android', r'\1\nimport android', content)
content = re.sub(r'([A-Za-z0-9])import java', r'\1\nimport java', content)
content = re.sub(r'([A-Za-z0-9])import com', r'\1\nimport com', content)

# And fix package
content = re.sub(r'screensimport', r'screens\nimport', content)

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'w') as f:
    f.write(content)
