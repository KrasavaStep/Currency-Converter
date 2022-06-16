package com.example.currencyconverter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.currencyconverter.databinding.ActivityMainBinding
import com.example.currencyconverter.stock_market_screen.StockMarketFragment
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    var networkConnection: NetworkConnection? = null

    private lateinit var listener: NavController.OnDestinationChangedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        val drawerLayout = binding.drawerLayout
        binding.navigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.currencyListFragment -> {
                    supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(com.google.android.material.R.color.design_dark_default_color_primary)))
                }
                R.id.cryptocurrencyFragment -> {
                    supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(R.color.list_item_bg_color)))
                }
                R.id.stockMarketFragment -> {
                    supportActionBar?.setBackgroundDrawable(ColorDrawable(getColor(R.color.main_color)))
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
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        super.onPause()
        navController.addOnDestinationChangedListener(listener)
    }

    companion object {
        private const val OFFLINE_VAL = "Offline"
        private const val ONLINE_VAL = "Online"
    }
}