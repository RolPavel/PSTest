package com.rolstudio.pstest.api

import com.rolstudio.pstest.models.RepositoryContentItems
import com.rolstudio.pstest.models.SearchResponse
import com.rolstudio.pstest.models.UserRepositoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchAPI {

    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") name: String,
        @Header("Authorization") auth: String
    ): Response<SearchResponse>

    @GET("search/repositories")
    suspend fun getRepositories(
        @Query("q") login: String,
        @Header("Authorization") auth: String
    ): Response<UserRepositoryResponse>

    @GET("/repos/{owner}/{repo}/contents/{path}")
    suspend fun getRepositoryContents(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String?
    ): Response<List<RepositoryContentItems>>
}