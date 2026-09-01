#!/bin/bash
sed -i '/suspend fun seedSampleDataIfEmpty() {/i \
    suspend fun getAllDataForBackup(): com.example.data.remote.BackupPayload {\
        return com.example.data.remote.BackupPayload(\
            products = kotlinx.coroutines.flow.first(products),\
            customers = kotlinx.coroutines.flow.first(customers),\
            sales = kotlinx.coroutines.flow.first(sales),\
            receipts = kotlinx.coroutines.flow.first(receipts),\
            disbursements = kotlinx.coroutines.flow.first(disbursements),\
            salesReturns = kotlinx.coroutines.flow.first(salesReturns)\
        )\
    }\
\
    suspend fun restoreFromBackup(payload: com.example.data.remote.BackupPayload) {\
        payload.products.forEach { db.productDao().insertProduct(it) }\
        payload.customers.forEach { db.customerDao().insertCustomer(it) }\
        db.saleDao().insertSales(payload.sales)\
        payload.receipts.forEach { db.receiptDao().insertReceipt(it) }\
        payload.disbursements.forEach { db.disbursementDao().insertDisbursement(it) }\
        payload.salesReturns.forEach { db.salesReturnDao().insertSalesReturn(it) }\
    }\
' app/src/main/java/com/example/data/BerboxRepository.kt

sed -i '1s/^/import kotlinx.coroutines.flow.first\n/' app/src/main/java/com/example/data/BerboxRepository.kt
