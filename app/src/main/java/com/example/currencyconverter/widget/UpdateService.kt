package com.example.currencyconverter.widget

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import com.example.currencyconverter.MainActivity
import com.example.currencyconverter.R


class UpdateService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // generates random number
        val prefs = this.getSharedPreferences(MainActivity.PREF_WIDGET_NAME, Context.MODE_PRIVATE)
        val usdRub = prefs.getFloat(MainActivity.PREF_WIDGET_KEY1, MainActivity.DEF_VAL_WIDGET)
        val usdEur = prefs.getFloat(MainActivity.PREF_WIDGET_KEY2, MainActivity.DEF_VAL_WIDGET)
        val eurRub = prefs.getFloat(MainActivity.PREF_WIDGET_KEY3, MainActivity.DEF_VAL_WIDGET)
        // Reaches the view on widget and displays the number
        val view = RemoteViews(packageName, R.layout.currency_widget)
        view.setTextViewText(R.id.usd_rub, String.format("%.1f", usdRub) + " RUB")
        view.setTextViewText(R.id.usd_eur, String.format("%.1f", usdEur) + " EUR")
        view.setTextViewText(R.id.eur_rub, String.format("%.1f", eurRub) + " RUB")
        val theWidget = ComponentName(this, NewAppWidget::class.java)
        val manager = AppWidgetManager.getInstance(this)
        manager.updateAppWidget(theWidget, view)
        return super.onStartCommand(intent, flags, startId)
    }
}