package com.rolstudio.pstest.repository

import com.rolstudio.pstest.api.RetrofitInstance
import com.rolstudio.pstest.models.SearchItem
import com.rolstudio.pstest.models.SearchResponse
import com.rolstudio.pstest.models.UserRepositoryItem
import com.rolstudio.pstest.models.UserRepositoryResponse
import com.rolstudio.pstest.util.Constants.Companion.TOKEN
import retrofit2.Response

class SearchRepository {

    private suspend fun searchUsers(name: String): Response<SearchResponse> =
        RetrofitInstance.api.searchUsers(name, TOKEN)

    private suspend fun getRepositories(username: String): Response<UserRepositoryResponse> =
        RetrofitInstance.api.getRepositories(username, TOKEN)

    suspend fun searchUsersWithRepositories(name: String): Result<Map<SearchItem, List<UserRepositoryItem>>> {
        val userResponse = searchUsers(name)
        if (userResponse.isSuccessful) {
            val users = userResponse.body()?.items ?: emptyList()
            val result = mutableMapOf<SearchItem, List<UserRepositoryItem>>()

            for (user in users) {
                val repoResponse = getRepositories(user.login)
                if (repoResponse.isSuccessful) {
                    result[user] = repoResponse.body()?.items ?: emptyList()
                } else {
                    result[user] = emptyList() // Если ошибка, возвращаем пустой список репозиториев
                }
            }

            return Result.success(result)
        } else {
            return Result.failure(Exception("Failed to fetch users"))
        }
    }
}
