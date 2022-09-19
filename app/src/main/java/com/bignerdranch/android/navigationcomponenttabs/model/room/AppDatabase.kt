package com.bignerdranch.android.navigationcomponenttabs.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.room.AccountsDao
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.room.entities.AccountDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.BoxesDao
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities.AccountBoxSettingDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities.BoxDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.views.SettingDbView

// Версия и массив таблиц
@Database(version = 1 , entities = [AccountDbEntity::class,BoxDbEntity::class,AccountBoxSettingDbEntity::class], views = [SettingDbView::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAccountsDao():AccountsDao

    abstract fun getBoxesDao():BoxesDao

}