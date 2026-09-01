#!/bin/bash
sed -i '/data class RedeemCodeRequest(/i \
@JsonClass(generateAdapter = true)\
data class BackupPayload(\
    val products: List<com.example.data.local.ProductEntity>,\
    val customers: List<com.example.data.local.CustomerEntity>,\
    val sales: List<com.example.data.local.SaleEntity>,\
    val receipts: List<com.example.data.local.ReceiptEntity>,\
    val disbursements: List<com.example.data.local.DisbursementEntity>,\
    val salesReturns: List<com.example.data.local.SalesReturnEntity>\
)\
\
@JsonClass(generateAdapter = true)\
data class BackupRequest(\
    val rep_phone: String,\
    val backup_data: BackupPayload,\
    val last_synced: Long\
)\
' app/src/main/java/com/example/data/remote/SupabaseClient.kt

sed -i '/interface SupabaseApi {/a \
    @POST("rest/v1/company_backups")\
    @Headers("Prefer: resolution=merge-duplicates")\
    suspend fun upsertBackup(\
        @Header("apikey") apiKey: String,\
        @Header("Authorization") auth: String,\
        @Body request: BackupRequest\
    ): Response<Any>\
\
    @GET("rest/v1/company_backups")\
    suspend fun getBackup(\
        @Header("apikey") apiKey: String,\
        @Header("Authorization") auth: String,\
        @Query("rep_phone") phone: String\
    ): Response<List<BackupRequest>>\
' app/src/main/java/com/example/data/remote/SupabaseClient.kt
