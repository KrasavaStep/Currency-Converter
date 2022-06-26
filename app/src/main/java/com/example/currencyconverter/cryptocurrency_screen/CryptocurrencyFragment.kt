package com.example.currencyconverter.cryptocurrency_screen

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
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
        setHasOptionsMenu(true)
        _binding = FragmentCryptocurrencyBinding.bind(view)
        viewModel.getCryptoList()

        binding.cryptoRv.adapter = adapter
        binding.cryptoRv.layoutManager = LinearLayoutManager(requireContext())

        viewModel.allCryptos.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }

        binding.searchCurrency.clearFocus()
        binding.searchCurrency.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    viewModel.getCryptoForSearch(newText).observe(viewLifecycleOwner) { list ->
                        binding.cryptoRv.visibility = View.GONE
                        if (list.isEmpty()) {
                            binding.errorImage.setImageResource(R.drawable.ic_baseline_search_24)
                            binding.warningTextView.text = getString(R.string.no_cur_found)
                            binding.errorLayout.visibility = View.VISIBLE
                            return@observe
                        } else {
                            binding.errorLayout.visibility = View.GONE
                            binding.cryptoRv.visibility = View.VISIBLE
                        }
                        adapter.setData(list)
                    }
                }
                else {
                    viewModel.allCryptos.observe(viewLifecycleOwner) {
                        adapter.setData(it)
                    }
                }
                return true
            }

        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.crypto_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh_list -> {
                viewModel.getCryptoList()
                viewModel.allCryptos.observe(viewLifecycleOwner) {
                    adapter.setData(it)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}