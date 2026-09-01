package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales_returns")
data class SalesReturnEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val saleId: Int,
    val transactionId: String,
    val productId: Int,
    val productName: String,
    val quantityReturned: Int,
    val reason: String = "",
    val date: Long = System.currentTimeMillis()
)
