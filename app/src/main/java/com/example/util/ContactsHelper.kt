package com.example.util

import android.content.Context
import android.provider.ContactsContract

data class DeviceContact(
    val name: String,
    val phone: String
)

object ContactsHelper {
    fun fetchDeviceContacts(context: Context): List<DeviceContact> {
        val contacts = mutableListOf<DeviceContact>()
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        try {
            val cursor = context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )

            cursor?.use { c ->
                val nameIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                val seenPhones = mutableSetOf<String>()

                while (c.moveToNext()) {
                    val name = if (nameIdx >= 0) c.getString(nameIdx) ?: "" else ""
                    val rawNumber = if (numberIdx >= 0) c.getString(numberIdx) ?: "" else ""
                    val cleanNumber = rawNumber.replace("\\s+".toRegex(), "").replace("-", "")

                    if (name.isNotBlank() && cleanNumber.isNotBlank() && !seenPhones.contains(cleanNumber)) {
                        seenPhones.add(cleanNumber)
                        contacts.add(DeviceContact(name.trim(), rawNumber.trim()))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return contacts
    }
}
