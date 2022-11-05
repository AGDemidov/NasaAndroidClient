package com.agdemidov.nasaclient.ui.apod

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.agdemidov.nasaclient.R
import com.agdemidov.nasaclient.databinding.ApodCardPlaceHolderBinding
import com.agdemidov.nasaclient.models.ApodModel
import com.bumptech.glide.Glide

class ApodsAdapter(private val imageSideSize: Int) :
    RecyclerView.Adapter<ApodsAdapter.ApodItemViewHolder>() {

    private var apodItemsList: List<ApodModel> = mutableListOf()

    inner class ApodItemViewHolder(val binding: ApodCardPlaceHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apodImagePlaceholder.layoutParams.height = imageSideSize
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApodItemViewHolder =
        ApodItemViewHolder(
            ApodCardPlaceHolderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    private enum class ItemType {
        FIRST_ITEM,
        USUAL_ITEM;
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

    override fun onBindViewHolder(holder: ApodItemViewHolder, position: Int) {
        holder.binding.apodImageName.text = apodItemsList[position].title
        holder.binding.apodImageDate.text = apodItemsList[position].date
        holder.binding.apodImagePlaceholder.setOnClickListener {
            findNavController(holder.binding.root).navigate(
                ApodsFragmentDirections.actionNavApodToApodCardViewerFragment(position)
            )
        }
        apodItemsList[position].url?.let {
            loadApodImage(
                holder.binding.apodGalleryItem, holder.binding.apodImagePlaceholder, it
            )
        }
    }

    override fun getItemCount() = apodItemsList.size

    private fun loadApodImage(
        rootView: LinearLayout,
        targetImageView: ImageView,
        url: String
    ) {
        Glide
            .with(rootView.context)
            .load(url)
            .centerCrop()
            .error(R.drawable.nasa_logo_grey)
            .into(targetImageView)
    }
}
