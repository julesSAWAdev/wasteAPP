package wasteappsolutionsInc.example.jospi.wasteapp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShareHistory extends AppCompatActivity {

    ListView listView;
    ProgressBar progressBar;
    ShareHistAdapter shareAdapter ;
    List<ShareHistoryList> shareHistoryListList;
    String loginEmail = "";
    TextView tv_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_history);

        listView =  findViewById(R.id.listview);
        progressBar =  findViewById(R.id.progressBar1);
        tv_share = findViewById(R.id.tv_share);

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ShareCredits.class));
                ShareHistory.this.finish();
            }
        });

        SharedPreferences pref = getSharedPreferences("loginData",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        loginEmail = pref.getString("email",null);

        new ParseJSonDataClass(this).execute();
    }

    private class ParseJSonDataClass extends AsyncTask<Void, Void, Void> {
        public Context context;
        String FinalJSonResult;

        ParseJSonDataClass(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpServiceClass httpServiceClass = new HttpServiceClass("http://www.wasteappsolutions.com/WasteList4.php?email="+loginEmail);

            try {
                httpServiceClass.ExecutePostRequest();

                if (httpServiceClass.getResponseCode() == 200) {

                    FinalJSonResult = httpServiceClass.getResponse();

                    if (FinalJSonResult != null) {

                        try {

                            JSONArray jsonArray = new JSONArray(FinalJSonResult);
                            JSONObject jsonObject;
                            ShareHistoryList shareHistoryList;
                            shareHistoryListList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                shareHistoryList = new ShareHistoryList();
                                jsonObject = jsonArray.getJSONObject(i);
                                shareHistoryList.ID = jsonObject.getString("id");
                                shareHistoryList.SENDER = jsonObject.getString("sendrN");
                                shareHistoryList.RECEIVER = jsonObject.getString("reiceiverN");
                                shareHistoryList.CREDIT = jsonObject.getString("Ncredit");
                                shareHistoryList.DATE = jsonObject.getString("times");
                                shareHistoryListList.add(shareHistoryList);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressBar.setVerticalScrollbarPosition(View.GONE);
            listView.setVisibility(View.VISIBLE);
            shareAdapter = new ShareHistAdapter(context,shareHistoryListList);
            listView.setAdapter(shareAdapter);
        }
    }
}
