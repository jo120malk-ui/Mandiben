package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DisbursementDao {
    @Query("SELECT * FROM disbursements ORDER BY date DESC")
    fun getAllDisbursements(): Flow<List<DisbursementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisbursement(disbursement: DisbursementEntity): Long

    @Query("DELETE FROM disbursements WHERE id = :id")
    suspend fun deleteDisbursementById(id: Int)
}
