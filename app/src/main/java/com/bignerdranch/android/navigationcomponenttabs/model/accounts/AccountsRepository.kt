package com.bignerdranch.android.navigationcomponenttabs.model.accounts

import kotlinx.coroutines.flow.Flow
import com.bignerdranch.android.navigationcomponenttabs.model.*
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.entities.Account
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.entities.AccountFullData
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.entities.SignUpData

/**
 * Repository with account-related actions, e.g. sign-in, sign-up, edit account etc.
 */
interface AccountsRepository {

    /**
     * Whether user is signed-in or not.
     */
    suspend fun isSignedIn(): Boolean

    /**
     * Try to sign-in with the email and password.
     * @throws [EmptyFieldException]
     * @throws [AuthException]
     * @throws [StorageException]
     */
    suspend fun signIn(email: String, password: CharArray)

    /**
     * Create a new account.
     * @throws [EmptyFieldException]
     * @throws [PasswordMismatchException]
     * @throws [AccountAlreadyExistsException]
     * @throws [StorageException]
     */
    suspend fun signUp(signUpData: SignUpData)

    /**
     * Sign-out from the app.
     */
    suspend fun logout()

    /**
     * Get the account info of the current signed-in user.
     */
    suspend fun getAccount(): Flow<Account?>

    /**
     * Change the username of the current signed-in user.
     * @throws [EmptyFieldException]
     * @throws [AuthException]
     * @throws [StorageException]
     */
    suspend fun updateAccountUsername(newUsername: String)

    /**
     * Get all accounts with their boxes and settings.
     * Only admin user can do this. Otherwise [AuthException] is thrown.
     */
    suspend fun getAllData(): Flow<List<AccountFullData>>

}