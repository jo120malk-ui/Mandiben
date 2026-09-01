#!/bin/bash
sed -i '/fun updateSubscription(plan: String) {/i \
    fun redeemActivationCode(code: String) {\
        viewModelScope.launch {\
            val company = repository.getCompanyOnce() ?: return@launch\
            _isLoading.value = true\
            try {\
                val request = com.example.data.remote.RedeemCodeRequest(\
                    input_code = code.trim(),\
                    user_phone = company.repPhone\
                )\
                val response = com.example.data.remote.SupabaseClient.api.redeemCode(\
                    apiKey = com.example.data.remote.SupabaseClient.supabaseAnonKey,\
                    auth = "Bearer ${com.example.data.remote.SupabaseClient.supabaseAnonKey}",\
                    request = request\
                )\
                if (response.isSuccessful) {\
                    val result = response.body()?.toString()?.replace("\"", "") ?: "INVALID"\
                    when (result) {\
                        "INVALID" -> _userMessage.value = "الكود غير صحيح أو غير موجود."\
                        "USED" -> _userMessage.value = "عذراً، هذا الكود تم استخدامه مسبقاً!"\
                        else -> {\
                            updateSubscription(result)\
                            _userMessage.value = "تم تفعيل الاشتراك بنجاح! شكراً لك."\
                        }\
                    }\
                } else {\
                    _userMessage.value = "حدث خطأ في الاتصال بالسيرفر. تأكد من الإنترنت."\
                }\
            } catch (e: Exception) {\
                _userMessage.value = "حدث خطأ: ${e.localizedMessage}"\
            } finally {\
                _isLoading.value = false\
            }\
        }\
    }\
' app/src/main/java/com/example/ui/BerboxViewModel.kt
