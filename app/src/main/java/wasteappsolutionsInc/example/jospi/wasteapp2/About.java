package wasteappsolutionsInc.example.jospi.wasteapp2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class About extends AppCompatActivity {

    TextView web,shareapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        web = findViewById(R.id.web);
        shareapp = findViewById(R.id.tv_share_app);

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.wasteappsolutions.com")));
            }
        });

        shareapp.setOnClickListener(new View.OnClickListener() {
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
    }
}
