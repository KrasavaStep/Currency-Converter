package com.example.currencyconverter.currencylistscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.databinding.CurrencyListItemBinding

class CurrencyListAdapter (
    private val layoutInflater: LayoutInflater,
    private val clickListener: CurrencyClickListener
) : ListAdapter<UserApiModel, UserListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CurrencyListItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            userId.text = item.id.toString()
            userName.text = item.firstName
            userLastname.text = item.lastName
            userAddress.text = item.address
        }
    }

    inner class ViewHolder(val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val userApiModel = getItem(adapterPosition)
                    clickListener.onUserClicked(userApiModel)
                }
            }
        }
    }

    fun setData(users: List<UserApiModel>) {
        submitList(users.toMutableList())
    }

    interface CurrencyClickListener {
        fun onCurrencyClicked(user: UserApiModel)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<UserApiModel> = object : DiffUtil.ItemCallback<UserApiModel>() {
            override fun areItemsTheSame(oldItem: UserApiModel, newItem: UserApiModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserApiModel, newItem: UserApiModel): Boolean {
                return oldItem == newItem
            }
        }
    }

}