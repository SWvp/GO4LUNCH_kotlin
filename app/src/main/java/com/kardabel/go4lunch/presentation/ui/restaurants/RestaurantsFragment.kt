package com.kardabel.go4lunch.presentation.ui.restaurants

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kardabel.go4lunch.databinding.RecyclerviewRestaurantsBinding
import com.kardabel.go4lunch.di.ViewModelFactory
import com.kardabel.go4lunch.presentation.ui.detailsview.RestaurantDetailsActivity

class RestaurantsFragment: Fragment() {

    private var _binding: RecyclerviewRestaurantsBinding? = null
    private val binding get() = _binding!!

    //private val viewModel by viewModels<RestaurantsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RecyclerviewRestaurantsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // INIT RESTAURANT VIEWMODEL
        val viewModelFactory = ViewModelFactory.instance
        val restaurantsViewModel = ViewModelProvider(this, viewModelFactory!!)[RestaurantsViewModel::class.java]

        val adapter = RestaurantsAdapter { restaurantId ->
            startActivity(Intent(RestaurantDetailsActivity.navigate(requireContext(), restaurantId)))
        }

        binding.restaurantListRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        binding.restaurantListRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.restaurantListRecyclerView.adapter = adapter

        restaurantsViewModel.getRestaurantsWrapperViewStateMediatorLiveData.observe(viewLifecycleOwner){ restaurantsWrapperViewState ->
            adapter.submitList(restaurantsWrapperViewState.itemRestaurant)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}