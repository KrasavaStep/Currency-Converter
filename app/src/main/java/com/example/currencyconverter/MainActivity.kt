package com.example.currencyconverter

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.currencyconverter.internet_attention_dialog_fragment.InternetAttentionDialogFragment
import com.example.currencyconverter.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    var networkConnection: NetworkConnection? = null

    private lateinit var listener: NavController.OnDestinationChangedListener
    private lateinit var prefsSettings: SharedPreferences
    private lateinit var prefsThemeMode: SharedPreferences
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences(PREF_FIRST_LAUNCH_NAME, MODE_PRIVATE)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        val drawerLayout = binding.drawerLayout
        binding.navigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        listener =
            NavController.OnDestinationChangedListener { controller, destination, arguments ->
                when (destination.id) {
                    R.id.currencyListFragment -> {
                        supportActionBar?.setTitle(CURRENCY_LIST_TITLE)
                    }
                    R.id.cryptocurrencyFragment -> {
                        supportActionBar?.setTitle(CRYPTO_LIST_TITLE)
                    }
                    R.id.cryptoGraphicFragment -> {
                        supportActionBar?.setTitle(CRYPTO_GRAPHIC_TITLE)
                    }
                    R.id.settingsFragment -> {
                        supportActionBar?.setTitle(SETTINGS_TITLE)
                    }
                }
            }

        networkConnection = NetworkConnection(this)
        networkConnection?.observe(this) {
            if (it) {
                Snackbar.make(binding.root, ONLINE_VAL, Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.root, OFFLINE_VAL, Snackbar.LENGTH_SHORT).show()
            }
        }

        prefsSettings = getSharedPreferences(PREF_DECIMAL_NAME, Context.MODE_PRIVATE)
        prefsSettings.getInt(DECIMAL_DIGITS_KEY, DEF_DECIMAL_VALUE)

        prefsThemeMode = getSharedPreferences(PREF_NIGHT_NAME, Context.MODE_PRIVATE)
        val isNight = prefsThemeMode.getBoolean(PREF_NIGHT_KEY, PREF_NIGHT_VAL)
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
        if (prefs.getBoolean(PREF_FIRST_LAUNCH_KEY, true)) {
            setupInternetAttentionDialog()
            showInternetAttentionDialog()
            prefs.edit().putBoolean(PREF_FIRST_LAUNCH_KEY, false).apply()
        }
    }

    override fun onPause() {
        super.onPause()
        navController.addOnDestinationChangedListener(listener)
    }

    private fun showInternetAttentionDialog() {
        val dialogFragment = InternetAttentionDialogFragment()
        dialogFragment.show(supportFragmentManager, InternetAttentionDialogFragment.TAG)
    }

    private fun setupInternetAttentionDialog() {
        supportFragmentManager.setFragmentResultListener(
            InternetAttentionDialogFragment.REQUEST_KEY,
            this,
            FragmentResultListener { _, result ->
                when (result.getInt(InternetAttentionDialogFragment.KEY_RESPONSE)) {
                    DialogInterface.BUTTON_POSITIVE ->
                        Snackbar.make(binding.root, HELLO_MSG, Snackbar.LENGTH_SHORT).show()
                }
            })
    }



    companion object {
        private const val OFFLINE_VAL = R.string.offline
        private const val ONLINE_VAL = R.string.Online
        private const val HELLO_MSG = R.string.hello_msg

        private const val PREF_FIRST_LAUNCH_NAME = "com.example.currencyconverter"
        private const val PREF_FIRST_LAUNCH_KEY = "firstrun"

        const val PREF_DECIMAL_NAME = "app_prefs"
        const val DECIMAL_DIGITS_KEY = "decimal_digits"
        const val DEF_DECIMAL_VALUE = 3

        const val PREF_NIGHT_NAME = "night"
        const val PREF_NIGHT_KEY = "night_mode"
        const val PREF_NIGHT_VAL = false

        const val PREF_WIDGET_NAME = "widget"
        const val PREF_WIDGET_KEY1 = "usd_to_rub"
        const val PREF_WIDGET_KEY2 = "usd_to_eur"
        const val PREF_WIDGET_KEY3 = "eur_to_rub"
        const val DEF_VAL_WIDGET = 1f

        private var CURRENCY_LIST_TITLE = R.string.currency_list_title
        private var CRYPTO_LIST_TITLE = R.string.crypto_title
        private var CRYPTO_GRAPHIC_TITLE = R.string.crypto_chart
        private var SETTINGS_TITLE = R.string.setting_title
    }
}