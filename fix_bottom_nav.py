import re

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

# Let's drop the shadow on the bottom navigation for a cleaner look
content = re.sub(
    r'shadowElevation = 8\.dp,\s*tonalElevation = 8\.dp',
    'shadowElevation = 0.dp,\n                    tonalElevation = 0.dp',
    content,
    flags=re.MULTILINE
)

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.write(content)
