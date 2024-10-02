package com.rolstudio.pstest.models

import java.io.Serializable

data class UserRepositoryItem(
    val id: Int,
    val full_name: String,
    val name: String,
    val description: String?,
    val forks_count: Int,
    val owner: RepositoryOwner
) : Serializable