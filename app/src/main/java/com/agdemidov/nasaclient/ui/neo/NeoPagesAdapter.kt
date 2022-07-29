package com.agdemidov.nasaclient.ui.neo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agdemidov.nasaclient.R
import com.agdemidov.nasaclient.databinding.NeoPageViewHolderBinding
import com.agdemidov.nasaclient.models.NeoModel
import com.agdemidov.nasaclient.utils.DateGetter.getToday
import java.util.*

class NeoPagesAdapter(val context: Context, val action: OnTodayPageCreated) :
    RecyclerView.Adapter<NeoPagesAdapter.NeoPageViewHolder>() {

    interface OnTodayPageCreated {
        fun onTodayPageCreated(index: Int)
    }

    private var neoPagesMap: List<Pair<String, List<NeoModel>>> = listOf()
    private var todayDateIndex: Int = 0

    inner class NeoPageViewHolder(val binding: NeoPageViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NeoPageViewHolder {
        val itemHolder = NeoPageViewHolderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NeoPageViewHolder(itemHolder)
    }

    fun submitData(source: SortedMap<String, List<NeoModel>>) {
        neoPagesMap = source.toList()
        calculateTodayPageIndex()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: NeoPageViewHolder, position: Int) {
        holder.binding.neoDate.text =
            context.resources.getString(R.string.neo_for_date, neoPagesMap[position].first)

        val neoDayAdapter = holder.binding.neoDayItems
        neoDayAdapter.layoutManager = LinearLayoutManager(context)
        val neoDayPageAdapter = NeoDayPageAdapter()
        neoDayAdapter.adapter = neoDayPageAdapter
        neoDayPageAdapter.submitList(neoPagesMap[position].second)
        if (position == todayDateIndex) {
            action.onTodayPageCreated(todayDateIndex)
        }
    }

    override fun getItemCount() = neoPagesMap.size

    private fun calculateTodayPageIndex() {
        for (item in neoPagesMap) {
            if (item.first == getToday()) {
                todayDateIndex = neoPagesMap.indexOf(item)
                return
            }
        }
    }
}
