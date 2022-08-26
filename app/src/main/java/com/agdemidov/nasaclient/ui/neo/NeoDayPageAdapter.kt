package com.agdemidov.nasaclient.ui.neo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.agdemidov.nasaclient.R
import com.agdemidov.nasaclient.databinding.NeoItemViewHolderBinding
import com.agdemidov.nasaclient.models.NeoModel
import com.agdemidov.nasaclient.utils.Extensions.showView

class NeoDayPageAdapter(private val action: OnItemClicked) :
    RecyclerView.Adapter<NeoDayPageAdapter.NeoItemViewHolder>() {

    private var neoTodayItemsList: List<NeoModel> = mutableListOf()
    private var allItemsExpanded = false

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

    fun onExpandCollapseClicked() {
        allItemsExpanded = !allItemsExpanded
        action.allItemsExpanded(allItemsExpanded)
        neoTodayItemsList.forEach {
            it.isExpanded = allItemsExpanded
            notifyDataSetChanged()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NeoItemViewHolder, position: Int) {
        holder.binding.neoName.text = neoTodayItemsList[position].name

        holder.binding.root.setOnClickListener {
            neoTodayItemsList[position].isExpanded = !neoTodayItemsList[position].isExpanded
            allItemsExpanded = neoTodayItemsList.none { !it.isExpanded }
            action.allItemsExpanded(allItemsExpanded)
            notifyItemChanged(position)
        }
        holder.binding.extendedNeoData.showView(neoTodayItemsList[position].isExpanded)

        holder.binding.magnitudeValue.text = neoTodayItemsList[position].absoluteMagnitude

        holder.binding.diameterMinValue.text =
            neoTodayItemsList[position].estimatedDiameterMetersMin
        holder.binding.diameterMaxValue.text =
            neoTodayItemsList[position].estimatedDiameterMetersMax

        val context = holder.binding.root.context
        holder.binding.isHazardousValue.text =
            neoTodayItemsList[position].potentiallyHazardous?.let {
                if (it) {
                    val color = ContextCompat.getColor(context, R.color.hazardous_color)
                    holder.binding.isHazardousTitle.setTextColor(color)
                    holder.binding.isHazardousValue.setTextColor(color)
                    context.resources.getString(R.string.hazardous_yes)
                } else {
                    val color = ContextCompat.getColor(context, R.color.not_hazardous_color)
                    holder.binding.isHazardousTitle.setTextColor(color)
                    holder.binding.isHazardousValue.setTextColor(color)
                    context.resources.getString(R.string.hazardous_no)
                }
            } ?: "N/A"

        val url = neoTodayItemsList[position].nasa_jpl_url
            .replace("http://", "https://")
        holder.binding.openWebPage.setOnClickListener { action.onClick(url) }
    }

    override fun getItemCount() = neoTodayItemsList.size

    interface OnItemClicked {
        fun onClick(url: String)
        fun allItemsExpanded(allItemsExpanded: Boolean)
    }
}

