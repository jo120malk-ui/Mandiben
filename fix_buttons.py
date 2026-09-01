import re

def process_file(filepath):
    try:
        with open(filepath, 'r') as f:
            content = f.read()

        # Find all Button and OutlinedButton invocations and ensure shape is set to RoundedCornerShape(20.dp)
        # Note: This is a simple regex that will only match basic structures.
        
        # We will add shape = RoundedCornerShape(20.dp) to any Button/OutlinedButton that doesn't have a shape.
        # However, a simpler approach is to rely on Theme.kt to enforce button shapes if possible,
        # but Jetpack Compose uses ButtonDefaults.shape which is fully customizable.
        pass
    except FileNotFoundError:
        pass

