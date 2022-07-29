package com.agdemidov.nasaclient.ui

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.agdemidov.nasaclient.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.SharedFlow

abstract class BaseFragment<T : BaseViewModel> : Fragment() {
    abstract val viewModel: T

    private var toast: Toast? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectSingleSharedFlow(viewModel.alertEvent) {
            AlertDialog.Builder(view.context)
                .setTitle(it.first)
                .setMessage(it.second)
                .setPositiveButton(R.string.positive_button_text, null)
                .create()
                .show()
        }

        collectSingleSharedFlow(viewModel.snackBarEvent) {
            Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
        }

        collectSingleSharedFlow(viewModel.toastEvent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (toast == null) {
                    toast = Toast.makeText(context, it, Toast.LENGTH_LONG)
                    toast?.addCallback(object : Toast.Callback() {
                        override fun onToastHidden() {
                            super.onToastHidden()
                            toast = null
                        }
                    })
                    toast?.show()
                }
            } else {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun <T> collectSingleSharedFlow(src: SharedFlow<T>, callback: (type: T) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            src.collect {
                callback(it)
            }
        }
    }
}
