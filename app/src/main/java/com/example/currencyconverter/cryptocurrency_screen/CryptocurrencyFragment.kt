package com.example.currencyconverter.cryptocurrency_screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.currencyconverter.NetworkConnection
import com.example.currencyconverter.R
import com.example.currencyconverter.currency_list_screen.CurrencyListFragment
import com.example.currencyconverter.currency_list_screen.CurrencyListViewModel
import com.example.currencyconverter.databinding.FragmentCryptocurrencyBinding
import com.example.currencyconverter.databinding.FragmentCurrencyListBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class CryptocurrencyFragment : Fragment(R.layout.fragment_cryptocurrency) {

    private val viewModel by viewModel<CryptocurrencyViewModel>(named("cryptoVM"))

    private var _binding: FragmentCryptocurrencyBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCryptocurrencyBinding.bind(view)
        viewModel.getCryptoList()
    }
}