package ir.rahkarpouya.rpandroidlib

import android.content.Context

class RPSharedPreference(private val context: Context) {

    fun saveData(key: String, value: Any) {
        val settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = settings.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            else -> error("Only primitive types can be stored in SharedPreferences")
        }
        editor.apply()
    }

    fun getString(key: String): String {
        val settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return if (settings.contains(key))
            settings.getString(key, null)!!
        else ""
    }

    fun getInt(key: String): Int {
        val settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return if (settings.contains(key))
            settings.getInt(key, -1)
        else -1
    }

    fun getBoolean(key: String): Boolean {
        val settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return if (settings.contains(key))
            settings.getBoolean(key, false)
        else false
    }

    fun getLong(key: String): Long {
        val settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return if (settings.contains(key))
            settings.getLong(key, 0)
        else 0
    }

    fun getFloat(key: String): Float {
        val settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return if (settings.contains(key))
            settings.getFloat(key, 0f)
        else 0F
    }

    companion object {
        private const val PREFS_NAME = "PREFS_NAME"
    }

}
