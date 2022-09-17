package com.wilinz.guet_pass

import androidx.preference.PreferenceManager
import com.wilinz.guet_pass.tools.NameRandom

object Pref {
    val pref = PreferenceManager.getDefaultSharedPreferences(App.application)

    var passType: String = ""
        get() = pref.getString("pass_type", "桂电学生临时通行证")!!
        set(value) {
            field = value
            pref.edit().putString("pass_type", value).apply()
        }

    var lastName: String = ""
        get() {
            val lastName = pref.getString("last_name", "")!!
            return lastName.ifBlank { NameRandom.getChineseName().last().toString() }
        }
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
