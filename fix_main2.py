with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

if "import com.example.ui.screens.InitialSplashScreen" not in content:
    content = content.replace("import com.example.ui.screens.SplashOnboardingScreen", "import com.example.ui.screens.SplashOnboardingScreen\nimport com.example.ui.screens.InitialSplashScreen")

if "OnboardingStep.INITIAL_SPLASH -> {" not in content:
    target = """    when {
        !showWelcomeGift && !isMoreMenuSheetOpen -> {
            when (onboardingStep) {"""
    replacement = """    when {
        !showWelcomeGift && !isMoreMenuSheetOpen -> {
            when (onboardingStep) {
                OnboardingStep.INITIAL_SPLASH -> {
                    InitialSplashScreen(onSplashFinished = { viewModel.finishInitialSplash() })
                }"""
    content = content.replace(target, replacement)

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.write(content)
