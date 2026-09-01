import re

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

pattern = re.compile(r'Scaffold\(.*?bottomBar = \{', re.DOTALL)
content = re.sub(pattern, 'Scaffold(\n        bottomBar = {', content)

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.write(content)
