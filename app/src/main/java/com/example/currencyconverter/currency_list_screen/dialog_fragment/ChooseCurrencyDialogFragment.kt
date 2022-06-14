package com.example.currencyconverter.currency_list_screen.dialog_fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.currencyconverter.R
import com.example.currencyconverter.currency_list_screen.CurrencyListAdapter

class ChooseCurrencyDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = DialogInterface.OnClickListener { _, which ->
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(KEY_RESPONSE to which))
        }
        return AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setTitle("Choose currency")
            .setNegativeButton("Dismiss", listener)
            .setView(R.layout.dialog_currency_list)
            .create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    companion object {
        val TAG = ChooseCurrencyDialogFragment::class.java.simpleName
        val REQUEST_KEY = "$TAG:request"
        val KEY_RESPONSE = "RESPONSE"
    }
}