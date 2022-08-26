package com.agdemidov.nasaclient.ui.apod

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import com.agdemidov.nasaclient.R
import com.agdemidov.nasaclient.databinding.ApodCardPlaceHolderBinding
import com.agdemidov.nasaclient.databinding.ApodFirstItemViewHolderBinding
import com.agdemidov.nasaclient.databinding.ApodItemViewHolderBinding
import com.agdemidov.nasaclient.models.ApodModel
import com.bumptech.glide.Glide

class ApodsAdapter(private val displayMetrics: DisplayMetrics) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val itemsPerRow
        get() = _itemsPerRow

    private val _itemsPerRow by lazy {
        when ((displayMetrics.widthPixels / displayMetrics.density).toInt()) {
            in 0 until 600 -> 2
            in 600 until 1200 -> 3
            else -> 4
        }
    }

    private val imageSideSize = displayMetrics.widthPixels / _itemsPerRow
    private var apodItemsList: List<ApodModel> = mutableListOf()

    inner class ApodFirstItemViewHolder(val binding: ApodFirstItemViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apodFirstItemContent.apodImagePlaceholder.layoutParams.height = imageSideSize
        }
    }

    inner class ApodItemViewHolder(private val binding: ApodItemViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val childViews: MutableList<ApodCardPlaceHolderBinding> = mutableListOf()

        fun addChildrenViews() {
            for (i in 0 until _itemsPerRow) {
                val galleryItemBinding = ApodCardPlaceHolderBinding.inflate(
                    LayoutInflater.from(binding.root.context), binding.root, true
                )
                val imagePH = galleryItemBinding.apodImagePlaceholder
                imagePH.layoutParams.width = imageSideSize - imagePH.marginStart - imagePH.marginEnd
                imagePH.layoutParams.height = imageSideSize
                childViews.add(galleryItemBinding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            ItemType.FIRST_ITEM.ordinal -> ApodFirstItemViewHolder(
                ApodFirstItemViewHolderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            ItemType.USUAL_ITEM.ordinal -> {
                val apodItemVH = ApodItemViewHolder(
                    ApodItemViewHolderBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
                apodItemVH.addChildrenViews()
                apodItemVH
            }
            else -> throw IllegalArgumentException()
        }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ItemType.FIRST_ITEM.ordinal
            else -> ItemType.USUAL_ITEM.ordinal
        }
    }

    fun submitSourceList(source: List<ApodModel>) {
        apodItemsList = source
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ApodFirstItemViewHolder -> {
                val apodVH = holder.binding.apodFirstItemContent
                apodVH.apodImageName.text = apodItemsList[position].title
                apodVH.apodImageDate.text = apodItemsList[position].date
                loadApodImage(apodVH.root, apodVH.apodImagePlaceholder, apodItemsList[position].url)
            }
            is ApodItemViewHolder -> {
                try {
                    for (offset in 0 until _itemsPerRow) {
                        val calcIndex = 1 + offset + (position - 1) * _itemsPerRow
                        val apodVH = holder.childViews[offset]
                        apodVH.apodImageName.text = apodItemsList[calcIndex].title
                        apodVH.apodImageDate.text = apodItemsList[calcIndex].date
                        loadApodImage(
                            apodVH.root, apodVH.apodImagePlaceholder, apodItemsList[calcIndex].url
                        )
                    }
                } catch (ex: IndexOutOfBoundsException) {
                }
            }
        }
    }

    override fun getItemCount() =
        apodItemsList.size / _itemsPerRow + apodItemsList.size % _itemsPerRow

    private enum class ItemType {
        FIRST_ITEM,
        USUAL_ITEM;
    }

    private fun loadApodImage(
        rootView: LinearLayout,
        targetImageView: ImageView,
        url: String
    ) {
        Glide
            .with(rootView.context)
            .load(url)
            .centerCrop()
            .error(R.drawable.nasa_logo)
            .into(targetImageView);
    }
}

