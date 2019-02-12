package wasteappsolutionsInc.example.jospi.wasteapp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;

public class Credit extends AppCompatActivity implements View.OnClickListener {

    String TAG = "Send";
    EditText trans,cash;
    TextView defCredit;
    CardView send;
    String loginEmail = "";
    TextInputLayout inputCash,inputTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);

        trans = findViewById(R.id.transID);
        cash = findViewById(R.id.transCash);
        send = findViewById(R.id.cvSend);
        defCredit = findViewById(R.id.defCredit);

        inputCash = findViewById(R.id.inputCash);
        inputTrans = findViewById(R.id.inputTrans);

        trans.addTextChangedListener(new Credit.MyTextWatcher(trans));
        cash.addTextChangedListener(new Credit.MyTextWatcher(cash));

        SharedPreferences pref = getSharedPreferences("loginData",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        loginEmail = pref.getString("email",null);

        send.setOnClickListener(this);

        defCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),DetailCredit.class));
            }
        });
    }

    @Override
    public void onClick(View view) {

        if (!emptyTrans(trans)){

            if (!emptyCash(cash)){

                if (cashValidate()){

                    final String transID = trans.getText().toString();
                    final String transCash = cash.getText().toString();

                    HashMap<String,String> postCredit = new HashMap<>();
                    postCredit.put("email",loginEmail);
                    postCredit.put("trans",transID);
                    postCredit.put("amount",transCash);

                    PostResponseAsyncTask task = new PostResponseAsyncTask((Context) this,
                            postCredit, new AsyncResponse() {
                        @Override
                        public void processFinish(String output) {

                            Log.d(TAG,output);
                            if (output.contains("Error")){
                                finish();
                                overridePendingTransition( 0, 0);
                                startActivity(getIntent());
                                overridePendingTransition( 0, 0);
                                Toast.makeText(Credit.this,
                                        "Quelque chose a mal tourné.\n Vérifiez votre connexion réseau.",
                                        Toast.LENGTH_LONG).show();
                            }else {

                                startActivity(new Intent(getApplicationContext(),MainPage.class));
                                Toast.makeText(Credit.this,
                                        "Opération effectuée avec Succès.\nVotre compte sera crédité après la validation de votre dépôt Mobile Money. ",
                                        Toast.LENGTH_LONG).show();
                            }
                            Credit.this.finish();
                        }
                    });
                    task.execute("http://www.wasteappsolutions.com/WasteCredit.php?email="+loginEmail+"&amount="+transCash+"&trans="+transID);

                } // cashValidate
            }else { // Empty Cash
                inputCash.setError(getString(R.string.err_msg_deposit));
                requestFocus(inputCash);
            }

        }else { // Empty Trans
            inputTrans.setError(getString(R.string.err_msg_trans));
            requestFocus(inputTrans);
        }
    }

    /*
        Validation Functions
     */

    private boolean emptyTrans(EditText trans){
        String transID = trans.getText().toString();
        return (transID.isEmpty() );
    }

    private boolean emptyCash(EditText cash){
        String amount = cash.getText().toString();
        return (amount.isEmpty());
    }

    private boolean cashValidate(){

        final int length = cash.length();

        if (length < 4){
            inputCash.setError(getString(R.string.err_msg_deposit));
            requestFocus(inputCash);
            return false;
        }
        return true;
    }

    // ================== ANDROID HIVE ==========================

    // Validate Trans
    private boolean validateTrans() {
        if (trans.getText().toString().isEmpty()) {

            inputTrans.setError(getString(R.string.err_msg_trans));
            requestFocus(inputTrans);
            return false;

        } else {
            inputTrans.setErrorEnabled(false);
        }
        return true;
    }

    // Validate cash
    private boolean validateCash() {
        if (cash.getText().toString().isEmpty()) {

            inputCash.setError(getString(R.string.err_msg_deposit));
            requestFocus(inputCash);
            return false;

        } else {
            inputCash.setErrorEnabled(false);
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
                case R.id.transID:
                    validateTrans();
                    break;
                case R.id.transCash:
                    validateCash();
                    break;
            }
        }
    }
}
