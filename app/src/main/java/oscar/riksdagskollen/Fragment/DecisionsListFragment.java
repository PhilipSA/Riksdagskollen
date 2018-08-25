package oscar.riksdagskollen.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Util.Adapter.DecisionListAdapter;
import oscar.riksdagskollen.Util.Callback.DecisionsCallback;
import oscar.riksdagskollen.Util.JSONModel.DecisionDocument;
import oscar.riksdagskollen.Util.Adapter.RiksdagenViewHolderAdapter;

/**
 * Created by gustavaaro on 2018-06-16.
 */

public class DecisionsListFragment extends RiksdagenAutoLoadingListFragment {

    private List<DecisionDocument> decisionDocuments = new ArrayList<>();
    private List<DecisionDocument> searchedDocuments = new ArrayList<>();
    private RiksdagenViewHolderAdapter searchAdapter;
    private DecisionListAdapter adapter;
    private MenuItem menuItem;
    private SearchView searchView;
    private String currentQuery = "";

    public static DecisionsListFragment newInstance(){
        DecisionsListFragment newInstance = new DecisionsListFragment();
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.decisions);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new DecisionListAdapter(decisionDocuments, new RiksdagenViewHolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object document) {

            }
        }, getRecyclerView());

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) menuItem.getActionView();

        menuItem.setVisible(true);
        searchView.setQueryHint("Sök efter beslut...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!currentQuery.equals(query)) resetSearchPage();

                currentQuery = query;

                if (!isSearching()){
                    getRecyclerView().swapAdapter(adapter,false);
                } else {
                    setShowLoadingView(true);
                    RikdagskollenApp.getInstance().getRiksdagenAPIManager().searchForDecision(new DecisionsCallback() {
                        @Override
                        public void onDecisionsFetched(List<DecisionDocument> decisions) {
                            searchedDocuments.clear();
                            searchedDocuments.addAll(decisions);
                            searchAdapter = new DecisionListAdapter(searchedDocuments, new RiksdagenViewHolderAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Object document) {

                                }
                            }, getRecyclerView());
                            setShowLoadingView(false);
                            getRecyclerView().swapAdapter(searchAdapter,false);
                        }

                        @Override
                        public void onFail(VolleyError error) {
                            setShowLoadingView(false);
                            Toast.makeText(getContext(),"Hittade inga sökresultat", Toast.LENGTH_LONG).show();
                        }
                    }, query, getSearchPageToLoad());
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                if(s.isEmpty()) onQueryTextSubmit(s);
                return false;
            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
        if(menuItem != null) menuItem.setVisible(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        menuItem.setVisible(false);
    }



    @Override
    protected void loadNextPage() {
        setLoadingMoreItems(true);
        if(isSearching()){
           loadMoreSearchItems();
           incrementSearchPage();
        }
        else {
         loadMoreItems();
         incrementPage();
        }

    }

    private boolean isSearching(){
        return !currentQuery.isEmpty();
    }

    private void loadMoreSearchItems(){
        RikdagskollenApp.getInstance().getRiksdagenAPIManager().searchForDecision(new DecisionsCallback() {
            @Override
            public void onDecisionsFetched(List<DecisionDocument> decisions) {
                setShowLoadingView(false);
                searchedDocuments.addAll(decisions);
                getAdapter().notifyDataSetChanged();
                setLoadingMoreItems(false);
            }

            @Override
            public void onFail(VolleyError error) {
                setLoadingMoreItems(false);
                decrementSearchPage();
            }
        }, currentQuery, getSearchPageToLoad());
    }

    private void loadMoreItems(){
        RikdagskollenApp.getInstance().getRiksdagenAPIManager().getDecisions(new DecisionsCallback() {
            @Override
            public void onDecisionsFetched(List<DecisionDocument> documents) {
                setShowLoadingView(false);
                decisionDocuments.addAll(documents);
                getAdapter().notifyDataSetChanged();
                setLoadingMoreItems(false);
            }

            @Override
            public void onFail(VolleyError error) {
                setLoadingMoreItems(false);
                decrementPage();
            }
        }, getPageToLoad());
    }


    @Override
    RiksdagenViewHolderAdapter getAdapter() {
        if(isSearching()) return searchAdapter;
        return adapter;
    }
}
