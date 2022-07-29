package com.agdemidov.nasaclient.ui.apod

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agdemidov.nasaclient.databinding.ApodItemViewHolderBinding
import com.agdemidov.nasaclient.models.ApodModel

class ApodsAdapter : RecyclerView.Adapter<ApodsAdapter.ApodItemViewHolder>() {

    private var apodItemsList: List<ApodModel> = mutableListOf()

    inner class ApodItemViewHolder(val binding: ApodItemViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApodItemViewHolder =
        ApodItemViewHolder(
            ApodItemViewHolderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    fun submitList(source: List<ApodModel>) {
        apodItemsList = source
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ApodItemViewHolder, position: Int) {
        holder.binding.name.text =
            "${apodItemsList[position].date} ${apodItemsList[position].title}"
        holder.binding.url.text = apodItemsList[position].url
    }

    override fun getItemCount() = apodItemsList.size
}

