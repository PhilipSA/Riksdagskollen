package oscar.riksdagskollen.Utilities.Callbacks;

import com.android.volley.VolleyError;

import java.util.List;

import oscar.riksdagskollen.Utilities.JSONModels.PartyDocument;

/**
 * Created by gustavaaro on 2018-03-25.
 */

public interface PartyDocumentCallback {

    void onDocumentsFetched(List<PartyDocument> documents);

    void onFail(VolleyError error);
}
