import re
import os

files = [
    'app/src/main/java/com/example/ui/screens/ReportsScreen.kt',
    'app/src/main/java/com/example/ui/screens/AccountStatementScreen.kt',
    'app/src/main/java/com/example/ui/screens/ReceiptsScreen.kt',
    'app/src/main/java/com/example/ui/screens/DisbursementsScreen.kt'
]

new_card = """Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )"""
                        
new_card2 = """Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )"""

for file_path in files:
    if os.path.exists(file_path):
        with open(file_path, 'r') as f:
            content = f.read()
            
        content = re.sub(
            r'Card\(\s*modifier = Modifier\.fillMaxWidth\(\),\s*shape = RoundedCornerShape\(24\.dp\),\s*colors = CardDefaults\.cardColors\(\s*containerColor = MaterialTheme\.colorScheme\.surface\s*\)\s*\)',
            new_card,
            content,
            flags=re.MULTILINE
        )
        content = re.sub(
            r'Card\(\s*modifier = Modifier\.fillMaxWidth\(\),\s*shape = RoundedCornerShape\(16\.dp\),\s*colors = CardDefaults\.cardColors\(\s*containerColor = MaterialTheme\.colorScheme\.surface\s*\)\s*\)',
            new_card2,
            content,
            flags=re.MULTILINE
        )
        
        with open(file_path, 'w') as f:
            f.write(content)
