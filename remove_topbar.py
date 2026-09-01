import re

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

# Regular expression to remove topBar = { ... },
pattern = re.compile(r'topBar = \{.*?\},', re.DOTALL)
content = re.sub(pattern, '', content)

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.write(content)
