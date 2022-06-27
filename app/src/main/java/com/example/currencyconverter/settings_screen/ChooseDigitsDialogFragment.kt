package com.example.currencyconverter.settings_screen

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.example.currencyconverter.MainActivity
import com.example.currencyconverter.R
import com.example.currencyconverter.databinding.DialogChooseDigitsBinding

class ChooseDigitsDialogFragment(private val listener: DialogButtonsClickedListener) :
    DialogFragment(R.layout.dialog_choose_digits) {

    interface DialogButtonsClickedListener : DialogInterface {
        fun onSubmitBtnClick(value: String)
    }

    private var _binding: DialogChooseDigitsBinding? = null
    private val binding
        get() = _binding!!

    private var prefs: SharedPreferences? = null
    private var prefValue = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentOrientation = requireActivity().resources.configuration.orientation
        requireActivity().requestedOrientation = currentOrientation

        _binding = DialogChooseDigitsBinding.bind(view)

        prefs = activity?.getSharedPreferences(MainActivity.PREF_DECIMAL_NAME, Context.MODE_PRIVATE)
        prefs?.let {
            prefValue = it.getInt(
                MainActivity.DECIMAL_DIGITS_KEY,
                MainActivity.DEF_DECIMAL_VALUE
            )
        }

        val radioGr = view.findViewById<RadioGroup>(R.id.radio_group)
        radioGr.check(prefValue - 1)

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        binding.buttonSubmit.setOnClickListener {
            val selectedRB = binding.radioGroup.checkedRadioButtonId
            val radio = view.findViewById<RadioButton>(selectedRB)
            val result = radio.text.toString()[0]
            listener.onSubmitBtnClick(result.toString())
            dismiss()
        }
    }
}