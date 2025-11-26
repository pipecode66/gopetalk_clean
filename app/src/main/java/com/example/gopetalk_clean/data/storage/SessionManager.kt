package com.example.gopetalk_clean.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefsName = "gopetalk_session"
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val KEY_CURRENT_CHANNEL = "CURRENT_CHANNEL"
        private const val KEY_USER_ID = "USER_ID"
        private const val KEY_USER_FIRST_NAME = "USER_FIRST_NAME"
        private const val KEY_USER_LAST_NAME = "USER_LAST_NAME"
        private const val KEY_USER_EMAIL = "USER_EMAIL"
    }

    fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun saveUserId(userId: Int) {
        sharedPreferences.edit { putInt(KEY_USER_ID, userId) }
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }


    fun saveUserName(name: String) {
        sharedPreferences.edit { putString(KEY_USER_FIRST_NAME, name) }
    }

    fun saveUserLastName(lastName: String) {
        sharedPreferences.edit { putString(KEY_USER_LAST_NAME, lastName) }
    }

    fun saveUserEmail(email: String) {
        sharedPreferences.edit { putString(KEY_USER_EMAIL, email) }
    }

    fun saveCurrentChannel(channel: String) {
        sharedPreferences.edit { putString(KEY_CURRENT_CHANNEL, channel) }
    }

    fun getCurrentChannel(): String? {
        return sharedPreferences.getString(KEY_CURRENT_CHANNEL, null)
    }

    fun clearCurrentChannel() {
        sharedPreferences.edit { remove(KEY_CURRENT_CHANNEL) }
    }

    fun clearSession() {
        sharedPreferences.edit { clear() }
    }

}


