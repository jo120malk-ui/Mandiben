import re

with open('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'r') as f:
    content = f.read()

# I want to add an "onGoogleLogin" callback to LoginScreen
if 'onGoogleLogin: () -> Unit,' not in content:
    content = content.replace('onCreateAccount: () -> Unit', 'onCreateAccount: () -> Unit,\n    onGoogleLogin: () -> Unit')

# I will add the Google button just before the TextButton for "Create Account"
google_btn = """
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onGoogleLogin,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_myplaces),
                            contentDescription = "Google",
                            tint = androidx.compose.ui.graphics.Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("المتابعة باستخدام Google", fontWeight = FontWeight.Bold)
                    }
"""
if 'المتابعة باستخدام Google' not in content:
    content = content.replace('Spacer(modifier = Modifier.height(16.dp))\n                    TextButton(', google_btn + '\n                    Spacer(modifier = Modifier.height(16.dp))\n                    TextButton(')

with open('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'w') as f:
    f.write(content)
