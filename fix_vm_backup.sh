#!/bin/bash
sed -i '/fun updateSubscription(plan: String) {/i \
    fun autoSyncIfPro() {\
        viewModelScope.launch(Dispatchers.IO) {\
            val company = repository.getCompanyOnce() ?: return@launch\
            val isPro = company.subscriptionPlan in listOf("monthly", "yearly", "three_years")\
            if (!isPro) return@launch\
            try {\
                val payload = repository.getAllDataForBackup()\
                val request = com.example.data.remote.BackupRequest(\
                    rep_phone = company.repPhone,\
                    backup_data = payload,\
                    last_synced = System.currentTimeMillis()\
                )\
                com.example.data.remote.SupabaseClient.api.upsertBackup(\
                    apiKey = com.example.data.remote.SupabaseClient.supabaseAnonKey,\
                    auth = "Bearer ${com.example.data.remote.SupabaseClient.supabaseAnonKey}",\
                    request = request\
                )\
            } catch (e: Exception) { \
                /* Silently fail background sync */ \
            }\
        }\
    }\
\
    fun syncNow() {\
        viewModelScope.launch(Dispatchers.IO) {\
            val company = repository.getCompanyOnce() ?: return@launch\
            val isPro = company.subscriptionPlan in listOf("monthly", "yearly", "three_years")\
            if (!isPro) {\
                _userMessage.value = "هذه الميزة متاحة للمشتركين بـ Pro فقط."\
                return@launch\
            }\
            _isLoading.value = true\
            try {\
                val payload = repository.getAllDataForBackup()\
                val request = com.example.data.remote.BackupRequest(\
                    rep_phone = company.repPhone,\
                    backup_data = payload,\
                    last_synced = System.currentTimeMillis()\
                )\
                val response = com.example.data.remote.SupabaseClient.api.upsertBackup(\
                    apiKey = com.example.data.remote.SupabaseClient.supabaseAnonKey,\
                    auth = "Bearer ${com.example.data.remote.SupabaseClient.supabaseAnonKey}",\
                    request = request\
                )\
                if (response.isSuccessful) {\
                    _userMessage.value = "تمت المزامنة السحابية بنجاح ☁️"\
                } else {\
                    _userMessage.value = "فشل في المزامنة السحابية."\
                }\
            } catch (e: Exception) {\
                _userMessage.value = "حدث خطأ: ${e.localizedMessage}"\
            } finally {\
                _isLoading.value = false\
            }\
        }\
    }\
\
    fun restoreBackup() {\
        viewModelScope.launch(Dispatchers.IO) {\
            val company = repository.getCompanyOnce() ?: return@launch\
            _isLoading.value = true\
            try {\
                val response = com.example.data.remote.SupabaseClient.api.getBackup(\
                    apiKey = com.example.data.remote.SupabaseClient.supabaseAnonKey,\
                    auth = "Bearer ${com.example.data.remote.SupabaseClient.supabaseAnonKey}",\
                    phone = company.repPhone\
                )\
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {\
                    val backupPayload = response.body()!!.first().backup_data\
                    repository.restoreFromBackup(backupPayload)\
                    _userMessage.value = "تم استرجاع البيانات بنجاح 🔄"\
                } else {\
                    _userMessage.value = "لا توجد نسخة احتياطية محفوظة."\
                }\
            } catch (e: Exception) {\
                _userMessage.value = "حدث خطأ أثناء الاسترجاع: ${e.localizedMessage}"\
            } finally {\
                _isLoading.value = false\
            }\
        }\
    }\
' app/src/main/java/com/example/ui/BerboxViewModel.kt
