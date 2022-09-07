package com.bignerdranch.android.navigationcomponenttabs.model.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// класс помошник,помогает создавать,инициализировать и обновлять БД
class AppSQLiteHelper(
    private val applicationContext: Context
):SQLiteOpenHelper(applicationContext,"database.db",null,1) {
    // Начальная инициализация бд,открываем скрипт из ассетов и разбиваем
    // можно без ассетов,просто вбить комманды вручную
    override fun onCreate(db: SQLiteDatabase) {
        // Получаем sql script
        // открываем ассет,преобразуем его в инпут стрим,используем юз что бы закрыть стрим автоматом и в итоге получаем строку
        val sql = applicationContext.assets.open("db_init.sql").bufferedReader().use {
            it.readText()
        }
        // делим команды по ;,убираем пустые строки и запускаем выполнение каждой команды
        sql.split(";")
            .filter { it.isNotBlank() }
            .forEach{
                db.execSQL(it)
            }
    }

    // Миграция бд,т.е обновление бд до новой версии
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

}