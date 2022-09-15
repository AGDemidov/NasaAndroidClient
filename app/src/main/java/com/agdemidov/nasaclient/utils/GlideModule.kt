package com.agdemidov.nasaclient.utils

import android.content.Context
import com.agdemidov.nasaclient.httpclient.RetrofitClient
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions


@GlideModule
class NasaClientGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.setDefaultRequestOptions {
            RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        }
    }
}
