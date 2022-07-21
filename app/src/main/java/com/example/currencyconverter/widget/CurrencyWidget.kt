package com.example.currencyconverter.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.currencyconverter.MainActivity
import com.example.currencyconverter.R
import java.util.concurrent.TimeUnit


class NewAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
//        for (appWidgetId in appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId)
//        }
//
//        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val i = Intent(context, UpdateService::class.java)
//
//        if (service == null) {
//            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT)
//        }
//        manager.setRepeating(
//            AlarmManager.ELAPSED_REALTIME,
//            SystemClock.elapsedRealtime(),
//            60000,
//            service
//        )
        val inputData = Data.Builder().putIntArray("ids", appWidgetIds).build()
        val workRequest = PeriodicWorkRequestBuilder<UpdateWidgetWorker>(5, TimeUnit.HOURS)
            .setInputData(inputData).build()
        WorkManager.getInstance(context.applicationContext).enqueue(workRequest)
    }

}

//internal fun updateAppWidget(
//    context: Context,
//    appWidgetManager: AppWidgetManager,
//    appWidgetId: Int
//) {
//    val prefs = context.getSharedPreferences(MainActivity.PREF_WIDGET_NAME, Context.MODE_PRIVATE)
//
//    val views = RemoteViews(context.packageName, R.layout.currency_widget)
//    views.setTextViewText(
//        R.id.usd_rub,
//        String.format("%.1f",prefs.getFloat(MainActivity.PREF_WIDGET_KEY1, MainActivity.DEF_VAL_WIDGET)) + "RUB"
//    )
//    views.setTextViewText(
//        R.id.usd_eur,
//        String.format("%.1f",prefs.getFloat(MainActivity.PREF_WIDGET_KEY2, MainActivity.DEF_VAL_WIDGET)) + "EUR"
//    )
//    views.setTextViewText(
//        R.id.eur_rub,
//        String.format("%.1f",prefs.getFloat(MainActivity.PREF_WIDGET_KEY3, MainActivity.DEF_VAL_WIDGET)) + "RUB"
//    )
//
//    val intent = Intent(Intent.ACTION_VIEW)
//    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//    intent.data = Uri.parse("https://belarusbank.by/ru/fizicheskim_licam/valuta/kursy-valyut")
//
//    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
//    views.setOnClickPendingIntent(R.id.bank_btn, pendingIntent)
//
//    appWidgetManager.updateAppWidget(appWidgetId, views)
//}
