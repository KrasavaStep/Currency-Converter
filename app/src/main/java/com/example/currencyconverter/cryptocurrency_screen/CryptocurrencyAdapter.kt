package com.example.currencyconverter.cryptocurrency_screen

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.currencyconverter.R
import com.example.currencyconverter.data.db.entities.CryptocurrencyItem
import com.example.currencyconverter.databinding.CryptoListItemBinding
import kotlin.math.roundToInt

class CryptocurrencyAdapter(
    private val layoutInflater: LayoutInflater,
    private val clickListener: CryptoClickListener
) : ListAdapter<CryptocurrencyItem, CryptocurrencyAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CryptoListItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            name.text = item.name
            shortName.text = item.symbol?.uppercase()
            Glide.with(holder.itemView)
                .asBitmap()
                .load(item.image)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        img.setImageBitmap(resource)
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        img.setImageResource(R.drawable.broken_coin_png)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
            var text = holder.itemView.context.getString(R.string.crypto_price)
            price.text = String.format(text, item.currentPrice.toString())
            text = holder.itemView.context.getString(R.string.crypto_per_1h)
            var x = item.priceChangePercentage1hInCurrency * 100
            var result = x.roundToInt() / 1000.0
            percent1h.text = String.format(text, result.toString())
            val highPercent = holder.itemView.resources.getColor(R.color.high_percent_color)
            val lowPercent = holder.itemView.resources.getColor(R.color.low_percent_color)
            if (result >= 0.0) {
                percent1h.setTextColor(highPercent)
            } else {
                percent1h.setTextColor(lowPercent)
            }
            text = holder.itemView.context.getString(R.string.crypto_per_24h)

            x = item.priceChangePercentage24h * 100
            result = x.roundToInt() / 1000.0
            if (result >= 0.0) {
                Log.d("MyApp", result.toString())
                Log.d("MyApp", (result >= 0.0).toString())
                percent1h.setTextColor(highPercent)
            } else {
                percent1h.setTextColor(lowPercent)
            }
            percent24h.text = String.format(text, result.toString())
        }
    }

    inner class ViewHolder(val binding: CryptoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val cryptoItem = getItem(adapterPosition)
                    clickListener.onCryptoClicked(cryptoItem)
                }
            }
        }
    }

    fun setData(cryptos: List<CryptocurrencyItem>) {
        submitList(cryptos.toMutableList())
    }

    interface CryptoClickListener {
        fun onCryptoClicked(item: CryptocurrencyItem)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<CryptocurrencyItem> =
            object : DiffUtil.ItemCallback<CryptocurrencyItem>() {
                override fun areItemsTheSame(
                    oldItem: CryptocurrencyItem,
                    newItem: CryptocurrencyItem
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: CryptocurrencyItem,
                    newItem: CryptocurrencyItem
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

}