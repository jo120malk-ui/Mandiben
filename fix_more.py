with open('app/src/main/java/com/example/ui/BerboxViewModel.kt', 'r') as f:
    content = f.read()

target_enum = """enum class MoreOption {
    CUSTOMERS,
    RECEIPTS,
    DISBURSEMENTS,
    ACCOUNT_STATEMENT,
    SUBSCRIPTIONS,
    ACCOUNT_SETTINGS,
    COMMISSION
}"""

replacement_enum = """enum class MoreOption {
    CUSTOMERS,
    RECEIPTS,
    DISBURSEMENTS,
    ACCOUNT_STATEMENT,
    SUBSCRIPTIONS,
    ACCOUNT_SETTINGS,
    COMMISSION,
    SALES,
    REPORTS
}"""

content = content.replace(target_enum, replacement_enum)

with open('app/src/main/java/com/example/ui/BerboxViewModel.kt', 'w') as f:
    f.write(content)
