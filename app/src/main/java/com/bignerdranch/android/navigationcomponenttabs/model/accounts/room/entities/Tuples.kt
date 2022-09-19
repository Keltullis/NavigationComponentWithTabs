package com.bignerdranch.android.navigationcomponenttabs.model.accounts.room.entities

import androidx.room.*
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities.AccountBoxSettingDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities.BoxDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.views.SettingDbView


data class AccountSignInTuple(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "password") val password: String
)

data class AccountUpdateUsernameTuple(
    @ColumnInfo(name = "id") @PrimaryKey val id: Long,
    @ColumnInfo(name = "username") val username: String
)

data class AccountAndEditedBoxesTuple(
    @Embedded val accountDbEntity: AccountDbEntity,

    // 1 id из BoxDbEntity,а 2 id из AccountDbEntity
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = AccountBoxSettingDbEntity::class,
            parentColumn = "account_id",
            entityColumn = "box_id"
        )
    )
     val boxes:List<BoxDbEntity>
)

data class AccountAndSettingsTuple(
    @Embedded val accountDbEntity: AccountDbEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "account_id",
        entity = SettingDbView::class
    )
    val settings:List<SettingAndBoxTuple>
)

data class SettingAndBoxTuple(
    @Embedded val accountBoxSettingsDbEntity: SettingDbView,
    @Relation(
        parentColumn = "box_id",
        entityColumn = "id")
    val boxDbEntity: BoxDbEntity
)