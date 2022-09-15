package com.agdemidov.nasaclient.ui.apod

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.agdemidov.nasaclient.databinding.FragmentApodBinding
import com.agdemidov.nasaclient.ui.BaseFragment
import com.agdemidov.nasaclient.ui.ViewModelsFactory
import com.agdemidov.nasaclient.utils.Extensions.showView
import com.agdemidov.nasaclient.utils.NetworkManager
import com.agdemidov.nasaclient.utils.PAGE_SIZE
import com.agdemidov.nasaclient.utils.PROGRESS_END_POS
import com.agdemidov.nasaclient.utils.PROGRESS_START_POS

class ApodsFragment : BaseFragment<ApodsViewModel>() {

    private val TAG = ApodsFragment::class.simpleName
    private val SCROLL_POSITION_TAG = "${ApodsFragment::class.simpleName}_SCROLL_POSITION"

    private var _binding: FragmentApodBinding? = null
    private val binding
        get() = _binding!!

    private var isPageLoading = false
    private var storedScrollPosition = -1;

    override val viewModel: ApodsViewModel by viewModels {
        ViewModelsFactory.provideApodViewModel(requireActivity())
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
            Lifecycle.Event.ON_CREATE -> {
                arguments?.let {
                    val position = it.getInt(SCROLL_POSITION_TAG, -1)
                    if (position > 0) {
                        storedScrollPosition = position
                    }
                    Log.i(TAG, "${event.name} restored scroll position $storedScrollPosition")
                }
            }
            Lifecycle.Event.ON_PAUSE -> {
                Log.i(TAG, "${event.name} stored scroll position $storedScrollPosition")
                val args = Bundle()
                args.putInt(SCROLL_POSITION_TAG, storedScrollPosition)
                storedScrollPosition = -1
                arguments = args
            }
            else -> {}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApodBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val metrics = requireActivity().resources.displayMetrics
        val itemsPerRow = when ((metrics.widthPixels / metrics.density).toInt()) {
            in 0 until 600 -> 2
            in 600 until 1200 -> 3
            else -> 4
        }

        collectSharedFlow(viewModel.isFirstLaunch) {
            if (it) {
                Log.i(TAG, "Load cached data from DB or try to fetch new")
                viewModel.fetchCachedApodsOnStart()
            }
        }

        val swipeRefresh = binding.swipeRefresh
        swipeRefresh.setProgressViewOffset(false, PROGRESS_START_POS, PROGRESS_END_POS)
        swipeRefresh.setOnRefreshListener {
            isPageLoading = true
            viewModel.fetchApodsList()
        }
        collectSharedFlow(viewModel.progressIndicator) {
            swipeRefresh.isRefreshing = it
        }

        val apodsRecyclerView = binding.apodsRecyclerView
        val layoutManager = GridLayoutManager(requireActivity(), itemsPerRow)

        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) itemsPerRow else 1
            }
        }

        apodsRecyclerView.layoutManager = layoutManager
        val apodsAdapter = ApodsAdapter(metrics.widthPixels / itemsPerRow)
        apodsRecyclerView.adapter = apodsAdapter

        apodsRecyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
            val position = layoutManager.findLastVisibleItemPosition()
            storedScrollPosition = layoutManager.findFirstVisibleItemPosition()
            collectSharedFlow(viewModel.apodItems) {
                fetchNextApodsPageOnScroll(position, it.size)
            }
        }
        if (storedScrollPosition > 0) {
            apodsRecyclerView.smoothScrollToPosition(storedScrollPosition)
        }

        val apodNoDataText: TextView = binding.apodsNoData
        collectSharedFlow(viewModel.apodItems) {
            val isApodsListEmpty = it.isEmpty()
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

    private fun fetchNextApodsPageOnScroll(position: Int, listSize: Int) {
        if (position >= (listSize - PAGE_SIZE / 2 - 1) &&
            !isPageLoading && NetworkManager.isNetworkAvailable
        ) {
            Log.i(TAG, "reached position $position below the scroller, fetch next page")
            isPageLoading = true
            viewModel.fetchApodsList(false)
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
