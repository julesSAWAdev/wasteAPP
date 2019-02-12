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
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;

public class Register extends AppCompatActivity implements View.OnClickListener {

    final String TAG = "Register";
    EditText etName,etEmail,etPhone,etResidence,etPlace,etPassword,etConfirmPassword;
    CardView cvRegister;
    TextView tvLogin;
    TextInputLayout inputName,inputResidence,inputPlace,inputPhone,inputEmail,inputPassword,
            inputPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etResidence = findViewById(R.id.et_residence);
        etPlace = findViewById(R.id.et_place);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_c_password);
        tvLogin = findViewById(R.id.tv_login);
        cvRegister = findViewById(R.id.cv_register);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputPassword2 = findViewById(R.id.inputPassword2);
        inputPhone = findViewById(R.id.inputPhone);
        inputName = findViewById(R.id.inputName);
        inputResidence = findViewById(R.id.inputResidence);
        inputPlace = findViewById(R.id.inputPlace);

        etEmail.addTextChangedListener(new Register.MyTextWatcher(etEmail));
        etPassword.addTextChangedListener(new Register.MyTextWatcher(etPassword));
        etConfirmPassword.addTextChangedListener(new Register.MyTextWatcher(etConfirmPassword));
        etName.addTextChangedListener(new Register.MyTextWatcher(etName));
        etPlace.addTextChangedListener(new Register.MyTextWatcher(etPlace));
        etResidence.addTextChangedListener(new Register.MyTextWatcher(etResidence));
        etPhone.addTextChangedListener(new Register.MyTextWatcher(etPhone));

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

        cvRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (!emptyName(etName)){

            if (validateEmail()){

                if (!emptyPhone(etPhone)){

                    if (phoneValidate()){

                        if (!emptyResidence(etResidence)) {

                            if (!emptyPlace(etPlace)) {

                                if (!emptyPassword(etPassword)){

                                    if (!emptyConfirmPassword(etConfirmPassword)){

                                        if (!passwordValidate(etPassword,etConfirmPassword)){
                                            inputPassword2.setError(getString(R.string.err_msg_match));
                                            requestFocus(inputPassword2);
                                        }else { // password Validate

                                            final String name = etName.getText().toString();
                                            final String email = etEmail.getText().toString();
                                            final String phone = etPhone.getText().toString();
                                            final String residence = etResidence.getText().toString();
                                            final String place = etPlace.getText().toString();
                                            final String password = etPassword.getText().toString();
                                            //String password = MD5.encrypt(etPassword.getText().toString());

                                            HashMap<String, String> postData = new HashMap<>();

                                            postData.put("name", name);
                                            postData.put("email", email);
                                            postData.put("phone", phone);
                                            postData.put("residence", residence);
                                            postData.put("place", place);
                                            postData.put("password", password);

                                            PostResponseAsyncTask task1 = new PostResponseAsyncTask((Context) this,
                                                    postData, new AsyncResponse() {
                                                @Override
                                                public void processFinish(String output) {

                                                    Log.d(TAG, output);
                                                    if (output.contains("ErrorInsert")) {
                                                        finish();
                                                        overridePendingTransition( 0, 0);
                                                        startActivity(getIntent());
                                                        overridePendingTransition( 0, 0);
                                                        Toast.makeText(Register.this,
                                                                "L'email existe déjà. Données non enregistrées!",
                                                                Toast.LENGTH_LONG).show();
                                                    } else{
                                                        startActivity(new Intent(getApplicationContext(), Login.class));
                                                        Toast.makeText(Register.this,
                                                                "Votre compte WasteApp a été créé avec succès!",
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                    Register.this.finish();
                                                }
                                            });
                                            task1.execute("http://www.wasteappsolutions.com/wasteRegister.php");
                                        }

                                    }else{ // Confirm Password
                                        inputPassword2.setError(getString(R.string.err_msg_confirm));
                                        requestFocus(inputPassword2);
                                    }

                                }else{ // Password
                                    inputPassword.setError(getString(R.string.err_msg_password));
                                    requestFocus(inputPassword);
                                }

                            } else { //place
                                inputPlace.setError(getString(R.string.err_msg_place));
                                requestFocus(inputPlace);
                            }

                        }else{ // residence
                            inputResidence.setError(getString(R.string.err_msg_residence));
                            requestFocus(inputResidence);
                        }
                    }// phone Validate

                }else { // phone
                    inputPhone.setError(getString(R.string.err_msg_phone));
                    requestFocus(inputPhone);
                }
            } else { // email
                inputEmail.setError(getString(R.string.err_msg_email));
                requestFocus(inputEmail);
            }
        }else { // name
            inputName.setError(getString(R.string.err_msg_names));
            requestFocus(inputName);
        }
    }

    /*
         Validation Functions
     */

    private boolean emptyName(EditText etName) {
        String name = etName.getText().toString();
        return (name.isEmpty());
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

    private boolean phoneValidate(){

        final int length = etPhone.length();

        if (length < 8){
            inputPhone.setError(getString(R.string.err_msg_phone));
            requestFocus(inputPhone);
            return false;
        }
        return true;
    }

    private boolean emptyResidence(EditText etResidence) {
        String residence = etResidence.getText().toString();
        return (residence.isEmpty());
    }

    private boolean emptyPlace(EditText etPlace) {
        String place = etPlace.getText().toString();
        return (place.isEmpty());
    }

    private boolean emptyPhone(EditText etPhone) {
        String phone = etPhone.getText().toString();
        return (phone.isEmpty());
    }

    private boolean emptyPassword(EditText etPassword) {
        String password = etPassword.getText().toString();
        return (password.isEmpty());
    }

    private boolean emptyConfirmPassword(EditText etConfirmPassword) {
        String ConfirmPassword = etConfirmPassword.getText().toString();
        return (ConfirmPassword.isEmpty());
    }

    private boolean passwordValidate(EditText etPassword, EditText etConfirmPassword){
        String password = etPassword.getText().toString();
        String confirm = etConfirmPassword.getText().toString();
        return (password.equals(confirm));
    }

    // ========================= ANDROID HIVE =============================

    // Validate Password
    private boolean validatePassword() {
        if (etPassword.getText().toString().trim().isEmpty() &&
                etConfirmPassword.getText().toString().trim().isEmpty()) {

            inputPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            inputPassword2.setError(getString(R.string.err_msg_confirm));
            requestFocus(inputPassword2);
            return false;
        } else {
            inputPassword.setErrorEnabled(false);
            inputPassword2.setErrorEnabled(false);
        }
        return true;
    }

    // Is email valid
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //validate Names
    private boolean validateName() {
        if (etName.getText().toString().trim().isEmpty()) {

            inputName.setError(getString(R.string.err_msg_names));
            requestFocus(inputName);
            return false;

        } else {
            inputName.setErrorEnabled(false);
        }
        return true;
    }

    // validate Phone
    private boolean validatePhone() {
        if (etPhone.getText().toString().isEmpty()) {

            final int length = etPhone.length();

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

    // validate Residence
    private boolean validateResidence() {
        if (etResidence.getText().toString().trim().isEmpty()) {
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
        if (etPlace.getText().toString().isEmpty()) {

            inputPlace.setError(getString(R.string.err_msg_names));
            requestFocus(inputPlace);
            return false;

        } else {
            inputPlace.setErrorEnabled(false);
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

                case R.id.et_name:
                    validateName();
                    break;
                case R.id.et_email:
                    validateEmail();
                    break;
                case R.id.et_phone:
                    validatePhone();
                    break;
                case R.id.et_residence:
                    validateResidence();
                    break;
                case R.id.et_place:
                    validatePlace();
                    break;
                case R.id.et_password:
                    validatePassword();
                    break;
                case R.id.et_c_password:
                    passwordValidate(etPassword,etConfirmPassword);
                    break;
            }
        }
    }
}
