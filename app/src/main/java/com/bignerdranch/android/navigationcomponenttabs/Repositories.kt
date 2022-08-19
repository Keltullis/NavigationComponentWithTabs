package com.bignerdranch.android.navigationcomponenttabs

import com.bignerdranch.android.navigationcomponenttabs.model.accounts.AccountsRepository
import com.bignerdranch.android.navigationcomponenttabs.model.accounts.InMemoryAccountsRepository
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.BoxesRepository
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.InMemoryBoxesRepository

object Repositories {

    val accountsRepository: AccountsRepository = InMemoryAccountsRepository()

    val boxesRepository: BoxesRepository = InMemoryBoxesRepository(accountsRepository)

}