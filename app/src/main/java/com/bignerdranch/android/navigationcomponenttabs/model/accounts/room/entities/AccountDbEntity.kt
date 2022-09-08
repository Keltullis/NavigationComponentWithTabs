package com.bignerdranch.android.navigationcomponenttabs.model.accounts.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.entities.Account
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.entities.SignUpData

@Entity(
    tableName = "accounts",
    indices = [Index("email" , unique = true)]
)
data class AccountDbEntity(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "email", collate = ColumnInfo.NOCASE) val email: String,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "created_at") val createdAt: Long
) {
    // Из-за того что поле createdAt отличается от поля created_at в бд,мы указали аннотацию @ColumnInfo, которая в итоге переименует наше поле
    // collate = ColumnInfo.NOCASE делает поле нечувствительным к регистру
    // аннотация @Entity указывает что этот класс,это сущность,т.е таблица в бд


    // Преобразовывам сущность в класс аккаунт
    fun toAccount(): Account = Account(
        id = id,
        email = email,
        username = username,
        createdAt = createdAt
    )

    // Преобразовываем данные пользователя в строчку в таблице
    companion object {
        fun fromSignUpData(signUpData: SignUpData): AccountDbEntity = AccountDbEntity(
            // Id генерируется автоматом,поэтому передаём 0
            id = 0,
            email = signUpData.email,
            username = signUpData.username,
            password = signUpData.password,
            createdAt = System.currentTimeMillis()
        )
    }

}