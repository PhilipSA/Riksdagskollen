package oscar.riksdagskollen.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import oscar.riksdagskollen.Activities.DocumentReaderActivity;
import oscar.riksdagskollen.R;
import oscar.riksdagskollen.RikdagskollenApp;
import oscar.riksdagskollen.Utilities.Callbacks.PartyDocumentCallback;
import oscar.riksdagskollen.Utilities.JSONModels.Party;
import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;
import oscar.riksdagskollen.Utilities.PartyListViewholderAdapter;

/**
 * Created by gustavaaro on 2018-03-26.
 */

public class PartyListFragment extends Fragment {

    Party party;
    int pageToLoad = 1;
    private int pastVisiblesItems;
    private boolean loading = false;

    private List<PartyDocument> documentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PartyListViewholderAdapter partyListAdapter;


    public static PartyListFragment newIntance(Party party){
        Bundle args = new Bundle();
        args.putParcelable("party",party);
        PartyListFragment newInstance = new PartyListFragment();
        newInstance.setArguments(args);
        return newInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.party = getArguments().getParcelable("party");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(party.getName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_party_list,null);
        partyListAdapter = new PartyListViewholderAdapter(documentList, new PartyListViewholderAdapter.OnPartyDocumentClickListener() {
            @Override
            public void onPartyDocumentClickListener(PartyDocument document) {
                Intent intent = new Intent(getContext(), DocumentReaderActivity.class);
                intent.putExtra("document",document);
                startActivity(intent);
            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(partyListAdapter);
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

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        loadNextPage();
        return view;
    }


    private void loadNextPage(){
        loading = true;

        RikdagskollenApp.getInstance().getRiksdagenAPIManager().getDocumentsForParty(party, pageToLoad, new PartyDocumentCallback() {
            @Override
            public void onDocumentsFetched(List<PartyDocument> documents) {
                loading = false;
                documentList.addAll(documents);
                partyListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(VolleyError error) {
                loading = false;
            }
        });
        pageToLoad++;
    }



}


