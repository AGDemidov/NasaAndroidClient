package com.agdemidov.nasaclient.ui.apod

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.recyclerview.widget.LinearLayoutManager
import com.agdemidov.nasaclient.databinding.FragmentApodBinding
import com.agdemidov.nasaclient.services.apod.ApodService
import com.agdemidov.nasaclient.ui.BaseFragment
import com.agdemidov.nasaclient.ui.ViewModelsFactory
import com.agdemidov.nasaclient.utils.Extensions.showView

class ApodsFragment : BaseFragment<ApodsViewModel>() {

    private var _binding: FragmentApodBinding? = null
    private val binding
        get() = _binding!!

    override val viewModel: ApodsViewModel by viewModels {
        ViewModelsFactory(ApodService.instance)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycle.addObserver(lifecycleEventObserver)
    }

    override fun onDetach() {
        super.onDetach()
        lifecycle.removeObserver(lifecycleEventObserver)
    }

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> onFragmentStartEvent()
            else -> {}
        }
    }

    private fun onFragmentStartEvent() {
        if (isFirstLaunch) {
            viewModel.fetchApodsList()
            isFirstLaunch = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApodBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val swipeRefresh = binding.swipeRefresh
        swipeRefresh.setProgressViewOffset(false, 0, 150)
        swipeRefresh.setOnRefreshListener {
            viewModel.fetchApodsList()
        }
        collectSingleSharedFlow(viewModel.progressIndicator) {
            swipeRefresh.isRefreshing = it
        }

        val apodsRecyclerView = binding.apodsItemsList
        apodsRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        val apodsAdapter = ApodsAdapter()
        apodsRecyclerView.adapter = apodsAdapter

        val apodNoDataText: TextView = binding.apodsNoData
        viewModel.apodItems.observe(viewLifecycleOwner) {
            val isApodsListEmpty = it.isNullOrEmpty()
            apodNoDataText.showView(isApodsListEmpty)
            apodsRecyclerView.showView(!isApodsListEmpty)
            if (!isApodsListEmpty) {
                apodsAdapter.submitList(it)
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var isFirstLaunch = true
    }
}