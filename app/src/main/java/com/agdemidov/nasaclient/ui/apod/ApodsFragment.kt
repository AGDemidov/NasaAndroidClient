package com.agdemidov.nasaclient.ui.apod

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.agdemidov.nasaclient.R
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

    private var _binding: FragmentApodBinding? = null
    private val binding
        get() = _binding!!

    var subtitle: TextView? = null

    override val viewModel: ApodsViewModel by activityViewModels {
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
            Lifecycle.Event.ON_START -> {
                if (subtitle == null) {
                    subtitle = activity?.findViewById(R.id.horizontal_subtitle)
                }
                subtitle?.showView(true)
            }
            Lifecycle.Event.ON_STOP -> subtitle?.showView(false)
            else -> {}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApodBinding.inflate(inflater, container, false)

        val metrics = requireActivity().resources.displayMetrics
        val itemsPerRow = when ((metrics.widthPixels / metrics.density).toInt()) {
            in 0 until 600 -> 2
            in 600 until 1200 -> 3
            else -> 4
        }

        val swipeRefresh = binding.swipeRefresh
        swipeRefresh.setProgressViewOffset(false, PROGRESS_START_POS, PROGRESS_END_POS)
        swipeRefresh.setOnRefreshListener {
            viewModel.fetchApodsList()
        }

        collectStateFlow(viewModel.topProgressIndicator) {
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
            val firstPosition = layoutManager.findFirstVisibleItemPosition()
            val lastPosition = layoutManager.findLastVisibleItemPosition()
            val apodsListSize = viewModel.apodItems.value.size
            viewModel.setScrollPosition((firstPosition + lastPosition) / 2)
            changeTitle(firstPosition + 1, lastPosition + 1, apodsListSize)
            tryToFetchNextApodsPage(lastPosition, apodsListSize)
        }

        if (viewModel.storedScrollPosition > 0) {
            apodsRecyclerView.smoothScrollToPosition(viewModel.storedScrollPosition)
        }

        val apodNoDataText: TextView = binding.apodsNoData
        collectStateFlow(viewModel.apodItems) {
            val isApodsListEmpty = it.isEmpty()
            apodNoDataText.showView(isApodsListEmpty)
            apodsRecyclerView.showView(!isApodsListEmpty)
            if (!isApodsListEmpty) {
                apodsAdapter.submitSourceList(it)
                changeTitle(
                    layoutManager.findFirstVisibleItemPosition() + 1,
                    layoutManager.findLastVisibleItemPosition() + 1,
                    viewModel.apodItems.value.size
                )
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeTitle(firstPosition: Int, lastPosition: Int, loadedListSize: Int) {
        subtitle?.text = activity?.resources?.getString(
            R.string.apod_title_info_part,
            firstPosition,
            lastPosition,
            loadedListSize
        ) ?: "N/A"
    }

    private fun tryToFetchNextApodsPage(position: Int, listSize: Int) {
        if (NetworkManager.isNetworkAvailable && position >= (listSize - PAGE_SIZE / 8 - 1)) {
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
