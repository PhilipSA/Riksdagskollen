package oscar.riksdagskollen.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.Callbacks.CurrentNewsCallback;
import oscar.riksdagskollen.Utilities.Callbacks.PartyDocumentCallback;
import oscar.riksdagskollen.Utilities.CurrentNewsListAdapter;
import oscar.riksdagskollen.Utilities.JSONModels.CurrentNews;
import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;
import oscar.riksdagskollen.Utilities.PartyListViewholderAdapter;
import oscar.riksdagskollen.Utilities.RiksdagenViewHolderAdapter;


/**
 * Created by oscar on 2018-03-29.
 */

public class CurrentNewsListFragment extends RiksdagenAutoLoadingListFragment {
    private List<CurrentNews> newsList = new ArrayList<>();

    public static CurrentNewsListFragment newInstance(){
        CurrentNewsListFragment newInstance = new CurrentNewsListFragment();
        return newInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAdapter(new CurrentNewsListAdapter(newsList));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.news);

    }

    /**
     * Load the next page and add it to the adapter when downloaded and parsed.
     * Hides the loading view.
     */
    protected void loadNextPage(){
        setLoadingMoreItems(true);
        RikdagskollenApp.getInstance().getRiksdagenAPIManager().getCurrentNews( new CurrentNewsCallback() {
            @Override
            public void onNewsFetched(List<CurrentNews> currentNews) {
                setShowLoadingView(false);
                newsList.addAll(currentNews);
                getAdapter().notifyDataSetChanged();
                setLoadingMoreItems(false);
            }

            @Override
            public void onFail(VolleyError error) {
                setLoadingMoreItems(false);
                decrementPage();
            }
        });
        incrementPage();
    }


}
