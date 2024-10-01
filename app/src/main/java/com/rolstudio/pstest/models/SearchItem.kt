package com.rolstudio.pstest.models

import java.io.Serializable

data class SearchItem(
    val login: String,
    val id: Int,
    val avatar_url: String,
    val html_url: String,
    val score: Int
) : Serializable