import re
with open('app/src/main/java/com/example/ui/BerboxViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace("enum class OnboardingStep {\n    SPLASH_SWIPER,\n    LOGIN,\n    MANDATORY_FORM,\n    COMPLETED\n}", "enum class OnboardingStep {\n    INITIAL_SPLASH,\n    SPLASH_SWIPER,\n    LOGIN,\n    MANDATORY_FORM,\n    COMPLETED\n}")
content = content.replace("MutableStateFlow(OnboardingStep.SPLASH_SWIPER)", "MutableStateFlow(OnboardingStep.INITIAL_SPLASH)")
content = content.replace("fun skipSwiperToLogin() {", "fun finishInitialSplash() {\n        _onboardingStep.value = OnboardingStep.SPLASH_SWIPER\n    }\n\n    fun skipSwiperToLogin() {")

with open('app/src/main/java/com/example/ui/BerboxViewModel.kt', 'w') as f:
    f.write(content)
