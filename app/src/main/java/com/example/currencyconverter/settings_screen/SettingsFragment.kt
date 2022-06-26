package com.example.currencyconverter.settings_screen

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import com.example.currencyconverter.MainActivity
import com.example.currencyconverter.R
import com.example.currencyconverter.databinding.FragmentCurrencyListBinding
import com.example.currencyconverter.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding
        get() = _binding!!

    private var prefs: SharedPreferences? = null
    private var prefsThemeMode: SharedPreferences? = null
    private var prefValue = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        prefs = activity?.getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE)
        prefs?.let {
            prefValue = it.getInt(MainActivity.DECIMAL_DIGITS_KEY, MainActivity.DEF_VALUE)
        }

        prefsThemeMode =
            activity?.getSharedPreferences(MainActivity.PREF_NIGHT, Context.MODE_PRIVATE)
        prefsThemeMode?.let {
            binding.switchTheme.isChecked =
                it.getBoolean(MainActivity.PREF_NIGHT_KEY, MainActivity.PREF_NIGHT_VAL)
        }

        binding.numberText.text = prefValue.toString()
        val dialog = ChooseDigitsDialogFragment(object :
            ChooseDigitsDialogFragment.DialogButtonsClickedListener {
            override fun onSubmitBtnClick(value: String) {
                prefs?.edit()?.putInt(MainActivity.DECIMAL_DIGITS_KEY, value.toInt())?.apply()
                binding.numberText.text = value
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }

            override fun cancel() {}
            override fun dismiss() {}
        })

        binding.decimalLayout.setOnClickListener {
            dialog.show(parentFragmentManager, TAG)
        }

        binding.switchTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = isChecked
                prefsThemeMode?.edit()?.putBoolean(MainActivity.PREF_NIGHT_KEY, isChecked)?.apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = isChecked
                prefsThemeMode?.edit()?.putBoolean(MainActivity.PREF_NIGHT_KEY, isChecked)?.apply()
            }
        }

    }

    companion object {
        private val TAG = "choose_digits_dialog"
    }
}