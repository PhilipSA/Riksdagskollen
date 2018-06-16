package oscar.riksdagskollen.Utilities.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import oscar.riksdagskollen.R;
import oscar.riksdagskollen.Utilities.JSONModels.DecisionDocument;

/**
 * Created by oscar on 2018-03-29.
 */

public class DecisionListAdapter extends RiksdagenViewHolderAdapter {
    private List<DecisionDocument> decisionDocuments;
    private Context context;

    public DecisionListAdapter(List<DecisionDocument> items, final OnItemClickListener listener) {
        super(items, listener);
        this.clickListener = listener;
        decisionDocuments = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.decision_list_item, parent, false);
            return new DecisionView(itemView);
        } else {
            FrameLayout frameLayout = new FrameLayout(parent.getContext());
            //make sure it fills the space
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new HeaderFooterViewHolder(frameLayout);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < headers.size()) {
            View v = headers.get(position);
            //add our view to a header view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) holder, v);
        } else if (position >= headers.size() + decisionDocuments.size()) {
            View v = footers.get(position - decisionDocuments.size() - headers.size());
            //add our view to a footer view and display it
            prepareHeaderFooter((HeaderFooterViewHolder) holder, v);
        } else {
            DecisionDocument document = decisionDocuments.get(position);
            ((DecisionView) holder).bind(document, this.clickListener);
        }
    }

    /**
     * Class for displaying individual items in the list.
     */
    public class DecisionView extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView body;
        private TextView justDate;
        private TextView debateDate;
        private TextView decisionDate;
        private ImageView expandIcon;

        private TextView fullBet;
        private TextView searchVote;

        private View itemView;

        int rotationAngle = 0;



        public DecisionView(final View view) {
            super(view);
            this.itemView = view;
            title = view.findViewById(R.id.title);
            body = view.findViewById(R.id.body_text);
            expandIcon = view.findViewById(R.id.expand_icon);
            justDate = view.findViewById(R.id.justering_date);
            debateDate = view.findViewById(R.id.debatt_date);
            decisionDate = view.findViewById(R.id.beslut_date);
            fullBet = view.findViewById(R.id.full_bet_link);
            searchVote = view.findViewById(R.id.search_vote);
        }

        public void bind(final DecisionDocument item, final OnItemClickListener listener) {
            title.setText(item.getNotisrubrik());
            body.setText(Html.fromHtml(item.getNotis()));
            System.out.println(item.getNotisrubrik());
            System.out.println(item.getDebattdag());
            justDate.setText(dateStringBuilder("Justering: ",item.getJusteringsdag()));
            debateDate.setText(dateStringBuilder("Debatt: ", item.getDebattdag()));
            decisionDate.setText(dateStringBuilder("Beslut: ", item.getBeslutsdag()));
            fullBet.setText(textButtonBuilder("Läs fullständigt betänkande:  " + item.getRm() + ":" + item.getBeteckning()));
            searchVote.setText(textButtonBuilder("Sök efter votering"));



            if (item.isExpanded()){
                body.setVisibility(View.VISIBLE);
                fullBet.setVisibility(View.VISIBLE);
                searchVote.setVisibility(View.VISIBLE);
                expandIcon.setRotation(180);
            } else {
                body.setVisibility(View.GONE);
                fullBet.setVisibility(View.GONE);
                searchVote.setVisibility(View.GONE);
                expandIcon.setRotation(0    );
            }
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    rotationAngle = rotationAngle == 0 ? 180 : 0;  //toggle
                    expandIcon.animate().rotation(rotationAngle).setDuration(200).start();

                    listener.onItemClick(item);
                    if (item.isExpanded()) {
                        collapse(true);
                        item.setExpanded(false);
                    } else {
                        collapse(false);
                        item.setExpanded(true);
                    }
                }
            });

        }

        private SpannableString textButtonBuilder(String text){
            SpannableString txtSpannable= new SpannableString(text);
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            txtSpannable.setSpan(boldSpan, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtSpannable.setSpan(new UnderlineSpan(),0,text.length(),0);
            return txtSpannable;
        }

        private SpannableStringBuilder dateStringBuilder(String entity, String value){
            SpannableStringBuilder builder = new SpannableStringBuilder();
            SpannableString txtSpannable= new SpannableString(entity);
            if (value == null) value = "";
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            txtSpannable.setSpan(boldSpan, 0, entity.length()-2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.append(txtSpannable);
            builder.append(value);
            return builder;
        }


        void collapse(boolean collapse) {
            TransitionManager.beginDelayedTransition((ViewGroup) itemView.getParent());
            if (collapse){
                body.setVisibility(View.GONE);
                fullBet.setVisibility(View.GONE);
                searchVote.setVisibility(View.GONE);
            }
            else {
                body.setVisibility(View.VISIBLE);
                fullBet.setVisibility(View.VISIBLE);
                searchVote.setVisibility(View.VISIBLE);
            }
        }


    }
}