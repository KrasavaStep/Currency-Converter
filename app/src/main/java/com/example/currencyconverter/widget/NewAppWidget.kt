package com.example.currencyconverter.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.example.currencyconverter.MainActivity
import com.example.currencyconverter.R

class NewAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        val mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
        if (actionRefresh == intent?.action) {
            updateAppWidget(context, AppWidgetManager.getInstance(context), mAppWidgetId)
        }
    }

    companion object {
        const val actionRefresh = "refresh"
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val prefs = context.getSharedPreferences(MainActivity.PREF_WIDGET_NAME, Context.MODE_PRIVATE)
    val views = RemoteViews(context.packageName, R.layout.new_app_widget)
    views.setTextViewText(
        R.id.usd_rub,
        prefs.getFloat(MainActivity.PREF_WIDGET_KEY1, MainActivity.DEF_VAL_WIDGET).toString()
    )
    views.setTextViewText(
        R.id.usd_eur,
        prefs.getFloat(MainActivity.PREF_WIDGET_KEY2, MainActivity.DEF_VAL_WIDGET).toString()
    )
    views.setTextViewText(
        R.id.eur_rub,
        prefs.getFloat(MainActivity.PREF_WIDGET_KEY3, MainActivity.DEF_VAL_WIDGET).toString()
    )

    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.data = Uri.parse("https://belarusbank.by/ru/fizicheskim_licam/valuta/kursy-valyut")

    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    views.setOnClickPendingIntent(R.id.bank_btn, pendingIntent)

    views.setOnClickPendingIntent(
        R.id.refresh,
        getPendingSelfIntent(context, NewAppWidget.actionRefresh)
    )

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

fun getPendingSelfIntent(context: Context?, action: String?): PendingIntent? {
    val intent = Intent(context, NewAppWidget::class.java)
    intent.action = action
    return PendingIntent.getBroadcast(context, 0, intent, 0)
}
