package com.agdemidov.nasaclient.ui.apod

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
//import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.agdemidov.nasaclient.databinding.FragmentApodBinding
import com.agdemidov.nasaclient.services.apod.ApodService
import com.agdemidov.nasaclient.ui.BaseFragment
import com.agdemidov.nasaclient.ui.ViewModelsFactory
import com.agdemidov.nasaclient.utils.Constants.PAGE_SIZE
import com.agdemidov.nasaclient.utils.Extensions.showView

class ApodsFragment : BaseFragment<ApodsViewModel>() {

    private var _binding: FragmentApodBinding? = null
    private val binding
        get() = _binding!!

    private var isPageLoading = false

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
            isPageLoading = true
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
            isPageLoading = true
            viewModel.fetchApodsList()
        }
        collectSingleSharedFlow(viewModel.progressIndicator) {
            swipeRefresh.isRefreshing = it
        }

        val apodsRecyclerView = binding.apodsRecyclerView
        val layoutManager = LinearLayoutManager(requireActivity())
        apodsRecyclerView.layoutManager = layoutManager
        val apodsAdapter = ApodsAdapter(requireContext().resources.displayMetrics)
        apodsRecyclerView.adapter = apodsAdapter

        apodsRecyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
            val position = layoutManager.findLastVisibleItemPosition()
            viewModel.apodItems.value?.let {
                fetchNextApodsPage(position, apodsAdapter.itemsPerRow, it.size)
            }
        }

        val apodNoDataText: TextView = binding.apodsNoData
        viewModel.apodItems.observe(viewLifecycleOwner) {
            val isApodsListEmpty = it.isNullOrEmpty()
            apodNoDataText.showView(isApodsListEmpty)
            apodsRecyclerView.showView(!isApodsListEmpty)
            if (!isApodsListEmpty) {
                apodsAdapter.submitSourceList(it)
            }
            isPageLoading = false
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

    private fun fetchNextApodsPage(position: Int, itemsPerRow: Int, listSize: Int) {
        if (position >= (listSize / itemsPerRow - PAGE_SIZE / itemsPerRow - 1) && !isPageLoading) {
            Log.e("Scrolling", "reached position $position")
            viewModel.fetchApodsList(false)
            isPageLoading = true
        }
    }

    private fun measureView(view: View, result: (Int, Int) -> Unit) {
        val viewTreeObserver = view.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    requireView().viewTreeObserver.removeOnGlobalLayoutListener(this)
                    result(view.width, view.height)
                }
            })
        }
    }
}