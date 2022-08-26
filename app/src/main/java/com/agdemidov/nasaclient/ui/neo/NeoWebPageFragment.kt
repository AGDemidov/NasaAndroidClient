package com.agdemidov.nasaclient.ui.neo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.agdemidov.nasaclient.databinding.FragmentNeoWebPageBinding

class NeoWebPageFragment : Fragment() {

    private var _binding: FragmentNeoWebPageBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNeoWebPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView = view as WebView

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }

        arguments?.let {
            val args = NeoWebPageFragmentArgs.fromBundle(it)
            webView.loadUrl(
                args.neoPageUrl
            )
        }

        val settings = webView.settings
        settings.loadWithOverviewMode = true;
        settings.useWideViewPort = true;
        settings.javaScriptEnabled = true;

        settings.setSupportZoom(true)
        settings.builtInZoomControls = true

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}