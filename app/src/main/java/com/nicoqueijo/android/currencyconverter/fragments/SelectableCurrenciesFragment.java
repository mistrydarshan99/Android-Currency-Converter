package com.nicoqueijo.android.currencyconverter.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import com.nicoqueijo.android.currencyconverter.R;
import com.nicoqueijo.android.currencyconverter.activities.MainActivity;
import com.nicoqueijo.android.currencyconverter.adapters.SelectableCurrenciesAdapter;
import com.nicoqueijo.android.currencyconverter.interfaces.ICommunicator;
import com.nicoqueijo.android.currencyconverter.models.Currency;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

/**
 * Fragment used to search, filter, and add exchange rates to the ActiveCurrenciesFragment.
 */
public class SelectableCurrenciesFragment extends Fragment {

    public static final String TAG = SelectableCurrenciesFragment.class.getSimpleName();

    private MainActivity mHostingActivity;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private SelectableCurrenciesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DragScrollBar mDragScrollBar;

    /**
     * Factory method to create a new instance of this Fragment using the provided parameters.
     *
     * @return a new instance of Fragment
     */
    public static SelectableCurrenciesFragment newInstance() {
        return new SelectableCurrenciesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selectable_currency, container, false);
        initViewsAdaptersAndListeners(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        initMenu(menu, inflater);
    }

    /**
     * Hides the keyboard when app returns to focus. The keyboard pops up in the first place
     * because the Fragment under this one has a list of EditTexts and when it is started again
     * one of the EditText's will have the focus and force the keyboard to show.
     */
    @Override
    public void onStart() {
        super.onStart();
        mHostingActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Removes the Search menu item when this Fragment's view is destroyed as it is no longer
     * needed and would be irrelevant when other Fragment's take over the content frame.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mToolbar.getMenu().removeItem(R.id.search);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the reference to the hosting activity and the toolbar. Notifies this Fragment that it
     * has a menu.
     */
    private void setUpFragment() {
        setHasOptionsMenu(true);
        mHostingActivity = (MainActivity) getActivity();
        mToolbar = mHostingActivity.findViewById(R.id.toolbar);
    }

    /**
     * Initializes the views and sets up the adapters.
     *
     * @param view the root view of the inflated hierarchy
     */
    private void initViewsAdaptersAndListeners(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_selectable_currencies);
        mDragScrollBar = view.findViewById(R.id.drag_scroll_bar);
        mDragScrollBar.setIndicator(new AlphabetIndicator(getContext()), true);
        mAdapter = new SelectableCurrenciesAdapter(this);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    /**
     * Sets up the menu for this Fragment.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater used to instantiate menu XML files into Menu objects.
     */
    public void initMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_GO);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    /**
     * Passes the newly selected currency to the ActiveCurrenciesFragment via an interface.
     *
     * @param currency the new currency that was selected
     */
    public void sendActiveCurrency(Currency currency) {
        ICommunicator communicator = (ICommunicator) getActivity();
        communicator.passSelectedCurrency(currency);
    }
}
