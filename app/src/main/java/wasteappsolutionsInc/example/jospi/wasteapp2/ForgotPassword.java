package wasteappsolutionsInc.example.jospi.wasteapp2;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;
import java.util.HashMap;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    String TAG = "Reset";
    EditText Email;
    CardView Reset;
    TextInputLayout inputReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Email = findViewById(R.id.et_email);
        Reset = findViewById(R.id.cv_reset);
        inputReset = findViewById(R.id.inputReset);

        Email.addTextChangedListener(new ForgotPassword.MyTextWatcher(Email));

        Reset.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        if (validateEmail()){

            final String email = Email.getText().toString();

            HashMap<String,String> postReset = new HashMap<>();
            postReset.put("email",email);

            PostResponseAsyncTask task = new PostResponseAsyncTask((Context) this,
                    postReset, new AsyncResponse() {
                @Override
                public void processFinish(String output) {

                    Log.d(TAG,output);
                    if (output.contains("Error")){
                        finish();
                        overridePendingTransition( 0, 0);
                        startActivity(getIntent());
                        overridePendingTransition( 0, 0);
                        Toast.makeText(ForgotPassword.this,
                                "Le compte n'existe pas",
                                Toast.LENGTH_LONG).show();
                    }else {

                        startActivity(new Intent(getApplicationContext(),Login.class));
                        Toast.makeText(ForgotPassword.this,
                                "Réinitialisation effectuée avec succès, veuillez vérifier dans votre boîte de réception",
                                Toast.LENGTH_LONG).show();
                    }
                    ForgotPassword.this.finish();
                }
            });
            task.execute("http://www.wasteappsolutions.com/WasteReset.php?email="+email);
        }
    }

     /*
        Validation Functions
     */

    // Validate email
    private boolean validateEmail() {
        String email = Email.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputReset.setError(getString(R.string.err_msg_email));
            requestFocus(inputReset);
            return false;
        } else {
            inputReset.setErrorEnabled(false);
        }
        return true;
    }

    // Is email valid
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
            }
        }
    }
}
