with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

target = """    // Onboarding Flow Branching
    when (onboardingStep) {"""
replacement = """    // Onboarding Flow Branching
    when (onboardingStep) {
        OnboardingStep.INITIAL_SPLASH -> {
            InitialSplashScreen(onSplashFinished = { viewModel.finishInitialSplash() })
            return
        }"""
content = content.replace(target, replacement)

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
    f.write(content)
