package com.example.currencyconverter.currencylistscreen

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconverter.NetworkConnection
import com.example.currencyconverter.R
import com.example.currencyconverter.ResultState
import com.example.currencyconverter.afterTextChangedDebounce
import com.example.currencyconverter.data.db.entities.CurrencyItem
import com.example.currencyconverter.databinding.FragmentCurrencyListBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class CurrencyListFragment : Fragment(R.layout.fragment_currency_list) {
    private val viewModel by viewModel<CurrencyListViewModel>()
    private lateinit var adapter: CurrencyListAdapter

    private var currencyCode1 = USD_CODE
    private var currencyCode2 = RUB_CODE
    private var currencyValue1 = 1f
    private var currencyValue2 = 1f
    private var executionOrder = ORDER_POSITION_FIRST

    private var _binding: FragmentCurrencyListBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter =
            CurrencyListAdapter(layoutInflater, object : CurrencyListAdapter.CurrencyClickListener {
                override fun onCurrencyClicked(item: CurrencyItem) {
//                    binding.currencySecondTxt.text = item.code
//                    binding.currencySecondCostTxt.setText(START_CURRENCY_VALUE)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCurrencyListBinding.bind(view)
        binding.currencyListRv.adapter = adapter
        binding.currencyListRv.layoutManager = LinearLayoutManager(requireContext())

        binding.currencyFirstCostTxt.afterTextChangedDebounce(1000){ currencyValue ->
            if (currencyValue.isNotBlank()) {
                currencyValue1 = currencyValue.toFloat()
                executionOrder = ORDER_POSITION_FIRST
                setCurrencyValues()
            }
        }

        binding.currencySecondCostTxt.afterTextChangedDebounce(1000){ currencyValue ->
            if (currencyValue.isNotBlank()){
                currencyValue2 = currencyValue.toFloat()
                executionOrder = ORDER_POSITION_SECOND
                setCurrencyValues()
            }
        }

        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getExchangeRates()
                viewModel.getAllCurrencies()
                Snackbar.make(view, ONLINE_VAL, Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(view, OFFLINE_VAL, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.currencies.observe(viewLifecycleOwner) {
            when (it) {
                is ResultState.Error -> {
                    binding.currencyListProgressBar.visibility = View.GONE
                    binding.errorLayout.visibility = View.VISIBLE
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

        viewModel.rates.observe(viewLifecycleOwner) {
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

        viewModel.getCurrenciesFromDb.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.currencyListProgressBar.visibility = View.GONE
                binding.errorLayout.visibility = View.VISIBLE
                return@observe
            } else {
                binding.currencyListProgressBar.visibility = View.GONE
                binding.errorLayout.visibility = View.GONE
            }
            adapter.setData(it)
        }

        setCurrencyValues()
    }


    private fun setCurrencyValues() {
        viewModel.getAllDataForTwoCurrencies(currencyCode1, currencyCode2)
        viewModel.dataForTwoCurrencies.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { data ->
                val result =
                    viewModel.setDataForCurrencies(data, executionOrder, currencyValue1, currencyValue2)

                if (executionOrder == ORDER_POSITION_FIRST) currencyValue2 = result
                else if (executionOrder == ORDER_POSITION_SECOND) currencyValue1 = result

                binding.currencyFirstCostTxt.setText(currencyValue1.toString())
                binding.currencySecondCostTxt.setText(currencyValue2.toString())

            }
        }
        viewModel.firstCurrencyCode.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { code ->
                currencyCode1 = code
            }
        }
        viewModel.secondCurrencyCode.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { code ->
                currencyCode2 = code
            }
        }
        viewModel.firstCurrencyName.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { name ->
                binding.currencyFirstTxt.text = name
            }
        }
        viewModel.secondCurrencyName.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { name ->
                binding.currencySecondTxt.text = name
            }
        }
    }

    companion object {
        private const val ORDER_POSITION_FIRST = 1
        private const val ORDER_POSITION_SECOND = 2
        private const val TEXT_CHANGED_DELAY = 1000L
        private const val CHOOSE_DIALOG_TAG = "choose dialog"
        private const val START_CURRENCY_VALUE = "1"
        private const val OFFLINE_VAL = "Offline"
        private const val ONLINE_VAL = "Online"
        private const val USD_CODE = "USD"
        private const val RUB_CODE = "RUB"
    }

}