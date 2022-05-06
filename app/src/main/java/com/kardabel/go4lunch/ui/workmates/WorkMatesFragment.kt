package com.kardabel.go4lunch.ui.workmates

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
import com.kardabel.go4lunch.databinding.RecyclerviewWorkmatesBinding
import com.kardabel.go4lunch.di.ViewModelFactory
import com.kardabel.go4lunch.ui.chat.ChatActivity
import com.kardabel.go4lunch.ui.detailsview.RestaurantDetailsActivity

class WorkMatesFragment : Fragment() {

    private var _binding: RecyclerviewWorkmatesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = RecyclerviewWorkmatesBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // INIT RESTAURANT VIEWMODEL
        val workmatesViewModelFactory = ViewModelFactory.getInstance()
        val workMatesViewModel =
            ViewModelProvider(this, workmatesViewModelFactory)[WorkMatesViewModel::class.java]

        // CONFIGURE RECYCLERVIEW
        val adapter = WorkMatesRecyclerViewAdapter { workmate ->
            startActivity(Intent(ChatActivity.navigate(
                requireContext(),
                workmate.workmateId!!,
                workmate.workmateName!!,
                workmate.workmatePhoto!!)))

        }

        binding.workmateRecyclerView.adapter = adapter
        binding.workmateRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        binding.workmateRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        workMatesViewModel.workMatesViewStateMediatorLiveData.observe(viewLifecycleOwner) { workmatesList ->
            adapter.submitList(workmatesList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}