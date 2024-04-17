package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.SystemProperties
import android.os.UEventObserver
import android.preference.PreferenceManager
import android.util.Log
import java.io.File

object Lenovo: EntryStartup {
    val dtPanel = "/sys/devices/virtual/touch/tp_dev/gesture_on" //ZUI13 or older
    val gestureEnable = "/proc/gesture_control" //ZUI14 or newer
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            LenovoSettings.dt2w -> {
                //TODO: We need to check that the screen is on at this time
                //This won't have any effect if done with screen off
                val b = sp.getBoolean(key, false)
                val value = if(b) "1" else "0"
                writeToFileNofail(dtPanel, value)
                try {
                    val v = if(b) arrayOf<Byte>(-128, 0) else arrayOf<Byte>(0, 0)
                    File(gestureEnable).outputStream().use { it.write(v.toByteArray()) }
                } catch(t: Throwable) { Log.d("PHH", "OP: Couldn't write to gesture enable")}
                Log.d("PHH", "Setting dtPanel to $value")
            }
        }
    }

    fun writeToFileNofail(path: String, content: String) {
        try {
            File(path).printWriter().use { it.println(content) }
        } catch(t: Throwable) {
            Log.d("PHH", "Failed writing to $path", t)
        }
    }

    override fun startup(ctxt: Context) {
        if(!LenovoSettings.enabled()) return

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        //Refresh parameters on boot
        spListener.onSharedPreferenceChanged(sp, LenovoSettings.dt2w)
    }
}
