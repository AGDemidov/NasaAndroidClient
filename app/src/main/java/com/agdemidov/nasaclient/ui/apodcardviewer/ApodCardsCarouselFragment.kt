package com.agdemidov.nasaclient.ui.apodcardviewer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.agdemidov.nasaclient.R
import com.agdemidov.nasaclient.databinding.FragmentApodCardViewerBinding
import com.agdemidov.nasaclient.ui.BaseFragment
import com.agdemidov.nasaclient.ui.ViewModelsFactory
import com.agdemidov.nasaclient.ui.apod.ApodsViewModel
import com.agdemidov.nasaclient.utils.Extensions.showView
import com.agdemidov.nasaclient.utils.NetworkManager

class ApodCardsCarouselFragment : BaseFragment<ApodsViewModel>() {

    private var _binding: FragmentApodCardViewerBinding? = null
    private val binding
        get() = _binding!!

    override val viewModel: ApodsViewModel by activityViewModels {
        ViewModelsFactory.provideApodViewModel(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApodCardViewerBinding.inflate(inflater, container, false)

        val args: ApodCardsCarouselFragmentArgs by navArgs()
        viewModel.setScrollPosition(args.currentIndex)

        val apodCardsCarouselAdapter = ApodCardsCarouselAdapter()
        binding.apodCardsPager.adapter = apodCardsCarouselAdapter

        binding.apodCardsPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewModel.setScrollPosition(position)
                    val apodsListSize = viewModel.apodItems.value.size
                    changeTitle(position + 1, apodsListSize)
                    tryToFetchNextApodsPage(position, apodsListSize)
                }
            })

        collectStateFlow(viewModel.apodItems) {
            apodCardsCarouselAdapter.submitData(it)
            binding.apodsNoData.showView(it.isEmpty())
            binding.apodCardsPager.showView(it.isNotEmpty())
            if (it.isNotEmpty()) {
                changeTitle(
                    viewModel.storedScrollPosition + 1,
                    viewModel.apodItems.value.size
                )
            }
        }

        Handler(Looper.getMainLooper()).post {
            binding.apodCardsPager.setCurrentItem(args.currentIndex, true)
        }
        return binding.root
    }

    private fun changeTitle(position: Int, loadedListSize: Int) {
        (activity as AppCompatActivity).supportActionBar?.title = activity?.resources?.getString(
            R.string.apod_card_viewer_title,
            position, loadedListSize
        ) ?: "N/A"
    }

    private fun tryToFetchNextApodsPage(position: Int, listSize: Int) {
        if (NetworkManager.isNetworkAvailable && position >= (listSize - 5)) {
            viewModel.fetchApodsList(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
