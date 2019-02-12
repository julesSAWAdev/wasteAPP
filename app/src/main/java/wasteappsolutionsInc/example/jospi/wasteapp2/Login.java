package wasteappsolutionsInc.example.jospi.wasteapp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;

public class Login extends AppCompatActivity implements View.OnClickListener {

    final String TAG = "Login";
    EditText etEmail,etPassword;
    CardView cvLogin;
    TextView tvRegister,tv_forgot;
    TextInputLayout inputEmail,inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tvRegister = findViewById(R.id.tv_register);
        cvLogin = findViewById(R.id.cv_login);
        tv_forgot = findViewById(R.id.tv_forgot);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);

        etEmail.addTextChangedListener(new MyTextWatcher(etEmail));
        etPassword.addTextChangedListener(new MyTextWatcher(etPassword));

        cvLogin.setOnClickListener(this);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        tv_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,ForgotPassword.class));
            }
        });
    }

    String password = "";
    String noms = "";
    String credits = "";

    @Override
    public void onClick(View view) {

        if (validateEmail()){

            if (!emptyPassword(etPassword)){

                final String email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                HashMap<String, String> loginData = new HashMap<>();
                loginData.put("email",email);
                loginData.put("password",password);
                loginData.put("name",noms);
                loginData.put("credit",credits);

                PostResponseAsyncTask loginTask = new PostResponseAsyncTask((Context) this,
                        loginData, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        Log.d(TAG, s);
                        if (s.contains("LoginSuccess")){
                            SharedPreferences pref = getSharedPreferences("loginData",MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("email",email);
                            editor.putString("password",password);
                            editor.putString("name",noms);
                            editor.putString("credit",credits);
                            editor.commit();

                            startActivity(new Intent(Login.this,MainPage.class));

                        }else{
                            finish();
                            overridePendingTransition( 0, 0);
                            startActivity(getIntent());
                            overridePendingTransition( 0, 0);
                            Toast.makeText(Login.this,
                                    "Email ou mot de passe invalide! Réessayez",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

                loginTask.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void handleException(Exception e) {

                        if (e != null && e.getMessage() != null){
                            Log.d(TAG, e.getMessage());
                        }
                    }
                });

                loginTask.execute("http://www.wasteappsolutions.com/wasteLogin.php");

            }else { // Empty Password
                inputPassword.setError(getString(R.string.err_msg_password));
                requestFocus(inputPassword);
            }
        }else{ // validate email
            inputEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
        }
    }

    /*
        Functions
     */
    private boolean emptyPassword(EditText etPassword) {
        String password = etPassword.getText().toString();
        return (password.isEmpty());
    }

    // Validate email
    private boolean validateEmail() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputEmail.setErrorEnabled(false);
        }
        return true;
    }

    // Validate Password
    private boolean validatePassword() {
        if (etPassword.getText().toString().trim().isEmpty()) {
            inputPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputPassword.setErrorEnabled(false);
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
                case R.id.et_password:
                    validatePassword();
                    break;
            }
        }
    }
}
