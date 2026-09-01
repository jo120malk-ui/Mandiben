import re

with open('app/src/main/java/com/example/ui/screens/CustomersScreen.kt', 'r') as f:
    content = f.read()

new_card = """Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )"""

content = re.sub(
    r'Card\(\s*modifier = Modifier\.fillMaxWidth\(\),\s*shape = RoundedCornerShape\(24\.dp\),\s*colors = CardDefaults\.cardColors\(\s*containerColor = MaterialTheme\.colorScheme\.surface\s*\)\s*\)',
    new_card,
    content,
    flags=re.MULTILINE
)

with open('app/src/main/java/com/example/ui/screens/CustomersScreen.kt', 'w') as f:
    f.write(content)
