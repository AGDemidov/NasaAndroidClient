package com.agdemidov.nasaclient.ui.apodcardviewer

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agdemidov.nasaclient.R
import com.agdemidov.nasaclient.databinding.ApodCardsCarouselPlaceHolderBinding
import com.agdemidov.nasaclient.models.ApodModel
import com.bumptech.glide.Glide

class ApodCardsCarouselAdapter :
    RecyclerView.Adapter<ApodCardsCarouselAdapter.ApodCardsCarouselHolder>() {

    private var apodModels: List<ApodModel> = listOf()

    inner class ApodCardsCarouselHolder(val binding: ApodCardsCarouselPlaceHolderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApodCardsCarouselHolder {
        val itemHolder = ApodCardsCarouselPlaceHolderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ApodCardsCarouselHolder(itemHolder)
    }

    fun submitData(source: List<ApodModel>) {
        apodModels = source
        notifyDataSetChanged()
    }

    private fun getHtmlDescription(apod: ApodModel): CharSequence {
        return Html.fromHtml(
            "${apod.date ?: ""}<br>" +
                    "<b>${apod.title ?: ""}</b><br>" +
                    "<br>${apod.explanation ?: "N/A"}",
            Html.FROM_HTML_MODE_COMPACT
        )
    }

    override fun onBindViewHolder(holder: ApodCardsCarouselHolder, position: Int) {
        apodModels[position].url?.let {
            Glide
                .with(holder.binding.root.context)
                .load(it)
                .error(R.drawable.nasa_logo_grey)
                .fitCenter()
                .into(holder.binding.carouselImage)
        }
        holder.binding.carouselText.setText(
            getHtmlDescription(apodModels[position])
        )
    }

    override fun getItemCount() = apodModels.size
}
