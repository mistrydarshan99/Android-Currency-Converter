package com.nicoqueijo.android.currencyconverter.java.interfaces;

import com.nicoqueijo.android.currencyconverter.java.models.Currency;

/**
 * Used to communicate between Fragments. When a currency is selected in the
 * SelectableCurrenciesFragment this interface is used to notify the ActiveCurrenciesFragment
 * so it can be added to its list. This is done via the MainActivity.
 */
@FunctionalInterface
public interface ICommunicator {
    void passSelectedCurrency(Currency currency);
}