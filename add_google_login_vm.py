import re

with open('app/src/main/java/com/example/ui/BerboxViewModel.kt', 'r') as f:
    content = f.read()

func_code = """
    fun performGoogleLogin(email: String, name: String) {
        // If we already have a company, just log them in
        viewModelScope.launch {
            val existing = repository.getCompanyOnce()
            if (existing != null) {
                _onboardingStep.value = OnboardingStep.COMPLETED
            } else {
                // Otherwise, send them to the mandatory step-by-step form
                _tempGoogleName.value = name
                _onboardingStep.value = OnboardingStep.MANDATORY_FORM
            }
        }
    }
"""

if 'fun performGoogleLogin' not in content:
    content = content.replace('fun navigateToCreateAccount() {', func_code + '\n    fun navigateToCreateAccount() {')

with open('app/src/main/java/com/example/ui/BerboxViewModel.kt', 'w') as f:
    f.write(content)
