package com.bignerdranch.android.navigationcomponenttabs


import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.AccountsRepository
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.room.RoomAccountsRepository
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.BoxesRepository
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.RoomBoxesRepository
import com.bignerdranch.android.navigationcomponenttabs.model.room.AppDatabase
import com.bignerdranch.android.navigationcomponenttabs.model.room.MIGRATION_2_3
import com.bignerdranch.android.navigationcomponenttabs.model.settings.AppSettings
import com.bignerdranch.android.navigationcomponenttabs.model.settings.SharedPreferencesAppSettings
import com.bignerdranch.android.navigationcomponenttabs.utils.security.DefaultSecurityUtilsImpl
import com.bignerdranch.android.navigationcomponenttabs.utils.security.SecurityUtils

object Repositories {

    private lateinit var applicationContext: Context

    // -- stuffs

    val securityUtils: SecurityUtils by lazy { DefaultSecurityUtilsImpl() }

    // Прописываем РУЧНЫЕ миграции
    private val database: AppDatabase by lazy<AppDatabase> {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database.db")
            .addMigrations(MIGRATION_2_3)
            .createFromAsset("initial_database.db")
            .build()
    }

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val appSettings: AppSettings by lazy {
        SharedPreferencesAppSettings(applicationContext)
    }

    // --- repositories

    val accountsRepository: AccountsRepository by lazy {
        RoomAccountsRepository(database.getAccountsDao(), appSettings, securityUtils, ioDispatcher)
    }

    val boxesRepository: BoxesRepository by lazy {
        RoomBoxesRepository(accountsRepository, database.getBoxesDao(), ioDispatcher)
    }

    /**
     * Call this method in all application components that may be created at app startup/restoring
     * (e.g. in onCreate of activities and services)
     */
    fun init(context: Context) {
        applicationContext = context
    }
}