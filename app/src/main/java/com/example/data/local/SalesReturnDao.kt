package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SalesReturnDao {
    @Query("SELECT * FROM sales_returns ORDER BY date DESC")
    fun getAllSalesReturns(): Flow<List<SalesReturnEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalesReturn(salesReturn: SalesReturnEntity): Long
}
