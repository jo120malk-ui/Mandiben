with open('app/src/main/java/com/example/ui/screens/ReportsScreen.kt', 'r') as f:
    lines = f.readlines()
for i, line in enumerate(lines[:120]):
    print(f"{i}: {line}", end='')
