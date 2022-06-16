package com.example.currencyconverter.currency_list_screen

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconverter.*
import com.example.currencyconverter.currency_list_screen.dialog_fragment.ChooseCurrencyDialogFragment
import com.example.currencyconverter.data.db.entities.CurrencyItem
import com.example.currencyconverter.databinding.FragmentCurrencyListBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class CurrencyListFragment : Fragment(R.layout.fragment_currency_list) {
    private val viewModel by viewModel<CurrencyListViewModel>(named("currencyVM"))
    private lateinit var adapter: CurrencyListAdapter

    private var currencyCode1 = USD_CODE
    private var currencyCode2 = RUB_CODE
    private var currencyValue1 = START_CURRENCY_VALUE
    private var currencyValue2 = START_CURRENCY_VALUE
    private var executionOrder = ORDER_POSITION_FIRST

    private var _binding: FragmentCurrencyListBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter =
            CurrencyListAdapter(layoutInflater, object : CurrencyListAdapter.CurrencyClickListener {
                override fun onCurrencyClicked(item: CurrencyItem) {
                    currencyCode2 = item.code
                    executionOrder = ORDER_POSITION_FIRST
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

        binding.currencyFirstCostTxt.afterTextChangedDebounce(TEXT_CHANGED_DELAY) { currencyValue ->
            if (currencyValue.isNotBlank()) {
                currencyValue1 = currencyValue.toFloat()
                executionOrder = ORDER_POSITION_FIRST
                setCurrencyValues()
            }
        }

        binding.currencySecondCostTxt.afterTextChangedDebounce(TEXT_CHANGED_DELAY) { currencyValue ->
            if (currencyValue.isNotBlank()) {
                currencyValue2 = currencyValue.toFloat()
                executionOrder = ORDER_POSITION_SECOND
                setCurrencyValues()
            }
        }

        binding.searchCurrency.afterTextChangedDebounce(TEXT_CHANGED_DELAY) { currencyName ->
            viewModel.getDataForSearch(currencyName).observe(viewLifecycleOwner) { list ->
                binding.currencyListRv.visibility = View.GONE
                if (list.isEmpty()) {
                    binding.errorImage.setImageResource(R.drawable.ic_baseline_search_24)
                    binding.warningTextView.text = "No currencies found"
                    binding.errorLayout.visibility = View.VISIBLE
                    return@observe
                } else {
                    binding.errorLayout.visibility = View.GONE
                    binding.currencyListRv.visibility = View.VISIBLE
                }
                adapter.setData(list)
            }
        }

        binding.swapBtn.setOnClickListener {
            val tempCode = currencyCode1
            currencyCode1 = currencyCode2
            currencyCode2 = tempCode
            executionOrder = ORDER_POSITION_FIRST
            setCurrencyValues()
        }

        binding.currencyFirstTxt.setOnClickListener {
            showChooseCurrencyDialog()
        }

        binding.currencySecondCostTxt.setOnClickListener {

        }

        MainActivity().networkConnection?.observe(viewLifecycleOwner){
            if (it){
                Toast.makeText(requireContext(), "ds", Toast.LENGTH_SHORT).show()
            }
        }

        //TODO connection callback problem
//        val conn = NetworkConnection(requireContext())
//        conn.observe(viewLifecycleOwner) {
//            if (it) {
//                viewModel.getExchangeRates()
//                viewModel.getAllCurrencies()
//            }
//        }

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

        setCurrencyValues()
        setupChooseCurrencyDialog()
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

                if (executionOrder == ORDER_POSITION_FIRST) currencyValue2 = result
                else if (executionOrder == ORDER_POSITION_SECOND) currencyValue1 = result

                binding.currencyFirstCostTxt.setText(currencyValue1.toString())
                binding.currencySecondCostTxt.setText(currencyValue2.toString())

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

        getCurrenciesFromDb(!IS_FAVOURITE)
    }

    //TODO
    private fun showChooseCurrencyDialog() {
        val dialogFragment = ChooseCurrencyDialogFragment()
        dialogFragment.show(parentFragmentManager, ChooseCurrencyDialogFragment.TAG)
    }

    //TODO
    private fun setupChooseCurrencyDialog() {
        parentFragmentManager.setFragmentResultListener(
            ChooseCurrencyDialogFragment.REQUEST_KEY,
            viewLifecycleOwner,
            FragmentResultListener { _, result ->
                when (result.getInt(ChooseCurrencyDialogFragment.KEY_RESPONSE)) {
                    DialogInterface.BUTTON_NEGATIVE -> Toast.makeText(
                        requireContext(),
                        "dd",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getCurrenciesFromDb(isFavourite: Boolean) {
        viewModel.getCurrenciesFromDb(isFavourite).observe(viewLifecycleOwner) {
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
    }

    companion object {
        private const val ORDER_POSITION_FIRST = 1
        private const val ORDER_POSITION_SECOND = 2
        private const val TEXT_CHANGED_DELAY = 1000L
        private const val CHOOSE_DIALOG_TAG = "choose dialog"
        private const val START_CURRENCY_VALUE = 1f
        private const val OFFLINE_VAL = "Offline"
        private const val ONLINE_VAL = "Online"
        private const val USD_CODE = "USD"
        private const val RUB_CODE = "RUB"
        private const val IS_FAVOURITE = true
    }

}