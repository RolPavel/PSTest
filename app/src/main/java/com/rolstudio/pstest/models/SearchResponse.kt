package com.rolstudio.pstest.models

data class SearchResponse(
    val total_count: Int,
    val incomplete_results: Boolean,
    var items: List<SearchItem>
)