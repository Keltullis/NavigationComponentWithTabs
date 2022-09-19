package com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class SettingsTuple(
    @ColumnInfo(name = "is_active") val isActive:Boolean
)

// Объединили данные из двух таблиц в одну через Embedded
data class BoxAndSettingsTuple(
    @Embedded val boxDbEntity: BoxDbEntity,
    @Embedded val settingsDbEntity: AccountBoxSettingDbEntity?
)