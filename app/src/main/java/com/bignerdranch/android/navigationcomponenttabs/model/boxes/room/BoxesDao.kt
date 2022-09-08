package com.bignerdranch.android.navigationcomponenttabs.model.boxes.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities.AccountBoxSettingDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities.BoxDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoxesDao {

    @Query("SELECT * FROM boxes LEFT JOIN accounts_boxes_settings ON boxes.id = accounts_boxes_settings.box_id AND accounts_boxes_settings.account_id = :accountId")
    fun getBoxesAndSettings(accountId:Long):Flow<Map<BoxDbEntity,AccountBoxSettingDbEntity?>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setActiveFlagForBox(accountBoxSettingDbEntity: AccountBoxSettingDbEntity)

}