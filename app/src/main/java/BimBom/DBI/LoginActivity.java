package BimBom.DBI;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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
        loginDialog.setContentView(R.layout.login_dialog);
        if (loginDialog.getWindow() != null) {
            Drawable drawable = getResources().getDrawable(R.drawable.rounded_login_dialog);
            loginDialog.getWindow().setBackgroundDrawable(drawable);
        }
        btnOk = loginDialog.findViewById(R.id.btnOk);
        tvSignInfo = loginDialog.findViewById(R.id.tvSignInfo);
        authViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AuthViewModel.class);
        authViewModel.setContext(getApplicationContext());

        authViewModel.getUserLiveData().observe(this, userModel -> {
            if (userModel != null) {
                tvSignInfo.setText("SIGNED UP!");
                loginDialog.show();
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            } else {
                tvSignInfo.setText("LOGIN ERROR");
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
        String email = etLogin.getText().toString();
        String password = etPassword.getText().toString();
        authViewModel.loginUser(email, password);
    }

    private void onLoginWithGoogleClick() {
        Intent signInIntent = authViewModel.getGoogleSignInIntent();
        if (signInIntent != null) {
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        } else {
            Toast.makeText(this, "Błąd logowania przez Google", Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickRegistration(){
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
