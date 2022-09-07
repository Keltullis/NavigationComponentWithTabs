package com.bignerdranch.android.navigationcomponenttabs.model.settings

import android.content.Context
import com.bignerdranch.android.navigationcomponenttabs.model.settings.AppSettings.Companion.NO_ACCOUNT_ID

class SharedPreferencesAppSettings(
    appContext: Context
) : AppSettings {

    // Получили преференцы
    private val sharedPreferences = appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    // Кладём пару ключ/значение
    override fun setCurrentAccountId(accountId: Long) {
        sharedPreferences.edit()
            .putLong(PREF_CURRENT_ACCOUNT_ID, accountId)
            .apply()
    }
    // Кладём значение по ключу и даём значение по умолчанию
    override fun getCurrentAccountId(): Long = sharedPreferences.getLong(PREF_CURRENT_ACCOUNT_ID, NO_ACCOUNT_ID)

    companion object {
        private const val PREF_CURRENT_ACCOUNT_ID = "currentAccountId"
    }
}