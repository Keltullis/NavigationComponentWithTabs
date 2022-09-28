package com.bignerdranch.android.navigationcomponenttabs.model.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.room.AccountsDao
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.room.entities.AccountDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.BoxesDao
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities.AccountBoxSettingDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities.BoxDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.views.SettingDbView

// Версия и массив таблиц
// ниже приведён пример автомиграции
// рум не умеет определять какие колонки были удалены или переименованы,поэтому нужен spec
@Database(version = 3 ,
    entities = [AccountDbEntity::class,BoxDbEntity::class,AccountBoxSettingDbEntity::class],
    views = [SettingDbView::class],
    autoMigrations = [AutoMigration(from = 1, to = 2, spec = AutoMigrationSpec1To2::class)]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAccountsDao():AccountsDao

    abstract fun getBoxesDao():BoxesDao

}