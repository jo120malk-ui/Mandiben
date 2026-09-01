with open('app/src/main/java/com/example/ui/components/CustomerAutoCompleteTextField.kt', 'r') as f:
    content = f.read()

content = content.replace(".menuAnchor(),", ".menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable, enabled = true),")

with open('app/src/main/java/com/example/ui/components/CustomerAutoCompleteTextField.kt', 'w') as f:
    f.write(content)
