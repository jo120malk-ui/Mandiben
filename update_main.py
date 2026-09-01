import re

with open('app/src/main/java/com/example/ui/MainScreen.kt', 'r') as f:
    content = f.read()

target = """                    MoreOption.SUBSCRIPTIONS -> {
                        SubscriptionsScreen(
                            company = company,
                            onSubscribePlan = { plan -> viewModel.updateSubscription(plan) },
                            onRedeemCode = { code -> viewModel.redeemActivationCode(code) }
                        )
                    }"""

replacement = """                    MoreOption.SUBSCRIPTIONS -> {
                        SubscriptionsScreen(
                            company = company,
                            onSubscribePlan = { plan -> viewModel.updateSubscription(plan) },
                            onRedeemCode = { code -> viewModel.redeemActivationCode(code) },
                            onBack = { viewModel.selectMoreOption(null) }
                        )
                    }"""

if target in content:
    content = content.replace(target, replacement)
    with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
        f.write(content)
    print("Replaced successfully")
else:
    print("Target not found. Doing regex replace...")
    # fallback
    content = re.sub(r'onRedeemCode = \{ code -> viewModel.redeemActivationCode\(code\) \}\s*\)', 'onRedeemCode = { code -> viewModel.redeemActivationCode(code) },\n                            onBack = { viewModel.selectMoreOption(null) }\n                        )', content)
    with open('app/src/main/java/com/example/ui/MainScreen.kt', 'w') as f:
        f.write(content)
    print("Regex replace done")
