#!/bin/bash
sed -i '/if (existing.repPhone == phone && existing.password == pass) {/a \
                    autoRestoreOnLogin(existing)\
' app/src/main/java/com/example/ui/BerboxViewModel.kt

sed -i '/} else if (existing.password.isBlank()) {/a \
                    autoRestoreOnLogin(existing)\
' app/src/main/java/com/example/ui/BerboxViewModel.kt

sed -i '/fun restoreBackup() {/i \
    private fun autoRestoreOnLogin(company: com.example.data.local.CompanyEntity) {\
        viewModelScope.launch(Dispatchers.IO) {\
            try {\
                val response = com.example.data.remote.SupabaseClient.api.getBackup(\
                    apiKey = com.example.data.remote.SupabaseClient.supabaseAnonKey,\
                    auth = "Bearer ${com.example.data.remote.SupabaseClient.supabaseAnonKey}",\
                    phone = company.repPhone\
                )\
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {\
                    val backupPayload = response.body()!!.first().backup_data\
                    repository.restoreFromBackup(backupPayload)\
                }\
            } catch (e: Exception) {\
                // Silently fail if no backup or network error\
            }\
        }\
    }\
' app/src/main/java/com/example/ui/BerboxViewModel.kt
