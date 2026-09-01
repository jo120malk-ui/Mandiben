import re

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('@Composable\n@Composable\nfun CustomNavItem', '@Composable\nfun CustomNavItem')

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.write(content)
