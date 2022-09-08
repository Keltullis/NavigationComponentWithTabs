package com.bignerdranch.android.navigationcomponenttabs.model.boxes.room

import com.bignerdranch.android.navigationcomponenttabs.model.AuthException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.AccountsRepository
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.BoxesRepository
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.entities.Box
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.entities.BoxAndSettings
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.room.entities.AccountBoxSettingDbEntity
import com.bignerdranch.android.navigationcomponenttabs.model.room.wrapSQLiteException

class RoomBoxesRepository(
    private val accountsRepository: AccountsRepository,
    private val boxesDao: BoxesDao,
    private val ioDispatcher: CoroutineDispatcher
) : BoxesRepository {

    override suspend fun getBoxesAndSettings(onlyActive: Boolean): Flow<List<BoxAndSettings>> {
        return accountsRepository.getAccount()
            .flatMapLatest { account ->
                if (account == null) return@flatMapLatest flowOf(emptyList())
                queryBoxesAndSettings(account.id)
            }
            .mapLatest { boxAndSettings ->
                if (onlyActive) {
                    boxAndSettings.filter { it.isActive }
                } else {
                    boxAndSettings
                }
            }
    }

    override suspend fun activateBox(box: Box) = wrapSQLiteException(ioDispatcher) {
        setActiveFlagForBox(box, true)
    }

    override suspend fun deactivateBox(box: Box) = wrapSQLiteException(ioDispatcher) {
        setActiveFlagForBox(box, false)
    }

    private fun queryBoxesAndSettings(accountId: Long): Flow<List<BoxAndSettings>> {
        return boxesDao.getBoxesAndSettings(accountId).map { entities ->
            entities.map {
                val boxEntity = it.key
                val settingsEntity = it.value
                BoxAndSettings(
                    boxEntity.toBox(),
                    settingsEntity == null || settingsEntity.isActive
                )
            }
        }
    }

    private suspend fun setActiveFlagForBox(box: Box, isActive: Boolean) {
        val account = accountsRepository.getAccount().first() ?:throw AuthException()
        boxesDao.setActiveFlagForBox(
            AccountBoxSettingDbEntity(
                accountId = account.id,
                boxId = box.id,
                isActive = isActive
            )
        )
    }
}