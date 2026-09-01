package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSales(): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE customerName = :customerName ORDER BY date ASC")
    fun getSalesByCustomer(customerName: String): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE transactionId = :transactionId")
    suspend fun getSalesByTransactionId(transactionId: String): List<SaleEntity>

    @Query("SELECT MAX(invoiceNumber) FROM sales")
    suspend fun getMaxInvoiceNumber(): Int?

    @Query("SELECT COUNT(*) FROM sales WHERE productId = :productId")
    suspend fun getSalesCountForProduct(productId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSales(sales: List<SaleEntity>)

    @Query("DELETE FROM sales WHERE transactionId = :transactionId")
    suspend fun deleteInvoiceByTransactionId(transactionId: String)

    @Query("UPDATE sales SET quantity = quantity - :returnQty WHERE id = :saleId")
    suspend fun reduceSaleQuantity(saleId: Int, returnQty: Int)

    @Query("DELETE FROM sales WHERE id = :saleId")
    suspend fun deleteSaleItem(saleId: Int)
}
