import re

with open('app/src/main/java/com/example/ui/screens/DashboardScreen.kt', 'r') as f:
    content = f.read()

# Replace the Card containerColor to be transparent so we can use a custom Box with gradient
# Find the Card block for the header.
pattern = r'Card\(\s*modifier = Modifier\.fillMaxWidth\(\),\s*shape = RoundedCornerShape\(24\.dp\),\s*colors = CardDefaults\.cardColors\([^)]+\)\s*\)\s*\{'

replacement = """Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().background(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )) {"""

new_content = re.sub(pattern, replacement, content)

# But wait, now I need to close that Box!
# Let's do it manually using bash or a smarter python script.

