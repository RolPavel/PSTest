package com.rolstudio.pstest.api

import com.rolstudio.pstest.models.SearchResponse
import com.rolstudio.pstest.models.UserRepositoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("search/users")
    suspend fun searchUsers(@Query("q") name: String): Response<SearchResponse>

    @GET("search/repositories")
    suspend fun searchRepositories(@Query("q") name: String): Response<UserRepositoryResponse>

}