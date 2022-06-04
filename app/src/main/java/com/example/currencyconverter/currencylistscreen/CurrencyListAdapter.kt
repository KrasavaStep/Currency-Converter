package com.example.currencyconverter.currencylistscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.databinding.CurrencyListItemBinding
import com.example.currencyconverter.data.currencyapi.models.CurrencyApiModel
import com.example.currencyconverter.data.db.entities.CurrencyItem

class CurrencyListAdapter (
    private val layoutInflater: LayoutInflater,
    private val clickListener: CurrencyClickListener
) : ListAdapter<CurrencyItem, CurrencyListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CurrencyListItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            fullNameCur.text = item.name
            pluralNameCur.text = item.name_plural
            currencyCost.text = "${item.symbol_native}  ${item.value}"
            favouriteCheckBox.isChecked = false
        }
    }

    inner class ViewHolder(val binding: CurrencyListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val currencyItem = getItem(adapterPosition)
                    clickListener.onCurrencyClicked(currencyItem)
                }
            }
        }
    }

    fun setData(users: List<CurrencyItem>) {
        submitList(users.toMutableList())
    }

    interface CurrencyClickListener {
        fun onCurrencyClicked(user: CurrencyItem)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<CurrencyItem> = object : DiffUtil.ItemCallback<CurrencyItem>() {
            override fun areItemsTheSame(oldItem: CurrencyItem, newItem: CurrencyItem): Boolean {
                return oldItem.code == newItem.code
            }

            override fun areContentsTheSame(oldItem: CurrencyItem, newItem: CurrencyItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}