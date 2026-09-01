with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
skip_next = False
for i, line in enumerate(lines):
    if skip_next:
        skip_next = False
        continue
    if line.strip() == '@Composable' and i + 1 < len(lines) and lines[i+1].strip() == '@Composable':
        new_lines.append(line)
        skip_next = True
    else:
        new_lines.append(line)

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.writelines(new_lines)
