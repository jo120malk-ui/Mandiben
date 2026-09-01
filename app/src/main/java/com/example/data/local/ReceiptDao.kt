package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM receipts ORDER BY date DESC")
    fun getAllReceipts(): Flow<List<ReceiptEntity>>

    @Query("SELECT * FROM receipts WHERE customerName = :customerName ORDER BY date ASC")
    fun getReceiptsByCustomer(customerName: String): Flow<List<ReceiptEntity>>

    @Query("SELECT MAX(receiptNumber) FROM receipts")
    suspend fun getMaxReceiptNumber(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: ReceiptEntity): Long

    @Query("DELETE FROM receipts WHERE id = :id")
    suspend fun deleteReceiptById(id: Int)
}
