package BimBom.DBI.View;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import BimBom.DBI.Model.UserModel;
import BimBom.DBI.R;
import BimBom.DBI.ViewModel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText etLogin, etPassword;
    private Button btnLogin, btnLoginWithGoogle, btnRegistration, btnBack, btnOk;
    private Dialog loginDialog;
    private TextView tvSignInfo;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupLoginDialog();
        setupViewModel();
        setupClickListeners();
    }

    private void initializeViews() {
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle);
        btnRegistration = findViewById(R.id.btnRegistration);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupLoginDialog() {
        loginDialog = new Dialog(this);
        loginDialog.setContentView(R.layout.info_dialog);

        if (loginDialog.getWindow() != null) {
            Drawable drawable = getResources().getDrawable(R.drawable.rounded_login_dialog);
            loginDialog.getWindow().setBackgroundDrawable(drawable);
        }

        btnOk = loginDialog.findViewById(R.id.btnOk);
        tvSignInfo = loginDialog.findViewById(R.id.tvInfo);
    }

    private void setupViewModel() {
        authViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AuthViewModel.class);
        authViewModel.setContext(getApplicationContext());

        authViewModel.getLoginResponseLiveData().observe(this, loginResponseDto -> {
            if (loginResponseDto != null) {
                handleSuccessfulLogin();
            } else {
                handleFailedLogin();
            }
        });
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> onLoginClick(v));
        btnLoginWithGoogle.setOnClickListener(v -> onLoginWithGoogleClick());
        btnRegistration.setOnClickListener(v -> onClickRegistration());
        btnBack.setOnClickListener(v -> finish());
    }

    private void onLoginClick(View view) {
        try {
            String email = etLogin.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                showError(getString(R.string.email_and_password_error));
                return;
            }

            if (!isValidEmail(email)) {
                showError(getString(R.string.email_error));
                return;
            }

            authViewModel.loginUser(email, password);
        } catch (Exception e) {
        }
    }

    private void handleSuccessfulLogin() {
        tvSignInfo.setText(getString(R.string.Logged));
        loginDialog.show();
        btnOk.setOnClickListener(v -> finish());
    }

    private void handleFailedLogin() {
        tvSignInfo.setText(R.string.login_error);
        loginDialog.show();
        btnOk.setOnClickListener(v -> loginDialog.dismiss());
    }

    private void showError(String errorMessage) {
        tvSignInfo.setText(errorMessage);
        loginDialog.show();
        btnOk.setOnClickListener(v -> loginDialog.dismiss());
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void onLoginWithGoogleClick() {
        Toast.makeText(LoginActivity.this, R.string.in_develop, Toast.LENGTH_SHORT).show();

    }

    private void onClickRegistration() {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }
}