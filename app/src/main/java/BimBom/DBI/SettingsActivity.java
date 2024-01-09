package BimBom.DBI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import BimBom.DBI.R;

public class SettingsActivity extends AppCompatActivity {
    private Button btnBack, btnlogout;

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnlogout = findViewById(R.id.btnLogout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initializeViews();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(SettingsActivity.this, "Wylogowano",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}

