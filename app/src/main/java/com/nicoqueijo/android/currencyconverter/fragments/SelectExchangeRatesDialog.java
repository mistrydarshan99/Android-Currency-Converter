package com.nicoqueijo.android.currencyconverter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.adapters.SelectExchangeRatesRecyclerViewAdapter;
import com.nicoqueijo.android.currencyconverter.models.Currency;

import java.util.ArrayList;
import java.util.List;


public class SelectExchangeRatesDialog extends DialogFragment {

    public static final String TAG = SelectExchangeRatesDialog.class.getSimpleName();
    private static final String ARG_CURRENCIES = "currencies";

    List<Currency> mCurrencies;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar mToolbar;
    private SearchView mSearchView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrencies = getArguments().getParcelableArrayList(ARG_CURRENCIES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_exchange_rate, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view_select_rates);
        mToolbar = view.findViewById(R.id.toolbar_search);
        mToolbar.inflateMenu(R.menu.menu_search);
        mSearchView = (SearchView) mToolbar.getMenu().getItem(0).getActionView();

        mAdapter = new SelectExchangeRatesRecyclerViewAdapter(getContext(), mCurrencies);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        return view;
    }

    public static SelectExchangeRatesDialog newInstance(ArrayList<Currency> currencies) {
        SelectExchangeRatesDialog selectExchangeRatesDialog = new SelectExchangeRatesDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_CURRENCIES, currencies);
        selectExchangeRatesDialog.setArguments(args);
        return selectExchangeRatesDialog;
    }

}