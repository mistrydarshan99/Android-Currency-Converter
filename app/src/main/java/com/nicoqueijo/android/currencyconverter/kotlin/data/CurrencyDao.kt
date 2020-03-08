package com.nicoqueijo.android.currencyconverter.kotlin.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(currency: Currency?)

    @Query("SELECT * FROM currency ORDER BY currency_code ASC")
    fun getAllCurrencies(): LiveData<MutableList<Currency>>

    @Query("SELECT * FROM currency WHERE is_selected = 1 ORDER BY 'order' ASC")
    fun getActiveCurrencies(): LiveData<MutableList<Currency>>
}
