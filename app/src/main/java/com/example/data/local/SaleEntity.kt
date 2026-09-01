package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transactionId: String, // UUID grouping items in an invoice
    val invoiceNumber: Int,    // Per-company invoice sequence #
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val salePrice: Double,
    val costPrice: Double = 0.0,
    val customerName: String,
    val status: String = "paid", // "paid" or "debt"
    val date: Long = System.currentTimeMillis()
)
