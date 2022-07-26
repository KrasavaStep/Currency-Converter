package com.example.currencyconverter.currency_list_screen

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconverter.*
import com.example.currencyconverter.data.db.entities.CurrencyItem
import com.example.currencyconverter.databinding.FragmentCurrencyListBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class CurrencyListFragment : Fragment(R.layout.fragment_currency_list) {
    private val viewModel by viewModel<CurrencyListViewModel>(named("currencyVM"))
    private lateinit var adapter: CurrencyListAdapter

    private var currencyCode1 = USD_CODE
    private var currencyCode2 = RUB_CODE
    private var currencyValue1 = START_CURRENCY_VALUE_RES
    private var currencyValue2 = START_CURRENCY_VALUE_RES
    private var executionOrder = ORDER_POSITION_FIRST_RES
    private var countOfDecimalDigits = COUNT_OF_DECIMAL_DIGITS_RES

    private var _binding: FragmentCurrencyListBinding? = null
    private val binding
        get() = _binding!!

    private var prefsDecimal: SharedPreferences? = null
    private var prefsExchange: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter =
            CurrencyListAdapter(
                object : CurrencyListAdapter.CurrencyClickListener {
                override fun onCurrencyClicked(item: CurrencyItem) {
                    currencyCode2 = item.code
                    executionOrder = ORDER_POSITION_FIRST_RES
                    setCurrencyValues()
                }

                override fun onCheckboxChecked(item: CurrencyItem, isChecked: Boolean) {
                    val newCurrencyItem = item.copy(isFavourite = isChecked)
                    viewModel.updateCurrency(newCurrencyItem)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        _binding = FragmentCurrencyListBinding.bind(view)
        binding.currencyListRv.adapter = adapter
        binding.currencyListRv.layoutManager = LinearLayoutManager(requireContext())

        prefsDecimal = activity?.getSharedPreferences(MainActivity.PREF_DECIMAL_NAME, Context.MODE_PRIVATE)
        prefsDecimal?.let {
            countOfDecimalDigits = it.getInt(
                MainActivity.DECIMAL_DIGITS_KEY,
                MainActivity.DEF_DECIMAL_VALUE
            )
        }

        prefsExchange = activity?.getSharedPreferences(PREF_EX_NAME, Context.MODE_PRIVATE)
        prefsExchange?.let { value ->
            currencyValue2 = value.getFloat(PREF_VAL2, START_CURRENCY_VALUE_RES)
        }
        prefsExchange?.let { value ->
            currencyValue1 = value.getFloat(PREF_VAL1, START_CURRENCY_VALUE_RES)
        }

        val networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getExchangeRates()
                viewModel.getAllCurrencies()
                getCurrenciesFromDb(!IS_FAVOURITE)
            } else {
                getCurrenciesFromDb(!IS_FAVOURITE)
            }
        }

        binding.currencyFirstCostTxt.afterTextChangedDebounce(TEXT_CHANGED_DELAY_RES) { currencyValue ->
            if (currencyValue.isNotBlank()) {
                val value = currencyValue.replace(',', '.')
                currencyValue1 = value.toFloat()
                executionOrder = ORDER_POSITION_FIRST_RES
                setCurrencyValues()
            } else {
                currencyValue1 = 0f
                executionOrder = ORDER_POSITION_FIRST_RES
                setCurrencyValues()
            }
        }

        binding.currencySecondCostTxt.afterTextChangedDebounce(TEXT_CHANGED_DELAY_RES) { currencyValue ->
            if (currencyValue.isNotBlank()) {
                val value = currencyValue.replace(',', '.')
                currencyValue2 = value.toFloat()
                executionOrder = ORDER_POSITION_SECOND_RES
                setCurrencyValues()
            } else {
                currencyValue2 = 0f
                executionOrder = ORDER_POSITION_FIRST_RES
                setCurrencyValues()
            }
        }

        binding.searchCurrency.clearFocus()
        binding.searchCurrency.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    viewModel.getCurrencyForSearch(newText).observe(viewLifecycleOwner) { list ->
                        binding.currencyListRv.visibility = View.GONE
                        if (list.isEmpty()) {
                            binding.errorImage.setImageResource(R.drawable.ic_baseline_search_24)
                            binding.warningTextView.text = getString(R.string.no_cur_found)
                            binding.errorLayout.visibility = View.VISIBLE
                            return@observe
                        } else {
                            binding.errorLayout.visibility = View.GONE
                            binding.currencyListRv.visibility = View.VISIBLE
                        }
                        adapter.setData(list)
                    }
                }
                return true
            }

        })

        binding.swapBtn.setOnClickListener {
            val tempCode = currencyCode1
            currencyCode1 = currencyCode2
            currencyCode2 = tempCode
            executionOrder = ORDER_POSITION_FIRST_RES
            setCurrencyValues()
        }

        viewModel.currencies.observe(viewLifecycleOwner) {
            when (it) {
                is ResultState.Error -> {
                    binding.currencyListProgressBar.visibility = View.GONE
                    binding.currencyListRv.visibility = View.VISIBLE
                    it.exception.message?.let { it1 -> Log.d(getString(TAG_RES), it1) }
                }
                is ResultState.Success -> {
                    binding.currencyListProgressBar.visibility = View.GONE
                    binding.errorLayout.visibility = View.GONE
                    binding.currencyListRv.visibility = View.VISIBLE
                }
                is ResultState.Loading -> {
                    binding.currencyListProgressBar.visibility = View.VISIBLE
                    binding.currencyListRv.visibility = View.GONE
                    binding.errorLayout.visibility = View.GONE
                }
            }
        }

        viewModel.rates.observe(viewLifecycleOwner) {
            when (it) {
                is ResultState.Error -> {
                    binding.currencyListProgressBar.visibility = View.GONE
                    binding.currencyListRv.visibility = View.VISIBLE
                    it.exception.message?.let { it1 -> Log.d(getString(TAG_RES), it1) }
                }
                is ResultState.Success -> {
                    binding.currencyListProgressBar.visibility = View.GONE
                    binding.errorLayout.visibility = View.GONE
                    binding.currencyListRv.visibility = View.VISIBLE
                }
                is ResultState.Loading -> {
                    binding.currencyListProgressBar.visibility = View.VISIBLE
                    binding.currencyListRv.visibility = View.GONE
                    binding.errorLayout.visibility = View.GONE
                }
            }
        }
    }


    private fun setCurrencyValues() {
        viewModel.getAllDataForTwoCurrencies(currencyCode1, currencyCode2)
        viewModel.dataForTwoCurrencies.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { data ->
                val result =
                    viewModel.setDataForCurrencies(
                        data,
                        executionOrder,
                        currencyValue1,
                        currencyValue2
                    )

                if (executionOrder == ORDER_POSITION_FIRST_RES) {
                    currencyValue2 = result
                    prefsExchange?.edit()?.putFloat(PREF_VAL2, currencyValue2)?.apply()
                } else if (executionOrder == ORDER_POSITION_SECOND_RES) {
                    currencyValue1 = result
                    prefsExchange?.edit()?.putFloat(PREF_VAL1, currencyValue1)?.apply()
                }

                binding.currencyFirstCostTxt.setText(
                    String.format(
                        "%.${countOfDecimalDigits}f",
                        currencyValue1
                    )
                )
                binding.currencySecondCostTxt.setText(
                    String.format(
                        "%.${countOfDecimalDigits}f",
                        currencyValue2
                    )
                )
            }
        }

        viewModel.errorResult.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { res ->
                if (res.isNotEmpty()) {
                    binding.currencyListRv.visibility = View.GONE
                    binding.errorLayout.visibility = View.VISIBLE
                    binding.warningTextView.text = res
                } else {
                    binding.currencyListRv.visibility = View.VISIBLE
                    binding.errorLayout.visibility = View.GONE
                }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_currency_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.simple_view -> {
                getCurrenciesFromDb(!IS_FAVOURITE)
                return true
            }
            R.id.favourite_view -> {
                getCurrenciesFromDb(IS_FAVOURITE)
                return true
            }
            R.id.refresh_list -> {
                viewModel.getExchangeRates()
                viewModel.getAllCurrencies()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getCurrenciesFromDb(isFavourite: Boolean) {
        viewModel.getCurrenciesFromDb(isFavourite).observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.currencyListProgressBar.visibility = View.GONE
                binding.currencyListRv.visibility = View.GONE
                binding.errorLayout.visibility = View.VISIBLE
                binding.warningTextView.text = getString(R.string.no_cur_found)
                return@observe
            } else {
                binding.currencyListProgressBar.visibility = View.GONE
                binding.errorLayout.visibility = View.GONE
                binding.currencyListRv.visibility = View.VISIBLE
            }
            adapter.setData(it)
            setCurrencyValues()
        }
    }

    companion object {
        private const val ORDER_POSITION_FIRST_RES = 1
        private const val ORDER_POSITION_SECOND_RES = 2
        private const val TEXT_CHANGED_DELAY_RES = 1000L
        private const val START_CURRENCY_VALUE_RES = 1f
        private const val IS_FAVOURITE = true
        private const val COUNT_OF_DECIMAL_DIGITS_RES = 3

        private const val USD_CODE = "USD"
        private const val RUB_CODE = "RUB"

        private const val PREF_EX_NAME = "exchange_values"
        private const val PREF_VAL1 = "value_ex1"
        private const val PREF_VAL2 = "value_ex2"

        private const val TAG_RES = R.string.cur_list_tag
    }

}