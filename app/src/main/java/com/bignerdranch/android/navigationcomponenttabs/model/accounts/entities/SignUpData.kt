package com.bignerdranch.android.navigationcomponenttabs.model.accounts.entities

import com.bignerdranch.android.navigationcomponenttabs.model.EmptyFieldException
import com.bignerdranch.android.navigationcomponenttabs.model.Field
import com.bignerdranch.android.navigationcomponenttabs.model.PasswordMismatchException

/**
 * Fields that should be provided during creating a new account.
 */
data class SignUpData(
    val username: String,
    val email: String,
    val password: String,
    val repeatPassword: String
) {
    fun validate() {
        if (email.isBlank()) throw EmptyFieldException(Field.Email)
        if (username.isBlank()) throw EmptyFieldException(Field.Username)
        if (password.isBlank()) throw EmptyFieldException(Field.Password)
        if (password != repeatPassword) throw PasswordMismatchException()
    }
}