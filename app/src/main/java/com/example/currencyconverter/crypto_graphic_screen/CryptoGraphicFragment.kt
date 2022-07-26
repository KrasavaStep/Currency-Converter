package com.example.currencyconverter.crypto_graphic_screen

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.currencyconverter.R
import com.example.currencyconverter.ResultState
import com.example.currencyconverter.databinding.FragmentCryptoGraphicBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class CryptoGraphicFragment : Fragment(R.layout.fragment_crypto_graphic) {
    private val viewModel by viewModel<CryptoGraphicViewModel>(named("graphicVM"))
    private val args: CryptoGraphicFragmentArgs by navArgs()

    private var _binding: FragmentCryptoGraphicBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCryptoGraphicBinding.bind(view)
        val cryptoId = args.cryptoid
        val marketCap = args.marketCap

        binding.marketCap.text = String.format(getString(R.string.market_cap), marketCap.toString())

        viewModel.getDayOHLC(cryptoId)
        viewModel.getYearOHLC(cryptoId)

        viewModel.dayOHLC.observe(viewLifecycleOwner) {
            when (it) {
                is ResultState.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
                is ResultState.Success -> {
                    viewModel.getDayOHLCData(it.data)
                    binding.progress.visibility = View.GONE
                }
                is ResultState.Error -> {
                    binding.progress.visibility = View.GONE
                    Log.d(getString(TAG_RES), it.exception.message.toString())
                }
            }
        }

        viewModel.yearOHLC.observe(viewLifecycleOwner) {
            when (it) {
                is ResultState.Loading -> {
                    binding.progress2.visibility = View.VISIBLE
                }
                is ResultState.Success -> {
                    viewModel.getYearOHLCData(it.data)
                    binding.progress2.visibility = View.GONE
                }
                is ResultState.Error -> {
                    binding.progress2.visibility = View.GONE
                    Log.d(getString(TAG_RES), it.exception.message.toString())
                }
            }
        }

        viewModel.dayOHLCData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { data ->
                binding.graphDay.data = data
                binding.graphDay.animateXY(
                    resources.getInteger(DURATION_MILLIS_X_RES),
                    resources.getInteger(DURATION_MILLIS_Y_RES)
                )
                binding.graphDay.contentDescription = getString(R.string.chart_desc)
                binding.graphDay.setScaleMinima(
                    resources.getInteger(SCALE_X_RES).toFloat(),
                    resources.getInteger(SCALE_Y_RES).toFloat()
                )
            }
        }

        viewModel.yearOHLCData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { data ->
                binding.graphYear.data = data
                binding.graphYear.animateXY(
                    resources.getInteger(DURATION_MILLIS_X_RES),
                    resources.getInteger(DURATION_MILLIS_Y_RES)
                )
                binding.graphYear.contentDescription = getString(R.string.chart_desc)
                binding.graphYear.setScaleMinima(
                    resources.getInteger(SCALE_X_RES).toFloat(),
                    resources.getInteger(SCALE_Y_RES).toFloat()
                )
            }
        }


    }

    companion object {
        private const val DURATION_MILLIS_X_RES = R.integer.DURATION_MILLIS_X
        private const val DURATION_MILLIS_Y_RES = R.integer.DURATION_MILLIS_Y
        private const val SCALE_X_RES = R.integer.SCALE_X
        private const val SCALE_Y_RES = R.integer.SCALE_Y
        private const val TAG_RES = R.string.crypto_gr_tag
    }

}