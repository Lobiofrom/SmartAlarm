package com.example.alarm.ringtonePicker

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract

class RingtonePickerContract : ActivityResultContract<Unit, Uri?>() {

    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL)
            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Выберите рингтон")
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode == RESULT_OK && intent != null) {
            return intent.parcelable(EXTRA_RINGTONE_PICKED_URI)
        }
        return null
    }

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }
}
