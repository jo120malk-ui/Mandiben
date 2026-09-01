import re

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

new_nav_item = """
@Composable
fun CustomNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    val indicatorColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent
    )
    val contentColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
    )

    androidx.compose.foundation.layout.Column(
        modifier = androidx.compose.ui.Modifier
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null, // Disable default ripple for custom interaction
                onClick = onClick
            )
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .width(56.dp)
                .height(32.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .background(indicatorColor),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
"""

pattern = re.compile(r'@Composable\nfun CustomNavItem\(.*?\}\n\}', re.DOTALL)
# actually the current definition is just `fun CustomNavItem`
pattern2 = re.compile(r'fun CustomNavItem\(.*?\}\n    \}\n\}', re.DOTALL)
content = re.sub(pattern2, new_nav_item, content)

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.write(content)
