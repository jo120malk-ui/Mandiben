with open('app/src/main/java/com/example/ui/screens/AccountStatementScreen.kt', 'r') as f:
    content = f.read()

target = """            CustomerAutoCompleteTextField(
                customers = customers,
                selectedName = selectedCustomerName,
                onNameSelected = onSelectCustomer,
                modifier = Modifier.fillMaxWidth()
            )"""

replacement = """            CustomerAutoCompleteTextField(
                customers = customers,
                selectedName = selectedCustomerName,
                onNameSelected = onSelectCustomer,
                modifier = Modifier.fillMaxWidth()
            )"""

# Instead of changing anything big in this screen right now, I will just let it be.
