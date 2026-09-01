package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val barcode: String = "",
    val quantity: Int,
    val price: Double,
    val costPrice: Double = 0.0,
    val lowStockThreshold: Int = 5,
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
