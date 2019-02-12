package wasteappsolutionsInc.example.jospi.wasteapp2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Commander extends AppCompatActivity implements View.OnClickListener {

    final String TAG = "Facture";
    Context context;
    Spinner SP_client,SP_frequency,SP_creneau;
    CardView cvConfirm,cvDays;
    String loginEmail = "";
    TextView Amount,link_credit,Collects,tvDays,tvDays2;
    private ProgressDialog pd;
    EditText residence,place,phone;
    TextInputLayout inputResidence,inputPlace,inputPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commander);

        SP_client = findViewById(R.id.client);
        SP_frequency = findViewById(R.id.frequency);
        SP_creneau = findViewById(R.id.creneau);

        residence = findViewById(R.id.et_residence2);
        place = findViewById(R.id.et_place2);
        phone = findViewById(R.id.et_phone2);

        residence.addTextChangedListener(new Commander.MyTextWatcher(residence));
        place.addTextChangedListener(new Commander.MyTextWatcher(place));
        phone.addTextChangedListener(new Commander.MyTextWatcher(phone));

        inputResidence = findViewById(R.id.inputResidence);
        inputPlace = findViewById(R.id.inputPlace);
        inputPhone = findViewById(R.id.inputPhone);

        tvDays = findViewById(R.id.tv_days);
        tvDays2 = findViewById(R.id.tvDays2);

        pd = new ProgressDialog(Commander.this);
        pd.setMessage("Chargement ...");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);

        SharedPreferences pref = getSharedPreferences("loginData", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        loginEmail = pref.getString("email", null);

        getSqlDetails();

        cvConfirm = findViewById(R.id.cvConfirm);
        cvDays = findViewById(R.id.cvDays);
        Amount = findViewById(R.id.tv_amount);
        Collects = findViewById(R.id.tv_collects);
        link_credit = findViewById(R.id.link_credit);

        ArrayAdapter<String> P_adapter = new ArrayAdapter<>(Commander.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.client));
        P_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SP_client.setAdapter(P_adapter);

        ArrayAdapter<String> F_adapter = new ArrayAdapter<>(Commander.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.frequency));
        F_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SP_frequency.setAdapter(F_adapter);

        ArrayAdapter<String> C_adapter = new ArrayAdapter<>(Commander.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.creneau));
        F_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SP_creneau.setAdapter(C_adapter);

        cvDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Build an AlertDialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(Commander.this);

                // String array for alert dialog multi choice items
                String[] days = new String[]{
                        "Lun",
                        "Mar",
                        "Mer",
                        "Jeu",
                        "Ven",
                        "Sam",
                        "Dim"
                };

                // Boolean array for initial selected items
                final boolean[] checkedDays = new boolean[]{
                        true, // Lundi
                        false, // Mardi
                        false, // Mercredi
                        false, // Jeudi
                        false, // Vendredi
                        false, // Samedi
                        false // Dimanche
                };

                // Convert the days array to list
                final List<String> daysList = Arrays.asList(days);

                builder.setMultiChoiceItems(days, checkedDays, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {

                        // Update the current focused item's checked status
                        checkedDays[i] = b;
                    }
                });

                // Specify the dialog is not cancelable
                builder.setCancelable(false);

                // Set a title for alert dialog
                builder.setTitle("Choisissez le(s) jour(s) de ramassage.");

                builder.setPositiveButton("Continuez", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // Do something when click positive button
                        tvDays2.setText("");
                        for (int j = 0; j< checkedDays.length; j++) {

                            boolean checked = checkedDays[j];

                            if (checked) {
                                tvDays2.setText(tvDays2.getText() + daysList.get(j) + " - ");
                            }
                        }
                    }

                });

                // Set the neutral/cancel button click listener
                builder.setNegativeButton("Annulez", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvDays2.setText(R.string.jour);
                    }
                });

                AlertDialog dialog = builder.create();
                // Display the alert dialog on interface
                dialog.show();
            }
        });

        cvConfirm.setOnClickListener(this);

        link_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Credit.class));
            }
        });
    }

    @Override
    public void onClick(View view) {

        String Client = SP_client.getSelectedItem().toString();
        String Frequency = SP_frequency.getSelectedItem().toString();

        if (Client.equals("Particulier")) {
            if (Frequency.equals("One Shot")) {
                Amount.setText("1");
            } else if (Frequency.equals("1* par semaine")) {
                Amount.setText("4");
            } else if (Frequency.equals("2* par semaine")) {
                Amount.setText("8");
            } else if (Frequency.equals("3* par semaine")) {
                Amount.setText("12");
            } else if (Frequency.equals("4* par semaine")) {
                Amount.setText("16");
            } else if (Frequency.equals("5* par semaine")) {
                Amount.setText("20");
            }
        } else if (Client.equals("Bar")) {
            if (Frequency.equals("One Shot")) {
                Amount.setText("1.5");
            } else if (Frequency.equals("1* par semaine")) {
                Amount.setText("6");
            } else if (Frequency.equals("2* par semaine")) {
                Amount.setText("12");
            } else if (Frequency.equals("3* par semaine")) {
                Amount.setText("18");
            } else if (Frequency.equals("4* par semaine")) {
                Amount.setText("24");
            } else if (Frequency.equals("5* par semaine")) {
                Amount.setText("30");
            }

        } else if (Client.equals("Epicerie") || Client.equals("Restaurant")) {
            if (Frequency.equals("One Shot")) {
                Amount.setText("2.5");
            } else if (Frequency.equals("1* par semaine")) {
                Amount.setText("10");
            } else if (Frequency.equals("2* par semaine")) {
                Amount.setText("20");
            } else if (Frequency.equals("3* par semaine")) {
                Amount.setText("30");
            } else if (Frequency.equals("4* par semaine")) {
                Amount.setText("40");
            } else if (Frequency.equals("5* par semaine")) {
                Amount.setText("50");
            }
        } else if (Client.equals("Hôtel")) {
            if (Frequency.equals("One Shot")) {
                Amount.setText("20");
            } else if (Frequency.equals("1* par semaine")) {
                Amount.setText("80");
            } else if (Frequency.equals("2* par semaine")) {
                Amount.setText("160");
            } else if (Frequency.equals("3* par semaine")) {
                Amount.setText("240");
            } else if (Frequency.equals("4* par semaine")) {
                Amount.setText("320");
            } else if (Frequency.equals("5* par semaine")) {
                Amount.setText("400");
            }
        } else if (Client.equals("Mariage") || Client.equals("Cérémonie")) {
            if (Frequency.equals("One Shot")) {
                Amount.setText("150");
            } else if (Frequency.equals("1* par semaine")) {
                Amount.setText("600");
            } else if (Frequency.equals("2* par semaine")) {
                Amount.setText("1200");
            } else if (Frequency.equals("3* par semaine")) {
                Amount.setText("1800");
            } else if (Frequency.equals("4* par semaine")) {
                Amount.setText("2400");
            } else if (Frequency.equals("5* par semaine")) {
                Amount.setText("3000");
            }
        }

        if (Frequency.equals("One Shot")) {
            Collects.setText("1");
        } else if (Frequency.equals("1* par semaine")) {
            Collects.setText("4* par mois");
        } else if (Frequency.equals("2* par semaine")) {
            Collects.setText("8* par mois");
        } else if (Frequency.equals("3* par semaine")) {
            Collects.setText("12* par mois");
        } else if (Frequency.equals("4* par semaine")) {
            Collects.setText("16* par mois");
        } else if (Frequency.equals("5* par semaine")) {
            Collects.setText("20* par mois");
        }

        if (!emptyResidence(residence)){

            if (!emptyPlace(place)){

                if (!emptyPhone(phone)){

                    if (phoneValidate()){

                        final String client = SP_client.getSelectedItem().toString();
                        final String frequency = SP_frequency.getSelectedItem().toString();
                        final String creneau = SP_creneau.getSelectedItem().toString();
                        final String place2 = place.getText().toString();
                        final String residence2 = residence.getText().toString();
                        final String phone2 = phone.getText().toString();
                        final String days = tvDays2.getText().toString();
                        final String amount = Amount.getText().toString();
                        final String collects = Collects.getText().toString();

                        HashMap<String, String> postData = new HashMap<>();

                        postData.put("email", loginEmail);
                        postData.put("client", client);
                        postData.put("frequency", frequency);
                        postData.put("place", place2);
                        postData.put("residence", residence2);
                        postData.put("phone", phone2);
                        postData.put("amount", amount);
                        postData.put("creneau", creneau);
                        postData.put("days", days);
                        postData.put("todo", collects);

                        PostResponseAsyncTask task1 = new PostResponseAsyncTask(this,
                                postData, new AsyncResponse() {
                            @Override
                            public void processFinish(String output) {

                                Log.d(TAG, output);
                                if (output.contains("ErrorInsert")) {
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(getIntent());
                                    overridePendingTransition(0, 0);
                                    Toast.makeText(Commander.this,
                                            "Quelque chose a mal tourné. Vérifiez votre connexion réseau.",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Intent intent = new Intent(getApplicationContext(), Facture.class);
                                    intent.putExtra("email", loginEmail);
                                    intent.putExtra("client", client);
                                    intent.putExtra("frequency", frequency);
                                    intent.putExtra("place", place2);
                                    intent.putExtra("residence", residence2);
                                    intent.putExtra("phone", phone2);
                                    intent.putExtra("amount", amount);
                                    intent.putExtra("creneau", creneau);
                                    intent.putExtra("days", days);
                                    intent.putExtra("todo", collects);
                                    startActivity(intent);
                                }
                                Commander.this.finish();
                            }
                        });
                        task1.execute("http://www.wasteappsolutions.com/WasteFacture.php");
                    }//phone Validate
                }else{// empty Phone
                    inputPhone.setError(getString(R.string.err_msg_phone));
                    requestFocus(inputPhone);
                }
            }else{// empty place
                inputPlace.setError(getString(R.string.err_msg_place));
                requestFocus(inputPlace);
            }
        }else{// empty residence
            inputResidence.setError(getString(R.string.err_msg_residence));
            requestFocus(inputResidence);
        }
    }

    /*
        Getting sql details for residence,place and phone placeholder
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

                                String Residence = jsonobject.getString("residence");
                                String Place = jsonobject.getString("place");
                                String Phone = jsonobject.getString("phone");

                                residence.setText(Residence);
                                place.setText(Place);
                                phone.setText(Phone);

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
        Validation Function
     */

    private boolean emptyResidence(EditText residence) {

        String Residence = residence.getText().toString();
        return (Residence.isEmpty());
    }

    private boolean emptyPlace(EditText place) {

        String Place = place.getText().toString();
        return (Place.isEmpty());
    }


    private boolean emptyPhone(EditText phone) {

        String Phone = phone.getText().toString();
        return (Phone.isEmpty());
    }

    private boolean phoneValidate(){

        final int length = phone.length();

        if (length < 8){
            inputPhone.setError(getString(R.string.err_msg_phone));
            requestFocus(inputPhone);
            return false;
        }
        return true;
    }

    // validate Residence
    private boolean validateResidence() {
        if (residence.getText().toString().trim().isEmpty()) {
            inputResidence.setError(getString(R.string.err_msg_residence));
            requestFocus(inputResidence);
            return false;

        } else {
            inputResidence.setErrorEnabled(false);
        }
        return true;
    }

    // validate Place
    private boolean validatePlace() {
        if (place.getText().toString().isEmpty()) {

            inputPlace.setError(getString(R.string.err_msg_names));
            requestFocus(inputPlace);
            return false;

        } else {
            inputPlace.setErrorEnabled(false);
        }
        return true;
    }

    // validate Phone
    private boolean validatePhone() {
        if (phone.getText().toString().isEmpty()) {

            final int length = phone.length();

            if (length < 8){
                inputPhone.setError(getString(R.string.err_msg_phone));
                requestFocus(inputPhone);
            }
            return false;

        } else {
            inputPhone.setErrorEnabled(false);
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
    class MyTextWatcher implements TextWatcher {

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

                case R.id.et_phone2:
                    validatePhone();
                    break;
                case R.id.et_residence2:
                    validateResidence();
                    break;
                case R.id.et_place2:
                    validatePlace();
                    break;
            }
        }
    }
}