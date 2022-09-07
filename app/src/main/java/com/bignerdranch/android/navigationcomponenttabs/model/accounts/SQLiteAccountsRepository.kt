package com.bignerdranch.android.navigationcomponenttabs.model.accounts

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.provider.Settings
import androidx.core.content.contentValuesOf
import com.bignerdranch.android.navigationcomponenttabs.model.AccountAlreadyExistsException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import com.bignerdranch.android.navigationcomponenttabs.model.AuthException
import com.bignerdranch.android.navigationcomponenttabs.model.EmptyFieldException
import com.bignerdranch.android.navigationcomponenttabs.model.Field
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.entities.Account
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.entities.SignUpData
import com.bignerdranch.android.navigationcomponenttabs.model.settings.AppSettings
import com.bignerdranch.android.navigationcomponenttabs.model.sqlite.AppSQLiteContract
import com.bignerdranch.android.navigationcomponenttabs.model.sqlite.AppSQLiteContract.AccountsTable
import com.bignerdranch.android.navigationcomponenttabs.model.sqlite.wrapSQLiteException
import com.bignerdranch.android.navigationcomponenttabs.utils.AsyncLoader

class SQLiteAccountsRepository(
    private val db: SQLiteDatabase,
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
            .map { accountId ->
                getAccountById(accountId.value)
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

    private fun findAccountIdByEmailAndPassword(email: String, password: String): Long {
        // 1 - имя таблица
        // 2 - массив колонок
        // 3 - условие выборки(знак вопроса означает что сюда подставят значение из 4 параметра
        // 4 - массив значений которые нужно подставить
        val cursor = db.query(
            AccountsTable.TABLE_NAME,
            arrayOf(AccountsTable.COLUMN_ID,AccountsTable.COLUMN_PASSWORD),
            "${AccountsTable.COLUMN_EMAIL} = ?",
            arrayOf(email),
            null,null,null
        )

        // Курсор нужно не забыть закрыть,поэтому используем use
        return cursor.use {
            if (cursor.count == 0) throw AuthException()
            cursor.moveToFirst()
            // методу getString нужно передать индекс колонки которая есть в ответе
            val accountPassword = cursor.getString(cursor.getColumnIndexOrThrow(AccountsTable.COLUMN_PASSWORD))
            if(accountPassword != password) throw AuthException()

            cursor.getLong(cursor.getColumnIndexOrThrow(AccountsTable.COLUMN_ID))
        }
    }

    // 1 параметр это название,2 нулл,3 это значения которые мы хотим вставить в новую строку
    // тут набор пар,ключ значение,ключ это колонка в строке,а значение это значение
    private fun createAccount(signUpData: SignUpData) {
        try {
            db.insertOrThrow(
                AccountsTable.TABLE_NAME,
                null,
                contentValuesOf(
                    AccountsTable.COLUMN_EMAIL to signUpData.email,
                    AccountsTable.COLUMN_USERNAME to signUpData.username,
                    AccountsTable.COLUMN_PASSWORD to signUpData.password,
                    AccountsTable.COLUMN_CREATED_AT to System.currentTimeMillis()
                )
            )
        } catch(e:SQLiteConstraintException){
            val appException = AccountAlreadyExistsException()
            appException.initCause(e)
            throw appException
        }
    }

    private fun getAccountById(accountId: Long): Account? {
        if (accountId == AppSettings.NO_ACCOUNT_ID) return null

        // Название,нужные колонки,условие выборки(where),с чем выборка,нулы
        val cursor = db.query(
            AccountsTable.TABLE_NAME,
            arrayOf(AccountsTable.COLUMN_ID,AccountsTable.COLUMN_EMAIL,AccountsTable.COLUMN_USERNAME,AccountsTable.COLUMN_CREATED_AT),
            "${AccountsTable.COLUMN_ID} = ?",
            arrayOf(accountId.toString()),
            null,null,null
        )

        return cursor.use {
            if(cursor.count == 0) return@use null
            cursor.moveToFirst()
            Account(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(AccountsTable.COLUMN_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(AccountsTable.COLUMN_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(AccountsTable.COLUMN_EMAIL)),
                createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(AccountsTable.COLUMN_CREATED_AT))
            )
        }
    }

    // Если id совпадёт,то в колонке username появится новый ник newUsername
    private fun updateUsernameForAccountId(accountId: Long, newUsername: String) {
        db.update(
            AccountsTable.TABLE_NAME,
            contentValuesOf(
                AccountsTable.COLUMN_USERNAME to newUsername
            ),
            "${AccountsTable.COLUMN_ID} = ?",
            arrayOf(accountId.toString())
        )
    }

    private class AccountId(val value: Long)
}