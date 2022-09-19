package com.bignerdranch.android.navigationcomponenttabs.model.accounts.entities

import com.bignerdranch.android.navigationcomponenttabs.model.boxes.entities.BoxAndSettings

data class AccountFullData(
    val account: Account,
    val boxesAndSettings: List<BoxAndSettings>
)