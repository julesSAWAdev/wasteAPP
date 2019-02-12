package wasteappsolutionsInc.example.jospi.wasteapp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class MainPage extends AppCompatActivity {


    TextView tv_name,tv_credit,tv_id,tv_share,tv_solde,refresh;
    public static TextView tv_share_credit;
    CardView crediter,commander,logout,historique,about,cvShareHist;
    String loginEmail = "";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        tv_name = findViewById(R.id.tv_names);
        tv_credit = findViewById(R.id.tv_credit);
        tv_id = findViewById(R.id.tv_id);
        crediter = findViewById(R.id.crediter_card);
        commander = findViewById(R.id.card_commander);
        logout = findViewById(R.id.cvLogout);
        historique = findViewById(R.id.card_historique);
        tv_share = findViewById(R.id.tv_share);
        about = findViewById(R.id.cv_about);
        tv_solde = findViewById(R.id.tv_solde);
        refresh = findViewById(R.id.tvRefresh);
        tv_share_credit = findViewById(R.id.tv_credit_share);
        cvShareHist = findViewById(R.id.cvShareHist);
        final String credit = tv_credit.getText().toString();

        pd = new ProgressDialog(MainPage.this);
        pd.setMessage("Chargement ...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);

        SharedPreferences pref = getSharedPreferences("loginData",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        loginEmail = pref.getString("email",null);

        getSqlDetails();

        new FetchCredit2().execute();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainPage.this,Login.class));
                MainPage.this.finish();
            }
        });

        crediter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainPage.this,Credit.class));
            }
        });

        cvShareHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String TAG2 = "Check";

                HashMap<String,String> postHist2 = new HashMap<>();
                postHist2.put("email",loginEmail);

                PostCheckAsyncTask task2 = new PostCheckAsyncTask(this,
                        postHist2, new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {

                        Log.d(TAG2,output);
                        if (output.contains("Error")){
                            finish();
                            overridePendingTransition( 0, 0);
                            startActivity(getIntent());
                            overridePendingTransition( 0, 0);
                            Toast.makeText(MainPage.this,
                                    "Votre historique de partage est vide.",
                                    Toast.LENGTH_LONG).show();
                        }else {

                            startActivity(new Intent(getApplicationContext(),ShareHistory.class));
                        }
                    }
                });
                task2.execute("http://www.wasteappsolutions.com/WasteCheck2.php?email="+loginEmail);
            }
        });

        historique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String TAG = "History";

                HashMap<String,String> postHist = new HashMap<>();
                postHist.put("email",loginEmail);

                PostCheckAsyncTask task = new PostCheckAsyncTask(this,
                        postHist, new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {

                        Log.d(TAG,output);
                        if (output.contains("Error")){
                            finish();
                            overridePendingTransition( 0, 0);
                            startActivity(getIntent());
                            overridePendingTransition( 0, 0);
                            Toast.makeText(MainPage.this,
                                    "Votre historique est vide.",
                                    Toast.LENGTH_LONG).show();
                        }else {
                            startActivity(new Intent(getApplicationContext(),History.class));
                        }
                    }
                });
                task.execute("http://www.wasteappsolutions.com/wastecheck.php?email="+loginEmail);
            }
        });

        commander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPage.this,Commander.class);
                intent.putExtra("email",loginEmail);
                startActivity(intent);
            }
        });

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ShareCredits.class);
                intent.putExtra("credit",credit);
                startActivity(intent);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),About.class));
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSqlDetails();
                new FetchCredit2().execute();
            }
        });
    }

    private void getSqlDetails() {

        String url= "http://www.wasteappsolutions.com/WasteMainPage.php?email="+loginEmail;
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        pd.hide();

                        try {

                            JSONArray jsonarray = new JSONArray(response);

                            for(int i=0; i < jsonarray.length(); i++) {

                                JSONObject jsonobject = jsonarray.getJSONObject(i);

                                String credit = jsonobject.getString("credit");
                                String name = jsonobject.getString("name");
                                String id = jsonobject.getString("iduser");
                                String solde = jsonobject.getString("solde");

                                tv_name.setText(name);
                                tv_credit.setText(credit);
                                tv_id.setText(" ID #"+id+ " ");
                                tv_solde.setText(solde + " Fcfa");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null){

                            Toast.makeText(getApplicationContext(),
                                    "Quelque chose a mal tourné.\n Vérifiez votre connexion réseau",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public class FetchCredit2 extends AsyncTask {

        String data ="";
        String singleParsed ="";
        String emailCredit = "";

        @Override
        protected Object doInBackground(Object[] objects) {

            SharedPreferences pref = getSharedPreferences("loginData",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            emailCredit = pref.getString("email",null);

            try {

                URL url = new URL("http://www.wasteappsolutions.com/WasteMainPage3.php?email="+emailCredit);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";

                while(line != null){
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                JSONArray JA = new JSONArray(data);

                for(int i = 0 ;i <JA.length(); i++){

                    JSONObject JO = (JSONObject) JA.get(i);
                    singleParsed =  ""+JO.get("Ncredit");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (singleParsed.isEmpty()){
                MainPage.tv_share_credit.setText("0");
            }else{
                MainPage.tv_share_credit.setText(this.singleParsed);
            }
        }
    }
}
