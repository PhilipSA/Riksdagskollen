package oscar.riksdagskollen.Fragments;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Activities.DocumentReaderActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.Callbacks.CurrentNewsCallback;
import oscar.riksdagskollen.Utilities.Callbacks.PartyDocumentCallback;
import oscar.riksdagskollen.Utilities.CurrentNewsListAdapter;
import oscar.riksdagskollen.Utilities.JSONModels.CurrentNews;
import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;
import oscar.riksdagskollen.Utilities.PartyListViewholderAdapter;


/**
 * Created by oscar on 2018-03-29.
 */

public class CurrentNewsListFragment extends Fragment {
    int pageToLoad = 1;
    private boolean loading = false;
    private List<CurrentNews> newsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private int pastVisiblesItems;
    private CurrentNewsListAdapter currentNewsListAdapter;


    public static CurrentNewsListFragment newInstance(){
        CurrentNewsListFragment newInstance = new CurrentNewsListFragment();
        return newInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.news);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_party_list,null);
        currentNewsListAdapter = new CurrentNewsListAdapter(newsList);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(currentNewsListAdapter);
        recyclerView.setNestedScrollingEnabled(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        if(!loading) loadNextPage();
                    }

                }
            }
        });
        loadNextPage();
        return view;
    }


    private void loadNextPage(){
        loading = true;

        RikdagskollenApp.getInstance().getRiksdagenAPIManager().getCurrentNews(new CurrentNewsCallback() {

            @Override
            public void onNewsFetched(List<CurrentNews> currentNews) {
                loading = false;
                newsList.addAll(currentNews);
                System.out.println(currentNews.get(0).getTitel());
                currentNewsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(VolleyError error) {
                loading = false;
            }
        });
        pageToLoad++;
    }

}
