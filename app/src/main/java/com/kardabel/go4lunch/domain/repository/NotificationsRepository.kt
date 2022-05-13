package com.kardabel.go4lunch.domain.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class NotificationsRepository(
    context: Context
) {

    companion object {
        const val REMINDER_REQUEST = "my reminder"
        const val SHARED_PREFS = "shared reminder"
    }

    private var sharedPref: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)

    fun isNotificationEnabledLiveData(): LiveData<Boolean> {

        val mutableLiveData = MutableLiveData<Boolean>()

        mutableLiveData.value =
            sharedPref.getBoolean(REMINDER_REQUEST, false)

        sharedPref.registerOnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences, key: String ->
            if (key == REMINDER_REQUEST) {
                mutableLiveData.value = sharedPreferences.getBoolean(key, false)
            }
        }
        return mutableLiveData
    }

    fun switchNotification() {

        if (!sharedPref.getBoolean(NotificationsRepository.REMINDER_REQUEST, false)) {
            sharedPref
                .edit()
                .putBoolean(REMINDER_REQUEST, true)
                .apply()
        } else {
            sharedPref
                .edit()
                .putBoolean(REMINDER_REQUEST, false)
                .apply()
        }
    }
}