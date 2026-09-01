#!/bin/bash
sed -i '/import com.squareup.moshi.Moshi/i \
import com.squareup.moshi.JsonClass\
\
@JsonClass(generateAdapter = true)\
data class RedeemCodeRequest(\
    val input_code: String,\
    val user_phone: String\
)' app/src/main/java/com/example/data/remote/SupabaseClient.kt

sed -i '/interface SupabaseApi {/a \
    @POST("rest/v1/rpc/redeem_code")\
    suspend fun redeemCode(\
        @Header("apikey") apiKey: String,\
        @Header("Authorization") auth: String,\
        @Body request: RedeemCodeRequest\
    ): Response<Any>\
' app/src/main/java/com/example/data/remote/SupabaseClient.kt
