package com.example.banksoalstis.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("BankSoalSession", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    // Simpan Role & ID untuk logika UI nanti
    fun saveUserDetail(id: Long, role: String, name: String) {
        val editor = prefs.edit()
        editor.putLong("user_id", id)
        editor.putString("user_role", role)
        editor.putString("user_name", name)
        editor.apply()
    }

    fun getRole(): String? = prefs.getString("user_role", null)
    fun getUserId(): Long = prefs.getLong("user_id", -1)
    fun getName(): String? = prefs.getString("user_name", null)

    fun logout() {
        prefs.edit().clear().apply()
    }
}