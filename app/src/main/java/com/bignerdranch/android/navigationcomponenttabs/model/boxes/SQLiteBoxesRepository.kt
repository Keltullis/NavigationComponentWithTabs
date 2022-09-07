package com.bignerdranch.android.navigationcomponenttabs.model.boxes

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import androidx.core.content.contentValuesOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import com.bignerdranch.android.navigationcomponenttabs.model.AuthException
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.AccountsRepository
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.entities.Box
import com.bignerdranch.android.navigationcomponenttabs.model.sqlite.AppSQLiteContract
import com.bignerdranch.android.navigationcomponenttabs.model.sqlite.AppSQLiteContract.AccountsBoxesSettingsTable
import com.bignerdranch.android.navigationcomponenttabs.model.sqlite.AppSQLiteContract.BoxesTable
import com.bignerdranch.android.navigationcomponenttabs.model.sqlite.wrapSQLiteException

class SQLiteBoxesRepository(
    private val db: SQLiteDatabase,
    private val accountsRepository: AccountsRepository,
    private val ioDispatcher: CoroutineDispatcher
) : BoxesRepository {

    private val reconstructFlow = MutableSharedFlow<Unit>(replay = 1).also { it.tryEmit(Unit) }

    override suspend fun getBoxes(onlyActive: Boolean): Flow<List<Box>> {
        return combine(accountsRepository.getAccount(), reconstructFlow) { account, _ ->
            queryBoxes(onlyActive, account?.id)
        }
            .flowOn(ioDispatcher)
    }

    override suspend fun activateBox(box: Box) = wrapSQLiteException(ioDispatcher) {
        setActiveFlagForBox(box, true)
    }

    override suspend fun deactivateBox(box: Box) = wrapSQLiteException(ioDispatcher) {
        setActiveFlagForBox(box, false)
    }

    private suspend fun setActiveFlagForBox(box: Box, isActive: Boolean) {
        val account = accountsRepository.getAccount().first() ?: throw AuthException()
        saveActiveFlag(account.id, box.id, isActive)
        reconstructFlow.tryEmit(Unit)
    }

    private fun queryBoxes(onlyActive: Boolean, accountId: Long?): List<Box> {
        if (accountId == null) return emptyList()

        val cursor = queryBoxes(onlyActive, accountId)
        return cursor.use {
            val list = mutableListOf<Box>()
            while (cursor.moveToNext()) {
                list.add(parseBox(cursor))
            }
            return@use list
        }
    }

    private fun parseBox(cursor: Cursor): Box {
        return Box(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(BoxesTable.COLUMN_ID)),
            colorName = cursor.getString(cursor.getColumnIndexOrThrow(BoxesTable.COLUMN_COLOR_NAME)),
            colorValue = Color.parseColor(cursor.getString(cursor.getColumnIndexOrThrow(BoxesTable.COLUMN_COLOR_VALUE)))
        )
    }

    private fun saveActiveFlag(accountId: Long, boxId: Long, isActive: Boolean) {
        // Вставляем новую строку в таблицу настроек,у такого то пользователя,такой то ящик,включен или же нет
        // если такая же строка с теми же параметрами уже есть,то мы заменяем её на новую
        db.insertWithOnConflict(
            AccountsBoxesSettingsTable.TABLE_NAME,
            null,
            contentValuesOf(
                AccountsBoxesSettingsTable.COLUMN_BOX_ID to boxId,
                AccountsBoxesSettingsTable.COLUMN_ACCOUNT_ID to accountId,
                AccountsBoxesSettingsTable.COLUMN_IS_ACTIVE to isActive
            ),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    // Выбрать все стобцы таблицы boxes и столбец is_active из таблицы настроек
    // из таблицы boxes
    // объединяем 2 таблицы в одну
    // где id коробки в обоих таблицах одинаковое
    // и id пользователя из таблицы настроек = 1
    // и где активность коробки ровна null (не заполненно)
    // или 1
    private fun queryBoxes(onlyActive: Boolean, accountId: Long): Cursor {
        return if (onlyActive) {
            val sql = "SELECT ${BoxesTable.TABLE_NAME}.* " +
                    "FROM ${BoxesTable.TABLE_NAME} " +
                    "LEFT JOIN ${AccountsBoxesSettingsTable.TABLE_NAME} " +
                    "ON ${AccountsBoxesSettingsTable.COLUMN_BOX_ID} = ${BoxesTable.COLUMN_ID} " +
                    "AND ${AccountsBoxesSettingsTable.COLUMN_ACCOUNT_ID} = ? " +
                    "WHERE ${AccountsBoxesSettingsTable.COLUMN_IS_ACTIVE} IS NULL " +
                    "OR ${AccountsBoxesSettingsTable.COLUMN_IS_ACTIVE} = 1"

            db.rawQuery(sql, arrayOf(accountId.toString()))
        } else {
            val sql = "SELECT * FROM ${BoxesTable.TABLE_NAME}"
            db.rawQuery(sql,null)
            //return db.query(BoxesTable.TABLE_NAME, arrayOf(BoxesTable.COLUMN_ID,BoxesTable.COLUMN_COLOR_NAME,BoxesTable.COLUMN_COLOR_VALUE),null,null,null,null,null)
        }
    }
}