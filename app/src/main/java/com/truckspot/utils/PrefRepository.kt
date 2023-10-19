package com.truckspot.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.util.*


class PrefRepository(val context: Context) {

    private val pref: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()
    private val gson = Gson()

    private fun String.put(long: Long) {
        editor.putLong(this, long)
        editor.commit()
    }

    private fun String.put(int: Int) {
        editor.putInt(this, int)
        editor.commit()
    }

    private fun String.put(string: String) {
        editor.putString(this, string)
        editor.commit()
    }

    private fun String.put(boolean: Boolean) {
        editor.putBoolean(this, boolean)
        editor.commit()
    }

    private fun String.getLong() = pref.getLong(this, 0)

    private fun String.getInt() = pref.getInt(this, 0)

    private fun String.getString() = pref.getString(this, "")!!

    private fun String.getBoolean() = pref.getBoolean(this, false)

    fun setLoggedIn(isLoggedIn: Boolean) {
        PREF_LOGGED_IN.put(isLoggedIn)
    }

    fun setMode(mode: String) {
        PREF_CURRENT_MODE.put(mode)
    }

    fun getMode(): String {
        val mode = PREF_CURRENT_MODE.getString()
        if (mode == null || mode.isEmpty()) return "inital"
        return mode
    }

    fun getLoggedIn() = PREF_LOGGED_IN.getBoolean()

    fun setUserName(username: String) {
        PREF_USERNAME.put(username)
    }

    fun getUserName() = PREF_USERNAME.getString()

    fun setPassword(password: String) {
        PREF_PASSWORD.put(password)
    }

    fun getPassword() = PREF_PASSWORD.getString()
    fun setName(name: String) {
        PREF_NAME.put(name)
    }

    fun getShippingNumber() = PREF_SHIPPING_NUMBER.getString()

    fun setShippingNumber(number: String) {
        PREF_SHIPPING_NUMBER.put(number)
    }

    fun setTrailerNumber(number: String) {
        PREF_TRAILER_NO.put(number)
    }

    fun getTrailerNumber() = PREF_TRAILER_NO.getString()

    fun setLastLogTime() {
        PREF_LAST_LOG_TIME.put(Date().time)
    }

    fun getLastLogTime() = PREF_LAST_LOG_TIME.getLong()

    fun getLogTimeDifference() = Date().time - PREF_LAST_LOG_TIME.getLong()
    fun getName() = PREF_NAME.getString()

    fun setToken(token: String) {
        PREF_TOKEN.put(token)
    }

    fun getToken() = PREF_TOKEN.getString()


    //    fun setLastRefreshTime(date: Date) {
//        PREF_LAST_REFRESH_TIME.put(gson.toJson(date))
//    }
//
//    fun getLastRefreshTime(): Date? {
//        PREF_LAST_REFRESH_TIME.getString().also {
//            return if (it.isNotEmpty())
//                gson.fromJson(PREF_LAST_REFRESH_TIME.getString(), Date::class.java)
//            else
//                null
//        }
//    }
    fun getObject(key: String, classOfT: Class<*>): Any? {
        val json = pref.getString(key, "")
        val value = try {
            Gson().fromJson(json, classOfT) ?: null
        } catch (e: Exception) {
            return null
        }
        return value
    }

    fun putObject(key: String, obj: Any) {
        editor.putString(key, "")
        val gson = Gson()
        editor.putString(key, gson.toJson(obj))
        editor.commit()
    }

    fun clearData() {
        editor.clear()
        editor.commit()
    }


}
