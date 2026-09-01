#!/bin/bash
# Fix SupabaseClient.kt imports
sed -i '/import com.squareup.moshi.JsonClass/d' app/src/main/java/com/example/data/remote/SupabaseClient.kt
sed -i '/@JsonClass(generateAdapter = true)/d' app/src/main/java/com/example/data/remote/SupabaseClient.kt
sed -i '/data class RedeemCodeRequest(/d' app/src/main/java/com/example/data/remote/SupabaseClient.kt
sed -i '/val input_code: String,/d' app/src/main/java/com/example/data/remote/SupabaseClient.kt
sed -i '/val user_phone: String/d' app/src/main/java/com/example/data/remote/SupabaseClient.kt
sed -i '/)/d' app/src/main/java/com/example/data/remote/SupabaseClient.kt

sed -i '1s/^/package com.example.data.remote\nimport com.squareup.moshi.JsonClass\n/' app/src/main/java/com/example/data/remote/SupabaseClient.kt
sed -i 's/package com.example.data.remote//2' app/src/main/java/com/example/data/remote/SupabaseClient.kt

cat << 'INNEREOF' >> app/src/main/java/com/example/data/remote/SupabaseClient.kt

@JsonClass(generateAdapter = true)
data class RedeemCodeRequest(
    val input_code: String,
    val user_phone: String
)
INNEREOF

# Fix ViewModel
sed -i 's/replace(""", "")/replace("\\\"", "")/g' app/src/main/java/com/example/ui/BerboxViewModel.kt

