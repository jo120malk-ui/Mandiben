#!/bin/bash
sed -i 's/kotlinx.coroutines.flow.first(products)/products.first()/g' app/src/main/java/com/example/data/BerboxRepository.kt
sed -i 's/kotlinx.coroutines.flow.first(customers)/customers.first()/g' app/src/main/java/com/example/data/BerboxRepository.kt
sed -i 's/kotlinx.coroutines.flow.first(sales)/sales.first()/g' app/src/main/java/com/example/data/BerboxRepository.kt
sed -i 's/kotlinx.coroutines.flow.first(receipts)/receipts.first()/g' app/src/main/java/com/example/data/BerboxRepository.kt
sed -i 's/kotlinx.coroutines.flow.first(disbursements)/disbursements.first()/g' app/src/main/java/com/example/data/BerboxRepository.kt
sed -i 's/kotlinx.coroutines.flow.first(salesReturns)/salesReturns.first()/g' app/src/main/java/com/example/data/BerboxRepository.kt
