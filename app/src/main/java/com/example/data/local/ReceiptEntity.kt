package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipts")
data class ReceiptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val receiptNumber: Int,
    val customerName: String,
    val amount: Double,
    val notes: String = "",
    val date: Long = System.currentTimeMillis()
)
