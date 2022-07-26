package com.example.currencyconverter.currency_list_screen

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.R
import com.example.currencyconverter.databinding.CurrencyListItemBinding
import com.example.currencyconverter.data.db.entities.CurrencyItem

class CurrencyListAdapter(
    private val clickListener: CurrencyClickListener
) : ListAdapter<CurrencyItem, CurrencyListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CurrencyListItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            fullNameCur.text = item.name
            val text = holder.itemView.context.getString(R.string.currency_code)
            currencyCode.text = String.format(text, item.symbol, item.code)
            currencyCost.text = item.symbol_native
            favouriteCheckBox.isChecked = item.isFavourite
        }
    }

    inner class ViewHolder(val binding: CurrencyListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val currencyItem = getItem(adapterPosition)
                    clickListener.onCurrencyClicked(currencyItem)
                }
            }
            binding.favouriteCheckBox.setOnCheckedChangeListener{ _, isChecked ->
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val currencyItem = getItem(adapterPosition)
                    clickListener.onCheckboxChecked(currencyItem, isChecked)
                }
            }
        }
    }

    fun setData(currencies: List<CurrencyItem>) {
        submitList(currencies.toMutableList())
    }

    interface CurrencyClickListener {
        fun onCurrencyClicked(item: CurrencyItem)
        fun onCheckboxChecked(item: CurrencyItem, isChecked: Boolean)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<CurrencyItem> =
            object : DiffUtil.ItemCallback<CurrencyItem>() {
                override fun areItemsTheSame(
                    oldItem: CurrencyItem,
                    newItem: CurrencyItem
                ): Boolean {
                    return oldItem.code == newItem.code
                }

                override fun areContentsTheSame(
                    oldItem: CurrencyItem,
                    newItem: CurrencyItem
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

}