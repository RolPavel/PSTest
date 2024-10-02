package com.rolstudio.pstest.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rolstudio.pstest.models.RepositoryContentItems
import com.rolstudio.pstest.models.SearchItem
import com.rolstudio.pstest.models.UserRepositoryItem
import com.rolstudio.pstest.repository.SearchRepository
import com.rolstudio.pstest.util.Resource
import kotlinx.coroutines.launch

class MainViewModel(app: Application, private val searchRepository: SearchRepository) : AndroidViewModel(app) {

    private val _searchUsers = MutableLiveData<Resource<Map<SearchItem, List<UserRepositoryItem>>>>()
    val searchUsers: LiveData<Resource<Map<SearchItem, List<UserRepositoryItem>>>> get() = _searchUsers

    private val _contents = MutableLiveData<List<RepositoryContentItems>>()
    val contents: LiveData<List<RepositoryContentItems>> = _contents

    fun searchUsersAndRepositories(query: String) {
        viewModelScope.launch {
            _searchUsers.postValue(Resource.Loading())

            val result = searchRepository.searchUsersWithRepositories(query)
            if (result.isSuccess) {
                _searchUsers.postValue(Resource.Success(result.getOrDefault(mapOf())))
            } else {
                _searchUsers.postValue(Resource.Error(result.exceptionOrNull()?.message ?: "Unknown Error"))
            }
        }
    }

    fun loadContents(owner: String, repo: String, path: String? = null) {
        viewModelScope.launch {
            try {
                val response = searchRepository.getRepositoryContents(owner, repo, path)
                Log.d("LoadContents", "Response: ${response.code()} ${response.message()}")
                if (response.isSuccessful) {
                    _contents.postValue(response.body())
                    Log.d("LoadContents", "Fetched contents: $contents")
                } else {
                    Log.e("LoadContents", "Error: ${response.code()} ${response.message()}")
                    _contents.postValue(emptyList())
                }
            } catch (e: Exception) {
                Log.e("LoadContents", "Exception: ${e.message}")
                _contents.postValue(emptyList())
            }
        }
    }
}
