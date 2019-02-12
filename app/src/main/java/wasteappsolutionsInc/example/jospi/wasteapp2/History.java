package wasteappsolutionsInc.example.jospi.wasteapp2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {

    ListView listView;
    ProgressBar progressBar;
    ListAdapter adapter ;
    List<BillHistory> billHist;
    String loginEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_history);

        listView =  findViewById(R.id.listview);
        progressBar =  findViewById(R.id.progressBar1);

        SharedPreferences pref = getSharedPreferences("loginData",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        loginEmail = pref.getString("email",null);

        new ParseJSonDataClass(this).execute();
    }

    @SuppressLint("StaticFieldLeak")
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

            HttpServiceClass httpServiceClass = new HttpServiceClass("http://www.wasteappsolutions.com/WasteList3.php?email="+loginEmail);

            try {
                httpServiceClass.ExecutePostRequest();

                if (httpServiceClass.getResponseCode() == 200) {

                    FinalJSonResult = httpServiceClass.getResponse();

                    if (FinalJSonResult != null) {

                        try {

                            JSONArray jsonArray = new JSONArray(FinalJSonResult);
                            JSONObject jsonObject;
                            BillHistory billHistory;
                            billHist = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                billHistory = new BillHistory();
                                jsonObject = jsonArray.getJSONObject(i);
                                billHistory.ORDER = jsonObject.getString("id");
                                billHistory.DATE = jsonObject.getString("created_date");
                                billHistory.CLIENT = jsonObject.getString("client");
                                billHistory.FREQUENCY = jsonObject.getString("frequency");
                                billHistory.TODO = jsonObject.getString("todo");
                                billHistory.DONE = jsonObject.getString("done");
                                billHistory.DAYS = jsonObject.getString("days");
                                billHistory.CRENEAU = jsonObject.getString("creneau");
                                billHistory.CREDIT = jsonObject.getString("amount");
                                billHistory.RESIDENCE = jsonObject.getString("residence");
                                billHistory.PLACE = jsonObject.getString("place");
                                billHistory.PHONE = jsonObject.getString("phone");
                                billHistory.STATUS = jsonObject.getString("status");
                                billHistory.MESSAGE = jsonObject.getString("message");
                                billHist.add(billHistory);
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
        protected void onPostExecute(Void result)

        {
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            adapter = new ListAdapter(context, billHist);
            listView.setAdapter(adapter);
        }
    }
}
