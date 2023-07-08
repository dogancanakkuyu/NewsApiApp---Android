package com.example.newsapiapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.newsapiapp.R
import com.example.newsapiapp.ServiceResponseState
import com.example.newsapiapp.adapter.CategoriesAdapter
import com.example.newsapiapp.adapter.NewsAdapter
import com.example.newsapiapp.databinding.FragmentMainBinding
import com.example.newsapiapp.utils.Constant
import com.example.newsapiapp.viewmodel.AuthenticationViewModel
import com.example.newsapiapp.viewmodel.ServiceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment(), CategoriesAdapter.ClickListener {

    private lateinit var binding: FragmentMainBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter


    private val serviceViewModel: ServiceViewModel by viewModels()
    private val authenticationViewModel: AuthenticationViewModel by viewModels()
    private var selectedItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoriesAdapter = CategoriesAdapter(Constant.categories, this)
        binding.recyclerViewCategories.adapter = categoriesAdapter

        sendNewRequest(0)
        println(authenticationViewModel.getUser().uid)
        logOut()
    }

    fun logOut() {
        binding.logOutBtn.setOnClickListener {
            authenticationViewModel.logOutFromAccount()
            findNavController().navigate(R.id.action_main_fragment_to_loginFragment)
        }
    }


    private fun sendNewRequest(item: Int) {
        lifecycleScope.launch {
            serviceViewModel.getArticles(Constant.categories[item])
            serviceViewModel.serviceFlow.collect {
                when (it) {
                    is ServiceResponseState.Loading -> println("news loading...")
                    is ServiceResponseState.Error -> println(it.error)
                    is ServiceResponseState.Success -> {
                        newsAdapter = it.body.articles?.let {
                            NewsAdapter(it)
                        }!!
                        binding.recyclerView.adapter = newsAdapter
                    }

                    else -> ServiceResponseState.Empty
                }

            }
        }
    }

    override fun onClickListener(item: Int) {
        sendNewRequest(item)
    }

    override fun getSelectedItem(): Int {
        return selectedItem
    }

    override fun setSelectedItem(item: Int) {
        selectedItem = item
    }

}