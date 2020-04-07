package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.adapter.ActiveCurrenciesAdapter
import com.nicoqueijo.android.currencyconverter.kotlin.util.CurrencyConversion
import com.nicoqueijo.android.currencyconverter.kotlin.util.SwipeAndDragHelper
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.roundToFourDecimalPlaces
import com.nicoqueijo.android.currencyconverter.kotlin.viewmodel.ActiveCurrenciesViewModel
import java.math.BigDecimal


class ActiveCurrenciesFragment : Fragment() {

    private lateinit var viewModel: ActiveCurrenciesViewModel

    private lateinit var recyclerView: CustomRecyclerView
    private lateinit var adapter: ActiveCurrenciesAdapter
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var keyboard: DecimalNumberKeyboard

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_active_currencies, container, false)
        viewModel = ViewModelProvider(this).get(ActiveCurrenciesViewModel::class.java)
        initViewsAndAdapter(view)
        observeObservables()
        /*populateDefaultCurrencies()*/
        return view
    }

    private fun initViewsAndAdapter(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view_active_currencies)
        val emptyListView = view.findViewById<View>(R.id.empty_list)
        keyboard = view.findViewById(R.id.keyboard)
        recyclerView.showIfEmpty(emptyListView)
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        initFloatingActionButton(view)
        adapter = ActiveCurrenciesAdapter(viewModel, keyboard)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        val itemTouchHelperCallback = SwipeAndDragHelper(adapter, 0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    private fun observeObservables() {
        viewModel.activeCurrencies.observe(viewLifecycleOwner, Observer { currencies ->
            adapter.setCurrencies(currencies)
            if (currencies.isEmpty()) {
                // TODO: hide keyboard
            } else {
                // TODO: show keyboard
            }
        })
        viewModel.focusedCurrency.observe(viewLifecycleOwner, Observer { focusedCurrency ->
            focusedCurrency?.let {
                /**
                 * Only do this when TextView is empty to avoid unnecessary work.
                 * Hints won't display while TextView has text so why bother computing it?
                 */
                focusedCurrency.conversion.conversionHint = "1"
                recyclerView.post {
                    viewModel.adapterActiveCurrencies
                            .filter { it != focusedCurrency }
                            .forEach {
                                val fromRate = focusedCurrency.exchangeRate
                                val toRate = it.exchangeRate
                                val converterHint = CurrencyConversion.convertCurrency(BigDecimal("1"), fromRate, toRate).roundToFourDecimalPlaces()
                                it.conversion.conversionHint = converterHint.toString()
                                adapter.notifyItemChanged(viewModel.adapterActiveCurrencies.indexOf(it))
                            }
                }
                recyclerView.smoothScrollToPosition(viewModel.adapterActiveCurrencies.indexOf(focusedCurrency))
            }
        })
    }

    private fun initFloatingActionButton(view: View) {
        floatingActionButton = view.findViewById(R.id.floating_action_button)
        floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_activeCurrenciesFragment_to_selectableCurrenciesFragment)
        }
    }

    private fun populateDefaultCurrencies() {
        viewModel.populateDefaultCurrencies()
    }
}
