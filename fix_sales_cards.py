import re

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'r') as f:
    content = f.read()

# I want to add `elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)` to the Cards if they don't have it.
# Actually, the default Card in Material3 doesn't have a big shadow anyway (it's 0.dp or 1.dp by default).

# Let's check the invoice card
# Card(
#     modifier = Modifier.fillMaxWidth().clickable { ... },
#     shape = RoundedCornerShape(24.dp),
#     colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
# )

new_card = """Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedInvoiceGroup = invoice },
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )"""

content = re.sub(
    r'Card\(\s*modifier = Modifier\s*\.fillMaxWidth\(\)\s*\.clickable \{ selectedInvoiceGroup = invoice \},\s*shape = RoundedCornerShape\(24\.dp\),\s*colors = CardDefaults\.cardColors\(\s*containerColor = MaterialTheme\.colorScheme\.surface\s*\)\s*\)',
    new_card,
    content,
    flags=re.MULTILINE
)

with open('app/src/main/java/com/example/ui/screens/SalesScreen.kt', 'w') as f:
    f.write(content)
