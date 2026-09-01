package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "companies")
@JsonClass(generateAdapter = true)
data class CompanyEntity(
    @Json(ignore = true)
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    
    @Json(name = "company_name")
    val companyName: String,
    
    @Json(name = "rep_name")
    val repName: String,
    
    @Json(name = "rep_phone")
    val repPhone: String,
    
    @Json(name = "password")
    val password: String = "", 
    
    @Json(name = "location_text")
    val locationText: String,
    
    @Json(name = "location_lat")
    val locationLat: Double = 31.9539,
    
    @Json(name = "location_lng")
    val locationLng: Double = 35.9106,
    
    @Json(name = "logo_url")
    val logoUrl: String? = null,
    
    @Json(name = "trial_ends_at")
    val trialEndsAt: Long = System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000L),
    
    @Json(name = "subscription_plan")
    val subscriptionPlan: String = "free", 
    
    @Json(name = "subscription_expires_at")
    val subscriptionExpiresAt: Long = System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000L),
    
    @Json(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

fun CompanyEntity?.isProActive(): Boolean {
    if (this == null) return false
    val now = System.currentTimeMillis()
    return now < trialEndsAt || now < subscriptionExpiresAt
}
