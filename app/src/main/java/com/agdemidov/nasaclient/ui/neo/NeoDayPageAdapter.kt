package com.agdemidov.nasaclient.ui.neo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agdemidov.nasaclient.databinding.NeoItemViewHolderBinding
import com.agdemidov.nasaclient.models.NeoModel

class NeoDayPageAdapter : RecyclerView.Adapter<NeoDayPageAdapter.NeoItemViewHolder>() {

    private var neoTodayItemsList: List<NeoModel> = mutableListOf()

    inner class NeoItemViewHolder(val binding: NeoItemViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NeoItemViewHolder =
        NeoItemViewHolder(
            NeoItemViewHolderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    fun submitList(source: List<NeoModel>) {
        neoTodayItemsList = source
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: NeoItemViewHolder, position: Int) {
        holder.binding.name.text = neoTodayItemsList[position].name
        holder.binding.url.text = neoTodayItemsList[position].nasa_jpl_url
            .replace("http://", "https://")
    }

    override fun getItemCount() = neoTodayItemsList.size
}

