package com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities

import androidx.room.*
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.room.entities.AccountDbEntity

@Entity(
    tableName = "accounts_boxes_settings",
    foreignKeys = [
        ForeignKey(
            entity = AccountDbEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BoxDbEntity::class,
            parentColumns = ["id"],
            childColumns = ["box_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["account_id", "box_id"],
    indices = [Index("box_id")]
)

// С помощью аннотации Embedded,мы вынесли третье поле в отдельный класс(это учебный пример,тут это по сути не нужно)
data class AccountBoxSettingDbEntity(
    @ColumnInfo(name = "account_id") val accountId:Long,
    @ColumnInfo(name = "box_id") val boxId:Long,
    @Embedded val settings:SettingsTuple
)
// Параметры внешних ключей
// 1 из какой таблицы
// 2 родитель с первичным ключем
// 3 наследник с внешним
// 4 что делать при удалении например аккаунта
// 5 что делать при обновлении например аккаунта