package com.bignerdranch.android.navigationcomponenttabs.model.accounts.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.room.entities.AccountDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.room.entities.AccountSignInTuple
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.room.entities.AccountUpdateUsernameTuple
import kotlinx.coroutines.flow.Flow

// Интерфейс,в котором хранятся описание всех операций с таблицей,реализацию сделает сам Room
@Dao
interface AccountsDao {
    @Query("SELECT id, password FROM accounts WHERE email = :email")
    suspend fun findByEmail(email:String): AccountSignInTuple?

    @Update(entity = AccountDbEntity::class)
    suspend fun updateUsername(account: AccountUpdateUsernameTuple)

    @Insert(entity = AccountDbEntity::class)
    suspend fun createAccount(accountDbEntity: AccountDbEntity)

    @Query("SELECT * FROM accounts WHERE id = :accountId")
    fun getById(accountId:Long):Flow<AccountDbEntity?>
}