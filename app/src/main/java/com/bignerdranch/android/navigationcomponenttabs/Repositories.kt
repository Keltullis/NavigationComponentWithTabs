package com.bignerdranch.android.navigationcomponenttabs


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.AccountsRepository
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.SQLiteAccountsRepository
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.BoxesRepository
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.SQLiteBoxesRepository
import com.bignerdranch.android.navigationcomponenttabs.model.settings.AppSettings
import com.bignerdranch.android.navigationcomponenttabs.model.settings.SharedPreferencesAppSettings
import com.bignerdranch.android.navigationcomponenttabs.model.sqlite.AppSQLiteHelper


object Repositories {

    private lateinit var applicationContext: Context

    // -- stuffs
    // Здесь хранится БД
    private val database: SQLiteDatabase by lazy<SQLiteDatabase> {
        AppSQLiteHelper(applicationContext).writableDatabase
    }

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val appSettings: AppSettings by lazy {
        SharedPreferencesAppSettings(applicationContext)
    }

    // --- repositories

    val accountsRepository: AccountsRepository by lazy {
        SQLiteAccountsRepository(database, appSettings, ioDispatcher)
    }

    val boxesRepository: BoxesRepository by lazy {
        SQLiteBoxesRepository(database, accountsRepository, ioDispatcher)
    }

    fun init(context: Context) {
        applicationContext = context
    }
}