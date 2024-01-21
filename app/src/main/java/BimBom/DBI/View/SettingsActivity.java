package BimBom.DBI.View;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import BimBom.DBI.R;
import BimBom.DBI.Service.JwtManager;

public class SettingsActivity extends AppCompatActivity {
    private Button btnBack, btnLogout, btnDeleteAccount, btnEditAccount, btnClearHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        setupButtonState();
        setupButtonClickListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccont);
        btnEditAccount = findViewById(R.id.btnEditAccount);
        btnClearHistory = findViewById(R.id.btnClearHistory);
    }

    private void setupButtonState() {
        if (!isUserLoggedIn()) {
            setButtonsEnabled(false);
        } else {
            setButtonsEnabled(true);
        }
    }

    private boolean isUserLoggedIn() {
        JwtManager jwtManager = new JwtManager(this);
        String jwtToken = jwtManager.getStoredJwtToken();
        return jwtToken != null && !jwtToken.isEmpty();
    }

    private void setButtonsEnabled(boolean isEnabled) {
        btnLogout.setEnabled(isEnabled);
        btnDeleteAccount.setEnabled(isEnabled);
        btnEditAccount.setEnabled(isEnabled);
        btnClearHistory.setEnabled(isEnabled);
    }

    private void setupButtonClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            JwtManager jwtManager = new JwtManager(this);
            jwtManager.clearJwtToken();
            Toast.makeText(SettingsActivity.this, R.string.logged_out, Toast.LENGTH_SHORT).show();
            finish();
        });

        btnEditAccount.setOnClickListener(v -> {
            Toast.makeText(SettingsActivity.this, R.string.in_develop, Toast.LENGTH_SHORT).show();
        });

        btnDeleteAccount.setOnClickListener(v -> {
            Toast.makeText(SettingsActivity.this, R.string.in_develop, Toast.LENGTH_SHORT).show();
        });
        btnClearHistory.setOnClickListener(v -> {
            Toast.makeText(SettingsActivity.this, R.string.in_develop, Toast.LENGTH_SHORT).show();
        });
    }
}
