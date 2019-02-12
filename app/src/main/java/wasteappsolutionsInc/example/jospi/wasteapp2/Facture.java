package wasteappsolutionsInc.example.jospi.wasteapp2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.HashMap;

public class Facture extends AppCompatActivity implements View.OnClickListener {

    final String TAG = "Pay";
    CardView payer,cancel;
    TextView tv_credit,tv_email,tv_client,tv_frequency,tv_amount,tv_place,tv_residence,tv_phone,
    tv_link_credit,tv_days,tv_creneau,tv_todo;
    private ProgressDialog pd;
    String loginEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facture);

        payer = findViewById(R.id.cvPay);
        cancel = findViewById(R.id.cvCancel);
        tv_email = findViewById(R.id.Email);
        tv_client = findViewById(R.id.Client);
        tv_frequency = findViewById(R.id.Frequency);
        tv_place = findViewById(R.id.Place);
        tv_residence = findViewById(R.id.Residence);
        tv_phone = findViewById(R.id.Phone);
        tv_days = findViewById(R.id.Days);
        tv_todo = findViewById(R.id.Todo);
        tv_amount = findViewById(R.id.tv_amount);
        tv_credit = findViewById(R.id.tv_credit);
        tv_link_credit = findViewById(R.id.link_credit);
        tv_creneau = findViewById(R.id.tv_creneau);

        /*
            Getting logged in user email via shared preferences
         */
        SharedPreferences pref = getSharedPreferences("loginData",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        loginEmail = pref.getString("email",null);

        pd = new ProgressDialog(Facture.this);
        pd.setMessage("Chargement ...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);

        getSqlDetails();

        /*
            Getting extra string from Commander.java
         */
        String extraEmail = getIntent().getStringExtra("email");
        tv_email.setText(extraEmail);

        String extraClient = getIntent().getStringExtra("client");
        tv_client.setText(extraClient);

        String extraFrequency = getIntent().getStringExtra("frequency");
        tv_frequency.setText(extraFrequency);

        String extraPlace = getIntent().getStringExtra("place");
        tv_place.setText(extraPlace);

        String extraResidence = getIntent().getStringExtra("residence");
        tv_residence.setText(extraResidence);

        String extraPhone = getIntent().getStringExtra("phone");
        tv_phone.setText(extraPhone);

        String extraAmount = getIntent().getStringExtra("amount");
        tv_amount.setText(extraAmount);

        String extraCreneau = getIntent().getStringExtra("creneau");
        tv_creneau.setText(extraCreneau);

        String extraDays = getIntent().getStringExtra("days");
        tv_days.setText(extraDays);

        String extraTodo = getIntent().getStringExtra("todo");
        tv_todo.setText(extraTodo);

        /*
            Canceling order
         */
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        payer.setOnClickListener(this);
    }

    /*
        Function to retrieve total number of credits available for the logged in user
        using volley library
     */
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
                                tv_credit.setText(credit);

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
                                    "Quelque chose a mal tourné. Vérifiez votre connexion réseau",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }

        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    /*
         Checkout placed order
     */
    @Override
    public void onClick(View view) {

        final String orderAmount = tv_amount.getText().toString();

        HashMap<String,String> postBill = new HashMap<>();
        postBill.put("email",loginEmail);
        postBill.put("amount",orderAmount);

        PostResponseAsyncTask task = new PostResponseAsyncTask((Context) this,
                postBill, new AsyncResponse() {
            @Override
            public void processFinish(String output) {

                Log.d(TAG,output);
                if (output.contains("Error")){

                    Toast.makeText(Facture.this,
                            "Vous n'avez pas assez de crédits pour effectuer la transaction. \n" +
                                    "Rechargez votre compte",
                            Toast.LENGTH_LONG).show();

                }else {
                    Intent intent = new Intent(getApplicationContext(),MainPage.class);
                    intent.putExtra("email",loginEmail);
                    intent.putExtra("amount",orderAmount);
                    startActivity(intent);

                    Toast.makeText(Facture.this,
                            "Commande payée avec succès",
                            Toast.LENGTH_LONG).show();
                }
                Facture.this.finish();
            }
        });
        task.execute("http://www.wasteappsolutions.com/WastePay.php?email="+loginEmail+"&amount="+orderAmount);
    }


    private void cancel(){

        HashMap<String,String> postCancel = new HashMap<>();
        postCancel.put("email",loginEmail);

        PostResponseAsyncTask task2 = new PostResponseAsyncTask((Context) this,
                postCancel, new AsyncResponse() {
            @Override
            public void processFinish(String output) {

                Log.d(TAG,output);
                if (output.contains("Error")){

                    Toast.makeText(Facture.this,
                            "Quelque chose a mal tourné. Vérifiez votre connexion réseau",
                            Toast.LENGTH_LONG).show();

                }else {

                    Intent intent = new Intent(getApplicationContext(),MainPage.class);
                    startActivity(intent);

                    Toast.makeText(Facture.this,
                            "Commande annulée avec succès",
                            Toast.LENGTH_LONG).show();
                }
                Facture.this.finish();
            }
        });
        task2.execute("http://www.wasteappsolutions.com/WasteCancel.php?email="+loginEmail);
    }
}
