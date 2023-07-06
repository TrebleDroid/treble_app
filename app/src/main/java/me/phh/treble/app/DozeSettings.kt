package me.phh.treble.app

import android.hardware.Sensor
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import me.phh.treble.app.DozeSettings.CHOPCHOP_KEY

object DozeSettings : Settings {
    const val HANDWAVE_KEY = "key_doze_handwave"
    const val POCKET_KEY = "key_doze_pocket"
    const val CHOPCHOP_KEY = "key_doze_chopchop"
    const val CHOPCHOP_CATEGORY_KEY = "key_doze_chopchop_category"
    const val CHOPCHOP_AUTO_TURNOFF_KEY = "key_doze_chopchop_autoturnoff"
    const val CHOPCHOP_AUTO_TURNOFF_TIMEOUT_KEY = "key_doze_chopchop_autoturnoff_timout"
    const val CHOPCHOP_AUTO_TURNOFF_IGNORE_DND_KEY = "key_doze_chopchop_autoturnoff_ignorednd"

    override fun enabled(): Boolean {
        //TODO: Check if sensors are available and respond to interrupts
        return true
    }
}

class DozeSettingsFragment : SettingsFragment() {
    override val preferencesResId = R.xml.pref_doze

    //Checking for ChopChop Sensor
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        val chopchopCategoryPref = findPreference<PreferenceCategory>(DozeSettings.CHOPCHOP_CATEGORY_KEY)
        try {
            Doze.sensorManager.getSensorList(Sensor.TYPE_ALL).first { it.stringType == "com.motorola.sensor.chopchop" }

        } catch (e: Exception){

            chopchopCategoryPref!!.isVisible = false
            findPreference<SwitchPreference>(DozeSettings.CHOPCHOP_KEY)!!.isChecked = false
            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = sp.edit()
            editor.putBoolean(CHOPCHOP_KEY, false)
            editor.apply()
        }
    }
}
