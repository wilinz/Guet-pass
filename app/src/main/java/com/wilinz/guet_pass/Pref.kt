package com.wilinz.guet_pass
import androidx.preference.PreferenceManager

object Pref{
    val pref = PreferenceManager.getDefaultSharedPreferences(App.application)

    var lastName: String = "有"
        get() = pref.getString("last_name", "有")!!
        set(value) {
            field = value
            pref.edit().putString("last_name", value).apply()
        }

    var headPath: String? = null
        get() = pref.getString("head_path", null)
        set(value) {
            field = value
            pref.edit().putString("head_path", value).apply()
        }

}
