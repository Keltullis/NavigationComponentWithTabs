package com.bignerdranch.android.navigationcomponenttabs.model.boxes.room

import androidx.room.*
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities.AccountBoxSettingDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities.BoxAndSettingsTuple
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities.BoxDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.views.SettingDbView
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.views.SettingWithEntitiesTuples
import kotlinx.coroutines.flow.Flow

@Dao
interface BoxesDao {

    // Возвращаем DataBaseView
    //@Query("SELECT * FROM settings_view WHERE account_id = :accountId")
    //fun getBoxesAndSettings(accountId:Long):Flow<List<SettingDbView>>

    @Transaction
    @Query("SELECT * FROM settings_view WHERE account_id = :accountId")
    fun getBoxesAndSettings(accountId:Long):Flow<List<SettingWithEntitiesTuples>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setActiveFlagForBox(accountBoxSettingDbEntity: AccountBoxSettingDbEntity)

}