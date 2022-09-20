package com.agdemidov.nasaclient.ui.neo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.agdemidov.nasaclient.R
import com.agdemidov.nasaclient.databinding.FragmentNeoBinding
import com.agdemidov.nasaclient.ui.BaseFragment
import com.agdemidov.nasaclient.ui.ViewModelsFactory
import com.agdemidov.nasaclient.utils.Extensions.showView
import com.agdemidov.nasaclient.utils.PROGRESS_END_POS
import com.agdemidov.nasaclient.utils.PROGRESS_START_POS

class NeoFragment : BaseFragment<NeoViewModel>() {

    private val TAG = NeoFragment::class.simpleName

    private var _binding: FragmentNeoBinding? = null
    private val binding
        get() = _binding!!

    override val viewModel: NeoViewModel by viewModels {
        ViewModelsFactory.provideNeoViewModel()
    }

    var swipeRefresh: SwipeRefreshLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNeoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.isFirstLaunch.observe(viewLifecycleOwner) {
            if (it) {
                Log.i(TAG, "Load cached neo data from preferences or try to fetch new")
                viewModel.fetchCachedTodayNeoListOnStart()
            }
        }

        swipeRefresh = binding.swipeRefresh
        swipeRefresh?.setProgressViewOffset(false, PROGRESS_START_POS, PROGRESS_END_POS)
        swipeRefresh?.setOnRefreshListener {
            viewModel.fetchTodayNeoList()
        }
        collectSharedFlow(viewModel.progressIndicator) {
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

        viewModel.neoData.observe(viewLifecycleOwner) {
            neoNoDataText.showView(!neoPagerView.isVisible)
            if (!it.neoModelsMap.isEmpty()) {
                neoNoDataText.text = getText(R.string.loading)
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        neoNoDataText.showView(false)
                        neoPagerView.showView(true)
                        neoAdapter.submitData(it.neoModelsMap)
                    }, 300
                )
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
