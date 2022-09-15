package com.agdemidov.nasaclient.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

const val APP_PREFERENCES_NAME = "app_prefs"
const val NEO_CACHED_OBJECT = "neo_cached_object"

object AppPreferences {
    lateinit var instance: SharedPreferences
        private set

    fun initialize(context: Context) {
        instance = context.getSharedPreferences(
            APP_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun <T> putModel(key: String, obj: T) {
        val jsonString = Gson().toJson(obj)
        instance.edit().putString(key, jsonString).apply()
    }

    inline fun <reified T> getModel(key: String): T? {
        val value = instance.getString(key, null)
        return Gson().fromJson(value, T::class.java)
    }
}
