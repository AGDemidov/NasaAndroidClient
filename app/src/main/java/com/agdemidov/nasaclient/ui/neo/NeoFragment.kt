package com.agdemidov.nasaclient.ui.neo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.agdemidov.nasaclient.databinding.FragmentNeoBinding
import com.agdemidov.nasaclient.services.neo.NeoService
import com.agdemidov.nasaclient.ui.BaseFragment
import com.agdemidov.nasaclient.ui.ViewModelsFactory
import com.agdemidov.nasaclient.utils.Extensions.showView

class NeoFragment : BaseFragment<NeoViewModel>() {

    private var _binding: FragmentNeoBinding? = null
    private val binding
        get() = _binding!!

    override val viewModel: NeoViewModel by viewModels {
        ViewModelsFactory(NeoService.instance)
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
            viewModel.fetchTodayNeoList()
            isFirstLaunch = false
        }
    }

    var swipeRefresh: SwipeRefreshLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNeoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        swipeRefresh = binding.swipeRefresh
        swipeRefresh?.setProgressViewOffset(false, 0, 150)
        swipeRefresh?.setOnRefreshListener {
            viewModel.fetchTodayNeoList()
        }
        collectSingleSharedFlow(viewModel.progressIndicator) {
            swipeRefresh?.isRefreshing = it
        }

        val neoPagerView: ViewPager2 = binding.neoPager
        neoPagerView.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    swipeRefresh?.isEnabled = when (state) {
                        ViewPager2.SCROLL_STATE_IDLE -> true
                        else -> false
                    }
                }
            })
        val neoAdapter = NeoPagesAdapter(
            requireActivity(),
            object : NeoPagesAdapter.OnTodayPageCreated {
                override fun onTodayPageCreated(index: Int) {
                    neoPagerView.currentItem = index
                }
            })
        neoPagerView.adapter = neoAdapter
        neoPagerView.offscreenPageLimit = 7

        val neoNoDataText: TextView = binding.neoNoData

        collectSingleSharedFlow(viewModel.neoData) { it ->
            val isTodayNeoListEmpty = it.neoModelsMap.isEmpty()

            neoPagerView.showView(!isTodayNeoListEmpty)
            neoNoDataText.showView(isTodayNeoListEmpty)
            if (!isTodayNeoListEmpty) {
                neoAdapter.submitData(it.neoModelsMap)
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