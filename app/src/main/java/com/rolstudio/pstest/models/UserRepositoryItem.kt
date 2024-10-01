package com.rolstudio.pstest.models

data class UserRepositoryItem(
    val id: Int,
    val full_name: String,
    val description: String?,
    val forks_count: Int
)