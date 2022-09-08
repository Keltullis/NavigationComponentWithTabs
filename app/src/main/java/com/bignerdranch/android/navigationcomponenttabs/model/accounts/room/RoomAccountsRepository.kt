package com.bignerdranch.android.navigationcomponenttabs.model.accounts.room

import android.database.sqlite.SQLiteConstraintException
import com.bignerdranch.android.navigationcomponenttabs.model.AccountAlreadyExistsException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import com.bignerdranch.android.navigationcomponenttabs.model.AuthException
import com.bignerdranch.android.navigationcomponenttabs.model.EmptyFieldException
import com.bignerdranch.android.navigationcomponenttabs.model.Field
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.AccountsRepository
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.entities.Account
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.entities.SignUpData
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.room.entities.AccountDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.room.entities.AccountUpdateUsernameTuple
import com.bignerdranch.android.navigationcomponenttabs.model.room.wrapSQLiteException
import com.bignerdranch.android.navigationcomponenttabs.model.settings.AppSettings
import com.bignerdranch.android.navigationcomponenttabs.utils.AsyncLoader

class RoomAccountsRepository(
    private val accountsDao: AccountsDao,
    private val appSettings: AppSettings,
    private val ioDispatcher: CoroutineDispatcher
) : AccountsRepository {

    private val currentAccountIdFlow = AsyncLoader {
        MutableStateFlow(AccountId(appSettings.getCurrentAccountId()))
    }

    override suspend fun isSignedIn(): Boolean {
        delay(2000)
        return appSettings.getCurrentAccountId() != AppSettings.NO_ACCOUNT_ID
    }

    override suspend fun signIn(email: String, password: String) = wrapSQLiteException(ioDispatcher) {
        if (email.isBlank()) throw EmptyFieldException(Field.Email)
        if (password.isBlank()) throw EmptyFieldException(Field.Password)

        delay(1000)

        val accountId = findAccountIdByEmailAndPassword(email, password)
        appSettings.setCurrentAccountId(accountId)
        currentAccountIdFlow.get().value = AccountId(accountId)

        return@wrapSQLiteException
    }

    override suspend fun signUp(signUpData: SignUpData) = wrapSQLiteException(ioDispatcher) {
        signUpData.validate()
        delay(1000)
        createAccount(signUpData)
    }

    override suspend fun logout() {
        appSettings.setCurrentAccountId(AppSettings.NO_ACCOUNT_ID)
        currentAccountIdFlow.get().value = AccountId(AppSettings.NO_ACCOUNT_ID)
    }

    override suspend fun getAccount(): Flow<Account?> {
        return currentAccountIdFlow.get()
            .flatMapLatest { accountId ->
                if (accountId.value == AppSettings.NO_ACCOUNT_ID) {
                    flowOf(null)
                } else {
                    getAccountById(accountId.value)
                }
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun updateAccountUsername(newUsername: String) = wrapSQLiteException(ioDispatcher) {
        if (newUsername.isBlank()) throw EmptyFieldException(Field.Username)
        delay(1000)
        val accountId = appSettings.getCurrentAccountId()
        if (accountId == AppSettings.NO_ACCOUNT_ID) throw AuthException()

        updateUsernameForAccountId(accountId, newUsername)

        currentAccountIdFlow.get().value = AccountId(accountId)
        return@wrapSQLiteException
    }

    private suspend fun findAccountIdByEmailAndPassword(email: String, password: String): Long {
        val tuple = accountsDao.findByEmail(email) ?: throw AuthException()
        if(tuple.password != password) throw AuthException()
        return tuple.id
    }

    private suspend fun createAccount(signUpData: SignUpData) {
        try {
            val entity = AccountDbEntity.fromSignUpData(signUpData)
            accountsDao.createAccount(entity)
        } catch (e:SQLiteConstraintException){
            val appException = AccountAlreadyExistsException()
            appException.initCause(e)
            throw appException
        }
    }

    private fun getAccountById(accountId: Long): Flow<Account?> {
        return accountsDao.getById(accountId).map { accountDbEntity -> accountDbEntity?.toAccount() }
    }

    private suspend fun updateUsernameForAccountId(accountId: Long, newUsername: String) {
        accountsDao.updateUsername(
            AccountUpdateUsernameTuple(accountId,newUsername)
        )
    }

    private class AccountId(val value: Long)

}