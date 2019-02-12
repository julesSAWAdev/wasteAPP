package wasteappsolutionsInc.example.jospi.wasteapp2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Startup extends AppCompatActivity {

    /*
    Variable declarations
    1. emailStored = user email if session was active else initialized to empty.
    2. passwordStored = user password if session was active else initialized to empty.
     */
    String emailStored = "", passwordStored = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        /*
            Handler class post delays to all the system to check for user session.
            The delay is exactly 1000 milliseconds.
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /*
                    Checking user session with help of SharedPreferences from previous Login if any.
                    if yes the the user will be redirected immediately to main page.
                    Else the user will be requested to login first
                 */
                SharedPreferences pref = getSharedPreferences("loginData",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                emailStored = pref.getString("email",null);
                passwordStored = pref.getString("password", null);

                if (emailStored == null){

                    startActivity(new Intent(getApplicationContext(),Login.class));

                }else {

                    startActivity(new Intent(getApplicationContext(),MainPage.class));

                }
                Startup.this.finish();
            }
        },1000);
    }
}
