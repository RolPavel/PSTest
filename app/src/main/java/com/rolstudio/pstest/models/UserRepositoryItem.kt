package com.rolstudio.pstest.models

data class UserRepositoryItem(
    val id: Int,
    val name: String,
    val forks_count: Int,
    val description: String?,
    val html_url: String
)