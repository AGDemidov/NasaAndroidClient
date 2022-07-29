package com.agdemidov.nasaclient.utils

import android.view.View

object Extensions {
    fun View.showView(doVisible: Boolean, keepPlaceHolder: Boolean = false) =
        if (doVisible) {
            visibility = View.VISIBLE
        } else if (keepPlaceHolder) {
            visibility = View.INVISIBLE
        } else {
            visibility = View.GONE
        }
}