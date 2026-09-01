package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disbursements")
data class DisbursementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val purpose: String, // petrol, food, repairs, fines, other
    val amount: Double,
    val notes: String = "",
    val date: Long = System.currentTimeMillis()
)
