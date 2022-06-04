package com.example.currencyconverter.currencylistscreen

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconverter.R
import com.example.currencyconverter.ResultState
import com.example.currencyconverter.databinding.FragmentCurrencyListBinding
import com.example.currencyconverter.data.currencyapi.models.CurrencyApiModel
import com.example.currencyconverter.data.db.entities.CurrencyItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class CurrencyListFragment : Fragment(R.layout.fragment_currency_list) {
    private val viewModel by viewModel<CurrencyListViewModel>()
    private lateinit var adapter: CurrencyListAdapter

    private var _binding: FragmentCurrencyListBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter =
            CurrencyListAdapter(layoutInflater, object : CurrencyListAdapter.CurrencyClickListener {
                override fun onCurrencyClicked(item: CurrencyItem) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCurrencyListBinding.bind(view)
        binding.currencyListRv.adapter = adapter
        binding.currencyListRv.layoutManager = LinearLayoutManager(requireContext())

        viewModel.setData()
        viewModel.currenciesResult.observe(viewLifecycleOwner) {
            when (it) {
                is ResultState.Error -> {
                    binding.currencyListProgressBar.visibility = View.GONE
                    binding.errorLayout.visibility = View.VISIBLE
                    it.exception.message?.let { it1 -> Log.d("MyApp", it1) }
                }
                is ResultState.Success -> {
                    binding.currencyListProgressBar.visibility = View.GONE
                    binding.errorLayout.visibility = View.GONE
                }
                is ResultState.Loading -> {
                    binding.currencyListProgressBar.visibility = View.VISIBLE
                }
            }
        }

        viewModel.getCurrenciesFromDb.observe(viewLifecycleOwner){
            adapter.setData(it)
        }
//        viewModel.getAllCurrencies()
//        viewModel.currencies.observe(viewLifecycleOwner){
//            when(it){
//                is ResultState.Error -> {
//                    binding.currencyListProgressBar.visibility = View.GONE
//                    binding.errorLayout.visibility = View.VISIBLE
//                    it.exception.message?.let { it1 -> Log.d("MyApp", it1) }
//                }
//                is ResultState.Success -> {
//                    binding.currencyListProgressBar.visibility = View.GONE
//                    binding.errorLayout.visibility = View.GONE
//                    adapter.setData(it.data)
//                }
//                is ResultState.Loading -> {
//                    binding.currencyListProgressBar.visibility = View.VISIBLE
//                }
//            }
//        }

        }
    }