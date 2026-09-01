with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("onBack = { viewModel.selectMoreOption(null) }", "onBack = { viewModel.clearMoreOption() }")

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.write(content)
