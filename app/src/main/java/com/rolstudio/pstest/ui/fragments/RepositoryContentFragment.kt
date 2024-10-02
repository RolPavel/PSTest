package com.rolstudio.pstest.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rolstudio.pstest.R
import com.rolstudio.pstest.adapters.RepositoryContentAdapter
import com.rolstudio.pstest.databinding.FragmentRepositoryContentBinding
import com.rolstudio.pstest.models.RepositoryContentItems
import com.rolstudio.pstest.ui.MainActivity
import com.rolstudio.pstest.ui.MainViewModel
import com.rolstudio.pstest.util.Resource

class RepositoryContentFragment : Fragment(R.layout.fragment_repository_content) {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: FragmentRepositoryContentBinding
    private lateinit var adapter: RepositoryContentAdapter
    private lateinit var owner: String
    private lateinit var repo: String
    private val folderStack = mutableListOf<RepositoryContentItems>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRepositoryContentBinding.bind(view)
        mainViewModel = (activity as MainActivity).mainViewModel

        owner = arguments?.getString("owner") ?: return
        repo = arguments?.getString("repo") ?: return

        setupRecyclerView()
        loadContents(owner, repo)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (folderStack.isNotEmpty()) {
                        folderStack.removeAt(folderStack.size - 1)
                        val previousItem = folderStack.lastOrNull()
                        if (previousItem != null) {
                            loadContents(
                                owner,
                                repo,
                                previousItem.path
                            )
                        } else {
                            findNavController().popBackStack()
                        }
                    } else {
                        findNavController().popBackStack()
                    }
                }
            })
    }

    private fun setupRecyclerView() {
        adapter = RepositoryContentAdapter {
            handleItemClick(it)
        }
        binding.recyclerRep.adapter = adapter
        binding.recyclerRep.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun loadContents(owner: String, repo: String, path: String? = "") {
        binding.progressBar.visibility = View.VISIBLE
        mainViewModel.loadContents(owner, repo, path ?: "")
        mainViewModel.contents.observe(viewLifecycleOwner) { contents ->
            binding.progressBar.visibility = View.GONE
            binding.errorMessage.visibility = View.GONE
            binding.retryButton.visibility = View.GONE
            when (contents) {
                is Resource.Success<*> -> {
                    val sortedContents = contents.sortedWith(
                        compareBy<RepositoryContentItems> {
                            when (it.type) {
                                "dir" -> 0
                                "file" -> 1
                                else -> 2
                            }
                        }.thenBy {
                            it.name
                        }
                    )
                    if (sortedContents.isNotEmpty()) {
                        adapter.submitList(sortedContents)
                    } else {
                        Toast.makeText(context, "Содержимое пусто", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error<*> -> {
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.retryButton.visibility = View.VISIBLE
                    binding.errorMessage.text = contents.message ?: "Неизвестная ошибка"
                }

                is Resource.Loading<*> -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
        binding.retryButton.setOnClickListener {
            loadContents(owner, repo, path)
        }
    }

    private fun handleItemClick(contentItem: RepositoryContentItems) {
        when (contentItem.type) {
            "dir" -> {
                folderStack.add(contentItem)
                loadContents(owner, repo, contentItem.path)
            }
            "file" -> {
                openFileInWebView(contentItem.html_url)
            }
        }
    }

    private fun openFileInWebView(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
