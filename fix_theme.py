with open('app/src/main/java/com/example/ui/theme/Theme.kt', 'r') as f:
    content = f.read()

content = content.replace('FintechError', 'BerboxError')

with open('app/src/main/java/com/example/ui/theme/Theme.kt', 'w') as f:
    f.write(content)
