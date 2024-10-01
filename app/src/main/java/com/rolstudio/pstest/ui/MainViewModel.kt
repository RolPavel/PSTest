package com.rolstudio.pstest.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rolstudio.pstest.models.SearchItem
import com.rolstudio.pstest.models.UserRepositoryItem
import com.rolstudio.pstest.repository.SearchRepository
import com.rolstudio.pstest.util.Resource
import kotlinx.coroutines.launch

class MainViewModel(app: Application, private val searchRepository: SearchRepository) : AndroidViewModel(app) {
    private val _searchUsers = MutableLiveData<Resource<Map<SearchItem, List<UserRepositoryItem>>>>()
    val searchUsers: LiveData<Resource<Map<SearchItem, List<UserRepositoryItem>>>> get() = _searchUsers

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
}
