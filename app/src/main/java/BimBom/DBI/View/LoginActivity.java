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
    private static final int RC_GOOGLE_SIGN_IN = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle);
        btnRegistration = findViewById(R.id.btnRegistration);
        btnBack = findViewById(R.id.btnBack);
        loginDialog = new Dialog(this);
        loginDialog.setContentView(R.layout.info_dialog);
        if (loginDialog.getWindow() != null) {
            Drawable drawable = getResources().getDrawable(R.drawable.rounded_login_dialog);
            loginDialog.getWindow().setBackgroundDrawable(drawable);
        }
        btnOk = loginDialog.findViewById(R.id.btnOk);
        tvSignInfo = loginDialog.findViewById(R.id.tvInfo);
        authViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AuthViewModel.class);
        authViewModel.setContext(getApplicationContext());

        authViewModel.getUserLiveData().observe(this, userModel -> {
            if (userModel != null) {
                tvSignInfo.setText(getString(R.string.Logged));
                loginDialog.show();
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            } else {
                tvSignInfo.setText(R.string.login_error);
                loginDialog.show();
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginDialog.dismiss();
                    }
                });
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick(v);
            }
        });

        btnLoginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginWithGoogleClick();
            }
        });
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRegistration();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onLoginClick(View view) {
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
            UserModel userModel = authViewModel.getUserLiveData().getValue();
            String tmp = userModel.generateJwtToken();
            Log.d("waznewchuj",tmp);
        }catch (Exception e)
        {
            Log.e("Logifajnedupa",e.getMessage());
        }
    }

    private void showError(String errorMessage) {
        tvSignInfo.setText(errorMessage);
        loginDialog.show();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDialog.dismiss();
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void onLoginWithGoogleClick() {
        Intent signInIntent = authViewModel.getGoogleSignInIntent();
        if (signInIntent != null) {
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        } else {
            Toast.makeText(this, R.string.error_loin_google , Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickRegistration() {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            authViewModel.handleGoogleSignInResult(data, this);
        }
    }
}
