package com.example.data
import kotlinx.coroutines.flow.*

import com.example.data.local.AppDatabase
import com.example.data.local.CompanyEntity
import com.example.data.local.CustomerEntity
import com.example.data.local.DisbursementEntity
import com.example.data.local.ProductEntity
import com.example.data.local.ReceiptEntity
import com.example.data.local.SaleEntity
import com.example.data.local.SalesReturnEntity
import com.example.data.remote.SupabaseClient
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class BerboxRepository(private val db: AppDatabase) {
    val company: Flow<CompanyEntity?> = db.companyDao().getCompany()
    val products: Flow<List<ProductEntity>> = db.productDao().getAllProducts()
    val lowStockProducts: Flow<List<ProductEntity>> = db.productDao().getLowStockProducts()
    val customers: Flow<List<CustomerEntity>> = db.customerDao().getAllCustomers()
    val sales: Flow<List<SaleEntity>> = db.saleDao().getAllSales()
    val receipts: Flow<List<ReceiptEntity>> = db.receiptDao().getAllReceipts()
    val disbursements: Flow<List<DisbursementEntity>> = db.disbursementDao().getAllDisbursements()
    val salesReturns: Flow<List<SalesReturnEntity>> = db.salesReturnDao().getAllSalesReturns()

    suspend fun getCompanyOnce(): CompanyEntity? = db.companyDao().getCompanyOnce()

    suspend fun fetchCompanyFromSupabase(phone: String): CompanyEntity? {
        if (!SupabaseClient.isConnected()) return null
        return try {
            val response = SupabaseClient.api.getCompanyByPhone(
                apiKey = SupabaseClient.supabaseAnonKey,
                auth = "Bearer ${SupabaseClient.supabaseAnonKey}",
                phone = "eq.$phone"
            )
            if (response.isSuccessful) {
                response.body()?.firstOrNull()
            } else {
                android.util.Log.e("SupabaseError", "Failed to fetch company: ${response.code()} ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("SupabaseError", "Exception fetching company: ${e.message}")
            null
        }
    }

    suspend fun saveCompanyLocally(company: CompanyEntity) {
        db.companyDao().insertCompany(company)
    }

    suspend fun saveCompany(company: CompanyEntity) {
        db.companyDao().insertCompany(company)
        
        try {
            if (SupabaseClient.isConnected()) {
                val response = SupabaseClient.api.insertCompany(
                    apiKey = SupabaseClient.supabaseAnonKey,
                    auth = "Bearer ${SupabaseClient.supabaseAnonKey}",
                    company = company
                )
                if (!response.isSuccessful) {
                    android.util.Log.e("SupabaseError", "Failed to insert company: ${response.code()} ${response.errorBody()?.string()}")
                } else {
                    android.util.Log.i("SupabaseSuccess", "Successfully inserted company: ${response.body()}")
                }
            } else {
                android.util.Log.e("SupabaseError", "SupabaseClient is not connected")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("SupabaseError", "Exception inserting company: ${e.message}")
        }
    }

    suspend fun addProduct(product: ProductEntity) {
        db.productDao().insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) {
        db.productDao().updateProduct(product)
    }

    suspend fun deleteProduct(productId: Int): Boolean {
        val salesCount = db.saleDao().getSalesCountForProduct(productId)
        if (salesCount > 0) {
            return false // Cannot delete product with sales
        }
        db.productDao().deleteProductById(productId)
        return true
    }

    suspend fun addCustomer(customer: CustomerEntity) {
        db.customerDao().insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: CustomerEntity) {
        db.customerDao().updateCustomer(customer)
    }

    suspend fun deleteCustomer(id: Int) {
        db.customerDao().deleteCustomerById(id)
    }

    suspend fun createSale(
        items: List<CartItem>,
        customerName: String,
        status: String
    ): String {
        val nextInvoiceNum = (db.saleDao().getMaxInvoiceNumber() ?: 1000) + 1
        val transactionId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val saleEntities = items.map { cartItem ->
            // Deduct stock quantity
            db.productDao().deductStock(cartItem.product.id, cartItem.quantity)

            SaleEntity(
                transactionId = transactionId,
                invoiceNumber = nextInvoiceNum,
                productId = cartItem.product.id,
                productName = cartItem.product.name,
                quantity = cartItem.quantity,
                salePrice = cartItem.salePrice,
                costPrice = cartItem.product.costPrice,
                customerName = customerName,
                status = status,
                date = now
            )
        }

        db.saleDao().insertSales(saleEntities)
        return transactionId
    }

    suspend fun deleteInvoice(transactionId: String) {
        val items = db.saleDao().getSalesByTransactionId(transactionId)
        for (item in items) {
            db.productDao().restoreStock(item.productId, item.quantity)
        }
        db.saleDao().deleteInvoiceByTransactionId(transactionId)
    }

    suspend fun processReturn(
        saleItem: SaleEntity,
        returnQty: Int,
        reason: String
    ) {
        if (returnQty <= 0) return
        db.productDao().restoreStock(saleItem.productId, returnQty)
        db.salesReturnDao().insertSalesReturn(
            SalesReturnEntity(
                saleId = saleItem.id,
                transactionId = saleItem.transactionId,
                productId = saleItem.productId,
                productName = saleItem.productName,
                quantityReturned = returnQty,
                reason = reason
            )
        )

        if (returnQty >= saleItem.quantity) {
            db.saleDao().deleteSaleItem(saleItem.id)
        } else {
            db.saleDao().reduceSaleQuantity(saleItem.id, returnQty)
        }
    }

    suspend fun createReceipt(customerName: String, amount: Double, notes: String): Int {
        val nextReceiptNum = (db.receiptDao().getMaxReceiptNumber() ?: 600) + 1
        db.receiptDao().insertReceipt(
            ReceiptEntity(
                receiptNumber = nextReceiptNum,
                customerName = customerName,
                amount = amount,
                notes = notes
            )
        )
        return nextReceiptNum
    }

    suspend fun deleteReceipt(id: Int) {
        db.receiptDao().deleteReceiptById(id)
    }

    suspend fun createDisbursement(purpose: String, amount: Double, notes: String) {
        db.disbursementDao().insertDisbursement(
            DisbursementEntity(
                purpose = purpose,
                amount = amount,
                notes = notes
            )
        )
    }

    suspend fun deleteDisbursement(id: Int) {
        db.disbursementDao().deleteDisbursementById(id)
    }

    suspend fun getAllDataForBackup(): com.example.data.remote.BackupPayload {
        return com.example.data.remote.BackupPayload(
            products = products.first(),
            customers = customers.first(),
            sales = sales.first(),
            receipts = receipts.first(),
            disbursements = disbursements.first(),
            salesReturns = salesReturns.first()
        )
    }

    suspend fun restoreFromBackup(payload: com.example.data.remote.BackupPayload) {
        payload.products.forEach { db.productDao().insertProduct(it) }
        payload.customers.forEach { db.customerDao().insertCustomer(it) }
        db.saleDao().insertSales(payload.sales)
        payload.receipts.forEach { db.receiptDao().insertReceipt(it) }
        payload.disbursements.forEach { db.disbursementDao().insertDisbursement(it) }
        payload.salesReturns.forEach { db.salesReturnDao().insertSalesReturn(it) }
    }

    suspend fun seedSampleDataIfEmpty() {
        if (db.productDao().getProductById(1) == null) {
            // Seed products
            val p1 = ProductEntity(
                name = "مياه معدنية 1.5 لتر (كرتونة)",
                description = "كرتونة مياه 12 عبوة 1.5 لتر",
                quantity = 45,
                price = 3.50,
                costPrice = 2.20,
                lowStockThreshold = 10
            )
            val p2 = ProductEntity(
                name = "عصير برتقال طبيعي 250 مل",
                description = "صندوق 24 عبوة زجاجية",
                quantity = 4, // Low stock!
                price = 12.00,
                costPrice = 8.50,
                lowStockThreshold = 10
            )
            val p3 = ProductEntity(
                name = "شيبس بطاطا عائلي 150 غرام",
                description = "شدة 12 كيس بنكهة الملح والخل",
                quantity = 28,
                price = 6.00,
                costPrice = 4.00,
                lowStockThreshold = 8
            )
            val p4 = ProductEntity(
                name = "بسكويت بالشوكولاتة 12 قطعة",
                description = "علبة كرتونية للمحلات",
                quantity = 2, // Low stock!
                price = 4.50,
                costPrice = 3.00,
                lowStockThreshold = 5
            )
            db.productDao().insertProduct(p1)
            db.productDao().insertProduct(p2)
            db.productDao().insertProduct(p3)
            db.productDao().insertProduct(p4)

            // Seed customers matching screenshots
            val c1 = CustomerEntity(
                name = "عميل عام",
                phone = "0789876543",
                location = "الزرقاء، حي معصوم"
            )
            val c2 = CustomerEntity(
                name = "مؤسسة النور التجارية",
                phone = "0771122334",
                location = "اربد، شارع الجامعة"
            )
            val c3 = CustomerEntity(
                name = "محطة المحروقات الدولية",
                phone = "0791234567",
                location = "عمان، شارع مكة"
            )
            db.customerDao().insertCustomer(c1)
            db.customerDao().insertCustomer(c2)
            db.customerDao().insertCustomer(c3)

            // Seed initial sales/receipts for "عميل عام" to display realistic Account Statement matching screenshot!
            val t1 = UUID.randomUUID().toString()
            val t2 = UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            val dayMs = 24 * 60 * 60 * 1000L

            db.saleDao().insertSales(
                listOf(
                    SaleEntity(
                        transactionId = t1,
                        invoiceNumber = 1021,
                        productId = 1,
                        productName = "مياه معدنية 1.5 لتر (كرتونة)",
                        quantity = 500,
                        salePrice = 3.50,
                        costPrice = 2.20,
                        customerName = "عميل عام",
                        status = "debt",
                        date = now - (15 * dayMs)
                    )
                )
            )

            db.receiptDao().insertReceipt(
                ReceiptEntity(
                    receiptNumber = 654,
                    customerName = "عميل عام",
                    amount = 1000.00,
                    notes = "دفعة حساب تحت الحساب",
                    date = now - (6 * dayMs)
                )
            )

            db.saleDao().insertSales(
                listOf(
                    SaleEntity(
                        transactionId = t2,
                        invoiceNumber = 1042,
                        productId = 3,
                        productName = "شيبس بطاطا عائلي",
                        quantity = 100,
                        salePrice = 5.00,
                        costPrice = 3.50,
                        customerName = "عميل عام",
                        status = "debt",
                        date = now - (1 * dayMs)
                    )
                )
            )

            // Seed sample disbursement
            db.disbursementDao().insertDisbursement(
                DisbursementEntity(
                    purpose = "petrol",
                    amount = 25.00,
                    notes = "تعبئة بنزين للسيارة أثناء الجولة",
                    date = now - (2 * dayMs)
                )
            )
        }
    }
}

data class CartItem(
    val product: ProductEntity,
    var quantity: Int = 1,
    var salePrice: Double = product.price
)
