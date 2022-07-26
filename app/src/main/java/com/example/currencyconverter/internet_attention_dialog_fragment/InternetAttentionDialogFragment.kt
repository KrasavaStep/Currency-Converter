package com.example.currencyconverter.internet_attention_dialog_fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.currencyconverter.R

class InternetAttentionDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = DialogInterface.OnClickListener { _, which ->
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(KEY_RESPONSE to which))
        }
        return AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setTitle(R.string.internet_attention_dialog_title)
            .setNegativeButton(R.string.internet_attention_negative_btn_text, listener)
            .setView(R.layout.dialog_internet_attention)
            .create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    companion object {
        val TAG = InternetAttentionDialogFragment::class.java.simpleName
        val REQUEST_KEY = "$TAG:request"
        val KEY_RESPONSE = "RESPONSE"
    }
}