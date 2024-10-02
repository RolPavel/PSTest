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
                job?.cancel()
                job = MainScope().launch {
                    delay(SEARCH_USERS_DELAY)
                    binding.searchEdit.isEnabled =
                        false
                    mainViewModel.searchUsersAndRepositories(query)
                }
            } else {
                combinedAdapter.submitList(emptyList())
            }
        }
        mainViewModel.searchUsers.observe(viewLifecycleOwner) { response ->
            handleUserSearchResponse(response)
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
                binding.progressBar.visibility = View.GONE
                binding.searchEdit.isEnabled = true
            }
            is Resource.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.searchEdit.isEnabled = false
            }
        }
    }

    private fun openRepositoryContent(owner: String, repo: String) {
        val bundle = Bundle().apply {
            putString("owner", owner)
            putString("repo", repo)
        }
        findNavController().navigate(
            R.id.action_searchFragment2_to_repositoryContentFragment,
            bundle
        )
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
                    val fullNameParts = item.item.full_name.split("/")
                    if (fullNameParts.size == 2) {
                        val owner = fullNameParts[0].trim()
                        val repo = fullNameParts[1].trim()
                        openRepositoryContent(owner, repo)
                    }
                }
            }
        }
    }
}
