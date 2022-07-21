package com.example.currencyconverter.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.currencyconverter.R
import com.example.currencyconverter.data.WidgetRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UpdateWidgetWorker(private val context: Context, params: WorkerParameters):
    CoroutineWorker(context, params), KoinComponent {

    private val repository: WidgetRepository by inject()

    override suspend fun doWork(): Result {
        val appWidgetIds = inputData.getIntArray("ids")
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)


        return try {

            val result1 = repository.getUSDtoRUB()
            val result2 = repository.getUSDtoEUR()
            val result3 = repository.getEURtoRUB()


            appWidgetIds?.forEach { id ->
                val views = RemoteViews(applicationContext.packageName, R.layout.currency_widget)

                views.setTextViewText(
                    R.id.usd_rub,
                    String.format("%.1f", result1.conversion_rate) + "RUB"
                )
                views.setTextViewText(
                    R.id.usd_eur,
                    String.format("%.1f",result2.conversion_rate) + "EUR"
                )
                views.setTextViewText(
                    R.id.eur_rub,
                    String.format("%.1f",result3.conversion_rate) + "RUB"
                )

                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.parse("https://belarusbank.by/ru/fizicheskim_licam/valuta/kursy-valyut")

                val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
                views.setOnClickPendingIntent(R.id.bank_btn, pendingIntent)
                appWidgetManager.updateAppWidget(id, views)
            }

            Result.success()
        } catch (ex: Exception){
            Result.failure()
        }
    }
}