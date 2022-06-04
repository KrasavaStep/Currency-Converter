package com.example.currencyconverter.di

import android.app.Application
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.currencyconverter.data.db.CurrencyDao
import com.example.currencyconverter.data.db.CurrencyDatabase
import org.koin.dsl.module

fun provideDatabase(application: Application): CurrencyDatabase {
    return Room.databaseBuilder(application, CurrencyDatabase::class.java, "currency_db")
        .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
        .build()}


fun provideDao(db: CurrencyDatabase): CurrencyDao {
    return db.getCurrencyDao()}


val currencyDbModule = module {
    single { provideDatabase(get()) }
    single { provideDao(get()) }

}