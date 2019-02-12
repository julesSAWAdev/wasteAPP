package wasteappsolutionsInc.example.jospi.wasteapp2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ShareCredits extends AppCompatActivity implements View.OnClickListener {

    final String TAG = "Share";
    EditText shareEmail,shareAmount;
    TextView shareID,shareNames,inscrire,shareWasteApp;
    ImageView searchEmail;
    String loginEmail = "";
    CardView share;
    private ProgressDialog pd;
    public static TextView tv_credit;
    TextInputLayout inputEmail,inputCredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_credits);

        shareEmail = findViewById(R.id.et_email);
        shareID = findViewById(R.id.et_id);
        shareNames = findViewById(R.id.et_names);
        shareAmount = findViewById(R.id.et_amount);
        searchEmail = findViewById(R.id.search_email);
        share = findViewById(R.id.cv_share);
        inscrire = findViewById(R.id.inscrire);
        tv_credit = findViewById(R.id.tv_credit);
        shareWasteApp = findViewById(R.id.tv_share_wasteapp);
        inputCredit = findViewById(R.id.inputCredit);
        inputEmail = findViewById(R.id.inputEmail);

        shareEmail.addTextChangedListener(new ShareCredits.MyTextWatcher(shareEmail));
        shareAmount.addTextChangedListener(new ShareCredits.MyTextWatcher(shareAmount));

        pd = new ProgressDialog(ShareCredits.this);
        pd.setMessage("Chargement ...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);

        new FetchCredit().execute();

        shareWasteApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String shareBody = "WasteApp";
                String shareSub = "Télécharge l’application WasteApp, je l’utilise pour commander et contrôler les services de ramassage porte-à-porte de mes déchets ménagers.: https://www.wasteappsolutions.com";
                intent.setType("text/plain");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(intent.EXTRA_SUBJECT,shareBody);
                intent.putExtra(intent.EXTRA_TEXT,shareSub);

                startActivity(Intent.createChooser(intent,"Partager WasteApp"));
            }
        });

        searchEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (submitForm()){

                    getSqlDetails();

                }else{
                    Toast.makeText(getApplicationContext(),
                            "Adresse e-mail invalide",Toast.LENGTH_LONG).show();
                    inputEmail.setError(getString(R.string.err_msg_email));
                    requestFocus(inputEmail);
                }
            }
        });

        share.setOnClickListener(this);

        inscrire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),Register.class));
                ShareCredits.this.finish();
            }
        });
    }

    private void getSqlDetails() {

        String getSearchEmail = shareEmail.getText().toString();

        String url= "http://www.wasteappsolutions.com/WasteSearch.php?email="+getSearchEmail;
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

                                String id = jsonobject.getString("iduser");
                                String names = jsonobject.getString("name");

                                shareID.setText(id);
                                shareNames.setText(names);

                                Toast.makeText(getApplicationContext(),
                                        "Email trouvé",Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Email non trouvé",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error != null){
                            Toast.makeText(getApplicationContext(),
                                    "Email non trouvé",Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View view) {

        if (validateEmail()){

            if (!emptyCredit(shareAmount)){

                if (creditValidate()){

                    SharedPreferences pref = getSharedPreferences("loginData",MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    loginEmail = pref.getString("email",null);

                    final String sharedCredit = shareAmount.getText().toString();
                    final String receiverEmail = shareEmail.getText().toString();

                    HashMap<String,String> postShare = new HashMap<>();
                    postShare.put("sender",loginEmail);
                    postShare.put("Ncredit",sharedCredit);
                    postShare.put("receiver", receiverEmail);

                    PostResponseAsyncTask task = new PostResponseAsyncTask(this,
                            postShare, new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {

                            Log.d(TAG,output);
                            if (output.contains("ErrorExist")){
                                finish();
                                overridePendingTransition( 0, 0);
                                startActivity(getIntent());
                                overridePendingTransition( 0, 0);
                                Toast.makeText(ShareCredits.this,
                                        "Désolé, vous ne pouvez pas partager des crédits à vous même.",
                                        Toast.LENGTH_LONG).show();

                            }else if(output.contains("Error")){
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition( 0, 0);
                                Toast.makeText(ShareCredits.this,
                                        "Vous n'avez pas assez de crédits pour effectuer l'opération",
                                        Toast.LENGTH_LONG).show();

                            }else {
                                startActivity(new Intent(getApplicationContext(),MainPage.class));
                                Toast.makeText(ShareCredits.this,
                                        "Opération effectuée avec succès",
                                        Toast.LENGTH_LONG).show();
                            }
                            ShareCredits.this.finish();
                        }
                    });
                    task.execute("http://www.wasteappsolutions.com/WasteShare.php?sender="+loginEmail+"&Ncredit="+sharedCredit+"&receiver="+receiverEmail);

                } // creditValidate

            }else{ // Empty ShareAmount
                inputCredit.setError(getString(R.string.err_msg_credit));
                requestFocus(inputCredit);
            }

        }else { // validate Email
            inputEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
        }
    }

    public class FetchCredit extends AsyncTask {

        String data ="";
        String singleParsed ="";
        String emailCredit = "";

        @Override
        protected Object doInBackground(Object[] objects) {

            SharedPreferences pref = getSharedPreferences("loginData",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            emailCredit = pref.getString("email",null);

            try {

                URL url = new URL("http://www.wasteappsolutions.com/WasteGet.php?email="+emailCredit);
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
                    singleParsed =  ""+JO.get("credit");
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
            ShareCredits.tv_credit.setText(this.singleParsed);
        }
    }

    /*
        Validation Functions ( ANDROID HIVE )
     */

    private boolean creditValidate(){

        final int value = Integer.valueOf(shareAmount.getText().toString());

        if (value < 6){
            inputCredit.setError(getString(R.string.err_msg_credit));
            requestFocus(inputCredit);
            return false;
        }
        return true;
    }


    private boolean emptyCredit(EditText shareAmount){
        String amount = shareAmount.getText().toString();
        return (amount.isEmpty());
    }

    private boolean submitForm() {

        if (!validateEmail()) {
            return false;
        }
        return true;
    }

    // Validate email
    private boolean validateEmail() {
        String email = shareEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputEmail.setErrorEnabled(false);
        }
        return true;
    }

    // Is email valid
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // validate Credit
    private boolean validateCredit() {
        if (shareAmount.getText().toString().isEmpty()) {

            inputCredit.setError(getString(R.string.err_msg_credit));
            requestFocus(inputCredit);
            return false;

        } else {
            inputCredit.setErrorEnabled(false);
        }
        return true;
    }

    // Request Focus
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    //My Text watcher
    private class MyTextWatcher implements TextWatcher {

        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.et_email:
                    validateEmail();
                    break;
                case R.id.et_amount:
                    validateCredit();
                    break;
            }
        }
    }

}
