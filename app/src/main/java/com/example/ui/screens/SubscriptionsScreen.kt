package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CompanyEntity

@Composable
fun SubscriptionsScreen(
    onRedeemCode: (String) -> Unit = {},
    company: CompanyEntity?,
    onSubscribePlan: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var isEnteringCode by remember { mutableStateOf(false) }
    var activationCode by remember { mutableStateOf("") }
    var selectedPlan by remember { mutableStateOf("monthly") } // "monthly" or "annual"

    // Background Gradient Mesh
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFCFCFC))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            // pink blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFE4E8).copy(alpha = 0.6f), Color.Transparent),
                    center = Offset(width * 0.1f, height * 0.15f),
                    radius = width * 0.7f
                )
            )
            // blue blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE4F0FF).copy(alpha = 0.6f), Color.Transparent),
                    center = Offset(width * 0.9f, height * 0.35f),
                    radius = width * 0.7f
                )
            )
            // yellow blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFF7D6).copy(alpha = 0.5f), Color.Transparent),
                    center = Offset(width * 0.4f, height * 0.75f),
                    radius = width * 0.8f
                )
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.05f))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Enter Code button / Field
                    if (!isEnteringCode) {
                        Text(
                            text = "إدخال الكود",
                            color = Color.DarkGray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { isEnteringCode = true }
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = activationCode,
                                onValueChange = { activationCode = it },
                                placeholder = { Text("الكود", fontSize = 12.sp) },
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(44.dp),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.White,
                                    focusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color.LightGray,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { 
                                    if (activationCode.isNotBlank()) {
                                        onRedeemCode(activationCode)
                                        isEnteringCode = false
                                    }
                                },
                                modifier = Modifier.height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Text("تأكيد", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Main Title
            item {
                Text(
                    text = "احصل على النسخة الاحترافية\nالآن مجاناً لأسبوع",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    lineHeight = 44.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Start
                )
            }

            // Features List
            item {
                val features = listOf(
                    "إدارة عدد غير محدود من الفواتير والمنتجات",
                    "تقارير وتحليلات متقدمة للأرباح",
                    "دعم فني على مدار الساعة",
                    "نسخ احتياطي سحابي آمن"
                )
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    features.forEach { feat ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .border(1.dp, Color.DarkGray, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.DarkGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = feat,
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Pricing Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Monthly Card
                    PricingCard(
                        modifier = Modifier.weight(1f),
                        title = "شهري",
                        price = "9.99 د.أ",
                        originalPrice = null,
                        description = "تجديد شهري",
                        badge = null,
                        isSelected = selectedPlan == "monthly",
                        onClick = { selectedPlan = "monthly" }
                    )

                    // Annual Card
                    PricingCard(
                        modifier = Modifier.weight(1f),
                        title = "سنوي",
                        price = "99.99 د.أ",
                        originalPrice = "119.88 د.أ",
                        description = "يُدفع سنوياً",
                        badge = "وفر 17%",
                        isSelected = selectedPlan == "annual",
                        onClick = { selectedPlan = "annual" }
                    )
                }
            }

            // Bottom Button (WhatsApp)
            item {
                Button(
                    onClick = {
                        val phone = "962776255805"
                        val msg = "أرغب بالاشتراك في النسخة الاحترافية (Pro)"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$phone?text=${Uri.encode(msg)}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366) // WhatsApp Green
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "تواصل معنا عبر واتساب",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        // Phone Icon inside bubble
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp, 8.dp, 0.dp, 8.dp)) // Bubble shape
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "WhatsApp",
                                tint = Color(0xFF25D366),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "سيتم تحويلك إلى واتساب للتواصل مع فريق الدعم\nلإتمام عملية الاشتراك وتفعيل حسابك",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 18.sp
                )
            }

            // Footer
            item {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("استعادة المشتريات", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text(" • ", color = Color.Gray, fontSize = 11.sp)
                    Text("شروط الاستخدام", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text(" • ", color = Color.Gray, fontSize = 11.sp)
                    Text("سياسة الخصوصية", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PricingCard(
    modifier: Modifier = Modifier,
    title: String,
    price: String,
    originalPrice: String?,
    description: String,
    badge: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val gradientBorder = if (isSelected) {
        Brush.linearGradient(listOf(Color(0xFFFFA9C5), Color(0xFF90C8FF)))
    } else {
        SolidColor(Color.LightGray.copy(alpha = 0.3f))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.8f))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                brush = gradientBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Column {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(24.dp)
                        .background(Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                color = Color.DarkGray,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (originalPrice != null) {
                Text(
                    text = originalPrice,
                    color = Color.Gray,
                    fontSize = 13.sp,
                    textDecoration = TextDecoration.LineThrough
                )
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }
            
            Text(
                text = price,
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                color = Color.Gray,
                fontSize = 12.sp
            )

            if (badge != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFFFFA9C5), Color(0xFF90C8FF))
                            ),
                            shape = CircleShape
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(44.dp))
            }
        }
    }
}
