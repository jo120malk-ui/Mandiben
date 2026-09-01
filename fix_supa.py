with open('app/src/main/java/com/example/data/remote/SupabaseClient.kt', 'r') as f:
    lines = f.readlines()
new_lines = []
skip_next = False
for i, line in enumerate(lines):
    if line.strip() == '@JsonClass(generateAdapter = true)' and i + 1 < len(lines) and lines[i+1].strip() == '@JsonClass(generateAdapter = true)':
        continue
    new_lines.append(line)
with open('app/src/main/java/com/example/data/remote/SupabaseClient.kt', 'w') as f:
    f.writelines(new_lines)
