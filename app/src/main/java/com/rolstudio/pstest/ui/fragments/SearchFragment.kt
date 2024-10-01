package com.rolstudio.pstest.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rolstudio.pstest.R
import com.rolstudio.pstest.adapters.CombinedAdapter
import com.rolstudio.pstest.databinding.FragmentSearchBinding
import com.rolstudio.pstest.models.SearchItem
import com.rolstudio.pstest.models.UserRepositoryItem
import com.rolstudio.pstest.ui.MainActivity
import com.rolstudio.pstest.ui.MainViewModel
import com.rolstudio.pstest.util.CombinedItem
import com.rolstudio.pstest.util.Constants.Companion.SEARCH_USERS_DELAY
import com.rolstudio.pstest.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var combinedAdapter: CombinedAdapter
    private lateinit var binding: FragmentSearchBinding
    private var job: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)
        mainViewModel = (activity as MainActivity).mainViewModel

        setupRecyclerView()

        binding.searchButton.setOnClickListener {
            val query = binding.searchEdit.query.toString().trim()
            if (query.length > 3) {
                // Отменяем предыдущий запрос, если он ещё выполняется
                job?.cancel()

                // Запускаем новый запрос в корутине
                job = MainScope().launch {
                    delay(SEARCH_USERS_DELAY)
                    binding.searchEdit.isEnabled = false // Отключаем поле поиска во время выполнения
                    mainViewModel.searchUsersAndRepositories(query)
                }
            } else {
                combinedAdapter.submitList(emptyList())
            }
        }

        // Наблюдаем за результатами поиска
        mainViewModel.searchUsers.observe(viewLifecycleOwner) { response ->
            handleUserSearchResponse(response)
        }
    }


    private fun setupRecyclerView() {
        combinedAdapter = CombinedAdapter()
        binding.recyclerSearch.apply {
            adapter = combinedAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        combinedAdapter.setOnItemClickListener { item ->
            when (item) {
                is CombinedItem.User -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.item.html_url))
                    startActivity(intent)
                }
                is CombinedItem.Repository -> {
                    val bundle = Bundle().apply {
                        putString("repoName", item.item.full_name)
                    }
                    findNavController().navigate(R.id.action_searchFragment2_to_repListFragment, bundle)
                }
            }
        }
    }

    private fun handleUserSearchResponse(response: Resource<Map<SearchItem, List<UserRepositoryItem>>>) {
        when (response) {
            is Resource.Success -> {
                response.data?.let { userRepoMap ->
                    val combinedItems = mutableListOf<CombinedItem>()
                    userRepoMap.forEach { (user, repos) ->
                        combinedItems.add(CombinedItem.User(user))
                        for (repo in repos) {
                            combinedItems.add(CombinedItem.Repository(repo))
                        }
                    }
                    combinedAdapter.submitList(combinedItems)
                    binding.progressBar.visibility = View.GONE // Скрыть прогресс
                    binding.searchEdit.isEnabled = true // Включить поле поиска
                }
            }
            is Resource.Error -> {
                binding.progressBar.visibility = View.GONE // Скрыть прогресс в случае ошибки
                binding.searchEdit.isEnabled = true // Включить поле поиска
                // Здесь можно вывести сообщение об ошибке
            }
            is Resource.Loading -> {
                binding.progressBar.visibility = View.VISIBLE // Показать прогресс
                binding.searchEdit.isEnabled = false // Отключить поле ввода
            }
        }
    }
}
