package BimBom.DBI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import BimBom.DBI.R;

public class SettingsActivity extends AppCompatActivity {
    private Button btnBack, btnlogout, btnDeleteAccont, btnEditAccount, btnClearHistory;

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnlogout = findViewById(R.id.btnLogout);
        btnDeleteAccont = findViewById(R.id.btnDeleteAccont);
        btnEditAccount = findViewById(R.id.btnEditAccount);
        btnClearHistory = findViewById(R.id.btnClearHistory);
    }

    private boolean isUserLoggedIn() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }

    private void buttonsEnabled() {
        if (!isUserLoggedIn()) {
            btnlogout.setEnabled(false);
            btnDeleteAccont.setEnabled(false);
            btnEditAccount.setEnabled(false);
            btnClearHistory.setEnabled(false);

        } else {
            btnlogout.setEnabled(true);
            btnDeleteAccont.setEnabled(true);
            btnEditAccount.setEnabled(true);
            btnClearHistory.setEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initializeViews();
        buttonsEnabled();
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
                Toast.makeText(SettingsActivity.this, R.string.logged_out, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        btnDeleteAccont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if (mAuth.getCurrentUser() != null) {
                    mAuth.getCurrentUser().delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SettingsActivity.this, R.string.account_deleted, Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(SettingsActivity.this, R.string.account_deleted_erroe, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}

