package com.example.currencyconverter.cryptocurrency_screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconverter.R
import com.example.currencyconverter.data.db.entities.CryptocurrencyItem
import com.example.currencyconverter.databinding.FragmentCryptocurrencyBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class CryptocurrencyFragment : Fragment(R.layout.fragment_cryptocurrency) {

    private val viewModel by viewModel<CryptocurrencyViewModel>(named("cryptoVM"))
    private lateinit var adapter: CryptocurrencyAdapter

    private var _binding: FragmentCryptocurrencyBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = inflater.inflate(R.layout.fragment_cryptocurrency, container, false)
        return fragmentView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = CryptocurrencyAdapter(
            layoutInflater,
            object : CryptocurrencyAdapter.CryptoClickListener {
                override fun onCryptoClicked(item: CryptocurrencyItem) {
                    val action =
                        CryptocurrencyFragmentDirections.actionCryptocurrencyFragmentToCryptoGraphicFragment(
                            item.id,
                            item.marketCap.toLong()
                        )
                    Navigation.findNavController(fragmentView).navigate(action)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCryptocurrencyBinding.bind(view)
        viewModel.getCryptoList()

        binding.cryptoRv.adapter = adapter
        binding.cryptoRv.layoutManager = LinearLayoutManager(requireContext())

        viewModel.allCryptos.observe(viewLifecycleOwner) {
            Log.d("MyApp", it.size.toString())
            adapter.setData(it)
        }
    }
}