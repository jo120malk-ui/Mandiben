#!/bin/bash
sed -i '/fun addProduct(/a \
        autoSyncIfPro()' app/src/main/java/com/example/ui/BerboxViewModel.kt
sed -i '/fun updateProduct(/a \
        autoSyncIfPro()' app/src/main/java/com/example/ui/BerboxViewModel.kt
sed -i '/fun deleteProduct(/a \
        autoSyncIfPro()' app/src/main/java/com/example/ui/BerboxViewModel.kt
sed -i '/fun addCustomer(/a \
        autoSyncIfPro()' app/src/main/java/com/example/ui/BerboxViewModel.kt
sed -i '/fun updateCustomer(/a \
        autoSyncIfPro()' app/src/main/java/com/example/ui/BerboxViewModel.kt
sed -i '/fun deleteCustomer(/a \
        autoSyncIfPro()' app/src/main/java/com/example/ui/BerboxViewModel.kt
sed -i '/fun processCheckout(/a \
            autoSyncIfPro()' app/src/main/java/com/example/ui/BerboxViewModel.kt
sed -i '/fun deleteInvoice(/a \
        autoSyncIfPro()' app/src/main/java/com/example/ui/BerboxViewModel.kt
sed -i '/fun processReturn(/a \
        autoSyncIfPro()' app/src/main/java/com/example/ui/BerboxViewModel.kt
sed -i '/fun createReceipt(/a \
        autoSyncIfPro()' app/src/main/java/com/example/ui/BerboxViewModel.kt
sed -i '/fun deleteReceipt(/a \
        autoSyncIfPro()' app/src/main/java/com/example/ui/BerboxViewModel.kt
sed -i '/fun createDisbursement(/a \
        autoSyncIfPro()' app/src/main/java/com/example/ui/BerboxViewModel.kt
sed -i '/fun deleteDisbursement(/a \
        autoSyncIfPro()' app/src/main/java/com/example/ui/BerboxViewModel.kt
