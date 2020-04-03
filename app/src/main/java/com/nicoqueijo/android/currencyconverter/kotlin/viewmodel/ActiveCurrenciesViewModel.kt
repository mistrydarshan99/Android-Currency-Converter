package com.nicoqueijo.android.currencyconverter.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.nicoqueijo.android.currencyconverter.kotlin.data.Repository
import com.nicoqueijo.android.currencyconverter.kotlin.model.Currency
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.FIRST
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.FOURTH
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.SECOND
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.THIRD
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hasMoreThanOneElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.*
import kotlin.properties.Delegates

class ActiveCurrenciesViewModel(application: Application) : AndroidViewModel(application) {

    // Candidate for dependency injection
    private val repository = Repository(application)

    private val _activeCurrencies = repository.getActiveCurrencies()
    val activeCurrencies: LiveData<MutableList<Currency>>
        get() = _activeCurrencies

    private fun upsertCurrency(currency: Currency?) = repository.upsertCurrency(currency)

    private suspend fun getCurrency(currencyCode: String) = repository.getCurrency(currencyCode)

    private fun isFirstLaunch() = repository.isFirstLaunch
    private fun setFirstLaunch(value: Boolean) {
        repository.isFirstLaunch = value
    }

    var adapterActiveCurrencies = mutableListOf<Currency>()
    var focusedCurrency: Currency? = null

    private lateinit var swipedCurrency: Currency
    private var swipedCurrencyOrder by Delegates.notNull<Int>()

    fun handleSwipe(position: Int): Int {
        val indexToRefresh = reassignFocusedCurrency(position)
        shiftCurrencies(position)
        return indexToRefresh
    }

    /**
     * Here we are setting the focused to the first currency in the list
     * if the swiped currency was focused.
     */
    private fun reassignFocusedCurrency(position: Int): Int {
        swipedCurrency = adapterActiveCurrencies[position]
        if (focusedCurrency == swipedCurrency) {
            val newlyFocusedCurrency: Currency
            if (position == 0) {
                if (adapterActiveCurrencies.hasMoreThanOneElement()) {
                    newlyFocusedCurrency = adapterActiveCurrencies[1] // SECOND
                    newlyFocusedCurrency.isFocused = true
                    swipedCurrency.isFocused = false
                    focusedCurrency = newlyFocusedCurrency
                    return 1
                } else {
                    focusedCurrency = null
                }
            } else {
                newlyFocusedCurrency = adapterActiveCurrencies[0] // FIRST
                newlyFocusedCurrency.isFocused = true
                swipedCurrency.isFocused = false
                focusedCurrency = newlyFocusedCurrency
                return 0
            }
        }
        return -1
    }

    private fun shiftCurrencies(position: Int) {
        swipedCurrency = adapterActiveCurrencies[position]
        swipedCurrencyOrder = swipedCurrency.order
//        val conversionValue = swipedCurrency.conversionValue
        run loop@{
            adapterActiveCurrencies
                    .reversed()
                    .forEach { currency ->
                        if (currency.order == swipedCurrencyOrder) {
                            return@loop
                        }
                        currency.order--
                        upsertCurrency(currency)
                    }
        }
        swipedCurrency.isSelected = false
        swipedCurrency.order = -1
        swipedCurrency.conversionValue = BigDecimal(0.0)
        upsertCurrency(swipedCurrency)
    }

    /**
     * Makes the passed currency focused and the previously focused currency unfocused.
     * If the previously focused currency was swiped it will not be in the list and the index will
     * return -1.
     */
    fun changeFocusedCurrency(newlyFocusedCurrency: Currency): Int {
        val indexOfPreviouslyFocusedCurrency = adapterActiveCurrencies.indexOf(focusedCurrency)
        if (indexOfPreviouslyFocusedCurrency.isValid()) {
            adapterActiveCurrencies[indexOfPreviouslyFocusedCurrency].isFocused = false
        }
        focusedCurrency = newlyFocusedCurrency
        newlyFocusedCurrency.isFocused = true
        return indexOfPreviouslyFocusedCurrency
    }

    fun handleSwipeUndo() {
//        swipedCurrency.conversionValue = conversionValue
        swipedCurrency.isSelected = true
        swipedCurrency.order = swipedCurrencyOrder
        upsertCurrency(swipedCurrency)
        for (i in swipedCurrencyOrder until adapterActiveCurrencies.size) {
            val currency = adapterActiveCurrencies[i]
            currency.order++
            upsertCurrency(currency)
        }
    }

    fun handleMove(oldPosition: Int, newPosition: Int) {
        swapCurrencies(oldPosition, newPosition)
    }

    fun handleDrop() {
        adapterActiveCurrencies.forEach {
            upsertCurrency(it)
        }
    }

    private fun swapCurrencies(oldPosition: Int, newPosition: Int) {
        adapterActiveCurrencies[oldPosition].order = adapterActiveCurrencies[newPosition].order.also {
            adapterActiveCurrencies[newPosition].order = adapterActiveCurrencies[oldPosition].order
        }
        adapterActiveCurrencies[oldPosition] = adapterActiveCurrencies[newPosition].also {
            adapterActiveCurrencies[newPosition] = adapterActiveCurrencies[oldPosition]
        }
    }

    fun populateDefaultCurrencies() {
        if (!isFirstLaunch()) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                setFirstLaunch(false)
                val localCurrencyCode = "USD_${java.util.Currency.getInstance(Locale.getDefault()).currencyCode}"
                getCurrency("USD_USD")?.run {
                    setDefaultCurrency(this, FIRST)
                }
                val localCurrency = getCurrency(localCurrencyCode)
                if (localCurrencyCode == "USD_USD" || localCurrency == null) {
                    getCurrency("USD_EUR")?.run {
                        setDefaultCurrency(this, SECOND)
                    }
                    getCurrency("USD_JPY")?.run {
                        setDefaultCurrency(this, THIRD)
                    }
                    getCurrency("USD_GBP")?.run {
                        setDefaultCurrency(this, FOURTH)
                    }
                } else {
                    localCurrency.run {
                        setDefaultCurrency(this, SECOND)
                    }
                }
            }
        }
    }

    private fun setDefaultCurrency(currency: Currency, order: Int) {
        currency.order = order
        currency.isSelected = true
        upsertCurrency(currency)
    }
}