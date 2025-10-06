package com.example.krishna_project.session

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        const val USER_ID = "user_id"
        const val USER_ROLE = "user_role"
    }

    fun saveSession(userId: String, role: String) {
        val editor = prefs.edit()
        editor.putString(USER_ID, userId)
        editor.putString(USER_ROLE, role)
        editor.apply()
    }

    fun getUserId(): String? {
        return prefs.getString(USER_ID, null)
    }

    fun getUserRole(): String? {
        return prefs.getString(USER_ROLE, null)
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
