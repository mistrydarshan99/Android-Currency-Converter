package com.nicoqueijo.android.currencyconverter.kotlin.view

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nicoqueijo.android.currencyconverter.R
import com.nicoqueijo.android.currencyconverter.kotlin.adapter.SelectableCurrenciesAdapter_kt


// Highlight 'Converter' in menu when we are in this Fragment
class SelectableCurrenciesFragment_kt : Fragment() {

    lateinit var toolbar: Toolbar
    lateinit var adapter: SelectableCurrenciesAdapter_kt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        toolbar = activity!!.findViewById(R.id.toolbar_kt)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_selectable_currency_kt, container, false)
        initAdaptersAndListeners(view)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        initMenu(menu, inflater)
    }

    // This might not be needed. Test to confirm.
//    override fun onDestroyView() {
//        super.onDestroyView()
//        toolbar.menu.removeItem(R.id.search)
//    }

    private fun initAdaptersAndListeners(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_selectable_currencies_kt)
//        val dragScrollBar: DragScrollBar = view.findViewById(R.id.drag_scroll_bar_kt)
//        dragScrollBar.setIndicator(AlphabetIndicator(context), true)
        adapter = SelectableCurrenciesAdapter_kt(context, MainActivity_kt.currencies)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context,
                DividerItemDecoration.VERTICAL))
    }

    private fun initMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_kt, menu)
        val searchView = menu.findItem(R.id.search_kt).actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_GO
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                adapter.getFilter().filter(newText)
                return false
            }
        })
    }

}