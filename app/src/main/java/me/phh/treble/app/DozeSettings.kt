package me.phh.treble.app

import android.hardware.Sensor
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference

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
        val chopchopPref = findPreference<SwitchPreference>(DozeSettings.CHOPCHOP_KEY)
        try {
            Doze.sensorManager.getSensorList(Sensor.TYPE_ALL).first { it.stringType == "com.motorola.sensor.chopchop" }
        } catch (e: Exception){
            chopchopCategoryPref!!.isVisible = false
            chopchopPref!!.isChecked = false
            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = sp.edit()
            editor.putBoolean(DozeSettings.CHOPCHOP_KEY, false)
            editor.apply()
        }
        chopchopPref!!.onPreferenceChangeListener = chopchopEnabledChangeListener
        findPreference<SwitchPreference>(DozeSettings.CHOPCHOP_AUTO_TURNOFF_KEY)!!.onPreferenceChangeListener = chopchopAutoturnoffChangeListener
        chopchopPref.callChangeListener(chopchopPref.isChecked)
    }

    private val chopchopEnabledChangeListener = Preference.OnPreferenceChangeListener { _, enabled ->
        val chopchopAutoTurnoffPref = findPreference<SwitchPreference>(DozeSettings.CHOPCHOP_AUTO_TURNOFF_KEY)
        val chopchopTimoutPref = findPreference<SeekBarPreference>(DozeSettings.CHOPCHOP_AUTO_TURNOFF_TIMEOUT_KEY)
        val chopchopDNDPref = findPreference<SwitchPreference>(DozeSettings.CHOPCHOP_AUTO_TURNOFF_IGNORE_DND_KEY)

        val autoturnoffEnabled = chopchopAutoTurnoffPref!!.isChecked

        chopchopAutoTurnoffPref.isEnabled = enabled as Boolean

        chopchopTimoutPref!!.isEnabled = enabled && autoturnoffEnabled
        chopchopDNDPref!!.isEnabled = enabled && autoturnoffEnabled

        true
    }

    private val chopchopAutoturnoffChangeListener = Preference.OnPreferenceChangeListener { _, enabled ->
        val chopchopPref = findPreference<SwitchPreference>(DozeSettings.CHOPCHOP_KEY)
        val chopchopTimoutPref = findPreference<SeekBarPreference>(DozeSettings.CHOPCHOP_AUTO_TURNOFF_TIMEOUT_KEY)
        val chopchopDNDPref = findPreference<SwitchPreference>(DozeSettings.CHOPCHOP_AUTO_TURNOFF_IGNORE_DND_KEY)

        val chopchopEnabled = chopchopPref!!.isChecked

        chopchopTimoutPref!!.isEnabled = enabled as Boolean && chopchopEnabled
        chopchopDNDPref!!.isEnabled = enabled && chopchopEnabled

        true
    }

}
