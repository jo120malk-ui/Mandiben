package com.example.data.remote

import com.example.BuildConfig
import com.example.data.local.CompanyEntity
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class BackupPayload(
    val products: List<com.example.data.local.ProductEntity>,
    val customers: List<com.example.data.local.CustomerEntity>,
    val sales: List<com.example.data.local.SaleEntity>,
    val receipts: List<com.example.data.local.ReceiptEntity>,
    val disbursements: List<com.example.data.local.DisbursementEntity>,
    val salesReturns: List<com.example.data.local.SalesReturnEntity>
)

@JsonClass(generateAdapter = true)
data class BackupRequest(
    val rep_phone: String,
    val backup_data: BackupPayload,
    val last_synced: Long
)

data class RedeemCodeRequest(
    val input_code: String,
    val user_phone: String
)

interface SupabaseApi {
    @POST("rest/v1/company_backups")
    @Headers("Prefer: resolution=merge-duplicates")
    suspend fun upsertBackup(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Body request: BackupRequest
    ): Response<Any>

    @GET("rest/v1/company_backups")
    suspend fun getBackup(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("rep_phone") phone: String
    ): Response<List<BackupRequest>>

    @POST("rest/v1/rpc/redeem_code")
    suspend fun redeemCode(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Body request: RedeemCodeRequest
    ): Response<Any>

    @POST("rest/v1/companies")
    @Headers("Prefer: return=representation")
    suspend fun insertCompany(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Body company: CompanyEntity
    ): Response<List<CompanyEntity>>

    @GET("rest/v1/companies")
    suspend fun getCompanyByPhone(
        @Header("apikey") apiKey: String,
        @Header("Authorization") auth: String,
        @Query("rep_phone") phone: String
    ): Response<List<CompanyEntity>>
}

object SupabaseClient {
    val supabaseUrl: String
        get() = "https://wwaxdvbgdlasajwpfdod.supabase.co"
    val supabaseAnonKey: String
        get() = "sb_publishable_TC7X_8vXAVbI-sX3I8ieFQ_Dy7S7YUz"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    val api: SupabaseApi by lazy {
        Retrofit.Builder()
            .baseUrl(if (supabaseUrl.endsWith("/")) supabaseUrl else "$supabaseUrl/")
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SupabaseApi::class.java)
    }

    const val PROJECT_ID = "wwaxdvbgdlasajwpfdod"
    const val PROJECT_NAME = "jo120.malk@gmail.com's Project"
    const val REGION = "ap-northeast-2"

    fun isConnected(): Boolean {
        return supabaseUrl.isNotEmpty() && supabaseAnonKey.isNotEmpty()
    }
}
