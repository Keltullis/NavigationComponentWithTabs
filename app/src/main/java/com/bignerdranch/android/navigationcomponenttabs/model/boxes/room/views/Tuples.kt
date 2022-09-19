package com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.views

import androidx.room.Embedded
import androidx.room.Relation
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.room.entities.AccountDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities.BoxDbEntity

// Используем Relation
// это позволяет ссылаться на другие сущности,entityColumn это идентификатор сущности которую описывают здесь
// а parentColumn из нашей сущности(берём что-то из одной таблицы и что-то из другой)
// в результате получили сущность,которая содержит настройки + инфу об аккаунте и о ящике
// @Relation объясняет библиотеке Room как получить данное свойство
data class SettingWithEntitiesTuples(
    //DataBaseView
    @Embedded val settingsDbEntity: SettingDbView,

    @Relation(parentColumn = "account_id", entityColumn = "id")
    val accountDbEntity: AccountDbEntity,

    @Relation(parentColumn = "box_id", entityColumn = "id")
    val boxDbEntity: BoxDbEntity
)