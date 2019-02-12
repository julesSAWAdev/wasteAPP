package wasteappsolutionsInc.example.jospi.wasteapp2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

public class DetailCredit extends AppCompatActivity {

    TextView tv_buy, tv_share_credit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_credit);

        tv_buy = findViewById(R.id.tv_buy);
        tv_share_credit = findViewById(R.id.tv_share_credit);

        tv_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),Credit.class));
            }
        });

        tv_share_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),ShareCredits.class));
            }
        });
    }
}
