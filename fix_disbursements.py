with open('app/src/main/java/com/example/ui/screens/DisbursementsScreen.kt', 'r') as f:
    content = f.read()

target = """            Spacer(modifier = Modifier.height(16.dp))
            if (disbursements.isEmpty()) {"""

replacement = """            Spacer(modifier = Modifier.height(16.dp))
            
            // Filters
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("الكل", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("اليوم", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (disbursements.isEmpty()) {"""

content = content.replace(target, replacement)

with open('app/src/main/java/com/example/ui/screens/DisbursementsScreen.kt', 'w') as f:
    f.write(content)
