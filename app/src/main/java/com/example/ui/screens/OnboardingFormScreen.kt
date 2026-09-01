package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class FormStep {
    COMPANY_NAME,
    REP_NAME,
    PHONE_AND_PASSWORD,
    LOCATION
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingFormScreen(
    initialRepName: String? = null,
    onSubmitForm: (companyName: String, repName: String, repPhone: String, password: String, location: String, lat: Double, lng: Double) -> Unit
) {
    var currentStep by remember { mutableStateOf(FormStep.COMPANY_NAME) }

    var companyName by remember { mutableStateOf("") }
    var repName by remember { mutableStateOf(initialRepName ?: "") }
    var repPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf(0.0) }
    var lng by remember { mutableStateOf(0.0) }

    var showError by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp).imePadding()
        ) {
            // Top Bar with Back Button
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep != FormStep.COMPANY_NAME) {
                    IconButton(
                        onClick = {
                            currentStep = when (currentStep) {
                                FormStep.REP_NAME -> FormStep.COMPANY_NAME
                                FormStep.PHONE_AND_PASSWORD -> FormStep.REP_NAME
                                FormStep.LOCATION -> FormStep.PHONE_AND_PASSWORD
                                else -> currentStep
                            }
                            showError = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "خطوة ${currentStep.ordinal + 1} من 4",
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                )
            }

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        slideInHorizontally(animationSpec = tween(300)) { width -> width } + fadeIn() with
                        slideOutHorizontally(animationSpec = tween(300)) { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally(animationSpec = tween(300)) { width -> -width } + fadeIn() with
                        slideOutHorizontally(animationSpec = tween(300)) { width -> width } + fadeOut()
                    }
                },
                label = "form_steps"
            ) { step ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (step) {
                            FormStep.COMPANY_NAME -> {
                                StepHeader(
                                    title = "اسم الشركة أو المحل",
                                    subtitle = "أدخل الاسم التجاري لنشاطك، سيظهر في الفواتير.",
                                    icon = Icons.Default.Business
                                )
                                OutlinedTextField(
                                    value = companyName,
                                    onValueChange = { companyName = it; showError = false },
                                    label = { Text("اسم الشركة / المحل *") },
                                    isError = showError,
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                if (showError) ErrorText("يرجى إدخال اسم الشركة")
                            }
                            FormStep.REP_NAME -> {
                                StepHeader(
                                    title = "اسم المندوب أو المدير",
                                    subtitle = "أدخل اسم المسؤول عن هذا الحساب.",
                                    icon = Icons.Default.Person
                                )
                                OutlinedTextField(
                                    value = repName,
                                    onValueChange = { repName = it; showError = false },
                                    label = { Text("الاسم *") },
                                    isError = showError,
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                if (showError) ErrorText("يرجى إدخال الاسم")
                            }
                            FormStep.PHONE_AND_PASSWORD -> {
                                StepHeader(
                                    title = "رقم الهاتف وكلمة المرور",
                                    subtitle = "يستخدم رقم الهاتف لتسجيل الدخول والمزامنة.",
                                    icon = Icons.Default.Phone
                                )
                                OutlinedTextField(
                                    value = repPhone,
                                    onValueChange = { repPhone = it; showError = false },
                                    label = { Text("رقم الهاتف (07XXXXXXXX) *") },
                                    isError = showError,
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it; showError = false },
                                    label = { Text("كلمة المرور *") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    isError = showError,
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                                )
                                if (showError) ErrorText("تأكد من رقم الهاتف الأردني وكلمة المرور")
                            }
                            FormStep.LOCATION -> {
                                StepHeader(
                                    title = "موقع النشاط التجاري",
                                    subtitle = "يمكنك إضافة موقع المحل، أو تحديد موقعك الحالي.",
                                    icon = Icons.Default.LocationOn
                                )
                                OutlinedTextField(
                                    value = locationText,
                                    onValueChange = { locationText = it },
                                    label = { Text("العنوان (اختياري)") },
                                    trailingIcon = {
                                        IconButton(onClick = {
                                            locationText = "عمان، شارع مكة - مجمع الأمل"
                                            lat = 31.9680
                                            lng = 35.8500
                                        }) {
                                            Icon(Icons.Default.MyLocation, "موقعي", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                when (step) {
                                    FormStep.COMPANY_NAME -> {
                                        if (companyName.isNotBlank()) currentStep = FormStep.REP_NAME
                                        else showError = true
                                    }
                                    FormStep.REP_NAME -> {
                                        if (repName.isNotBlank()) currentStep = FormStep.PHONE_AND_PASSWORD
                                        else showError = true
                                    }
                                    FormStep.PHONE_AND_PASSWORD -> {
                                        val validPhone = repPhone.trim().matches(Regex("^07[789]\\d{7}$"))
                                        if (validPhone && password.isNotBlank()) currentStep = FormStep.LOCATION
                                        else showError = true
                                    }
                                    FormStep.LOCATION -> {
                                        onSubmitForm(companyName, repName, repPhone, password, locationText, lat, lng)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = if (step == FormStep.LOCATION) "حفظ والبدء" else "التالي",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepHeader(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier.size(72.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun ErrorText(msg: String) {
    Text(
        text = msg,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
    )
}
