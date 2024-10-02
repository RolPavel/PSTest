package com.rolstudio.pstest.util

import com.rolstudio.pstest.models.SearchItem
import com.rolstudio.pstest.models.UserRepositoryItem

sealed class CombinedItem {
    data class User(val item: SearchItem) : CombinedItem()
    data class Repository(val item: UserRepositoryItem) : CombinedItem()
}