package BimBom.DBI.View;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import BimBom.DBI.R;

public class RegistrationActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText emailET, passwordET, passwordRepeatET;
    private TextView tvInfo;
    private Button btnSign, btnBack, btnOk;
    private Dialog registrationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initializeViews();
        setupFirebase();
        setupDialog();
        setupFieldListeners();
        setupButtonClickListeners();
    }

    private void initializeViews() {
        emailET = findViewById(R.id.etEmail);
        passwordET = findViewById(R.id.etPassword);
        passwordRepeatET = findViewById(R.id.etPasswordRepeat);
        btnSign = findViewById(R.id.btnSingUp);
        btnBack = findViewById(R.id.btnBack);
        btnOk = registrationDialog.findViewById(R.id.btnOk);
        tvInfo = registrationDialog.findViewById(R.id.tvInfo);
    }

    private void setupFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void setupDialog() {
        registrationDialog = new Dialog(this);
        registrationDialog.setContentView(R.layout.info_dialog);
        if (registrationDialog.getWindow() != null) {
            Drawable drawable = getResources().getDrawable(R.drawable.rounded_login_dialog);
            registrationDialog.getWindow().setBackgroundDrawable(drawable);
        }
    }

    private void setupButtonClickListeners() {
        btnSign.setOnClickListener(this::onSignButtonClick);
        btnBack.setOnClickListener(this::onBackButtonClick);
    }

    private void setupFieldListeners() {
        emailET.setOnFocusChangeListener(this::onEmailFocusChange);
        passwordET.setOnFocusChangeListener(this::onPasswordFocusChange);
        passwordRepeatET.setOnFocusChangeListener(this::onPasswordFocusChange);
    }

    private void onSignButtonClick(View view) {
        if (checkFields()) {
            register();
        }
    }

    private void onBackButtonClick(View view) {
        finish();
    }

    private void onEmailFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            checkEmailField();
        }
    }

    private void onPasswordFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            checkPasswordField();
        }
    }

    private boolean checkFields() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String confirmPassword = passwordRepeatET.getText().toString().trim();

        boolean fieldsValid = true;

        if (TextUtils.isEmpty(email)) {
            emailET.setError(getString(R.string.email_is_empty));
            fieldsValid = false;
        } else {
            emailET.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordET.setError(getString(R.string.password_is_empty));
            fieldsValid = false;
        } else {
            passwordET.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            passwordRepeatET.setError(getString(R.string.password_same_is_empty));
            fieldsValid = false;
        } else {
            passwordRepeatET.setError(null);
        }

        boolean passwordsMatch = password.equals(confirmPassword);

        if (!passwordsMatch) {
            passwordRepeatET.setError(getString(R.string.password_is_same));
            fieldsValid = false;
        } else {
            passwordRepeatET.setError(null);
        }
        return fieldsValid;
    }

    private void checkEmailField() {
        String email = emailET.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailET.setError(getString(R.string.email_is_empty));
        } else {
            emailET.setError(null);
        }
    }

    private void checkPasswordField() {
        String password = passwordET.getText().toString().trim();
        String confirmPassword = passwordRepeatET.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            passwordET.setError(getString(R.string.password_is_empty));
        } else if (!isSixCharacters(password)) {
            passwordET.setError(getString(R.string.password_should_be_six_characters));
        } else {
            passwordET.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            passwordRepeatET.setError(getString(R.string.password_is_same));
        } else if (!isSixCharacters(confirmPassword)) {
            passwordRepeatET.setError(getString(R.string.password_should_be_six_characters));
        } else {
            passwordRepeatET.setError(null);
        }
    }

    private boolean isSixCharacters(String text) {
        return text.matches("^.{6}$");
    }

    private void register() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            handleSuccessfulRegistration();
                        } else {
                            handleFailedRegistration();
                        }
                    }
                });
    }

    private void handleSuccessfulRegistration() {
        tvInfo.setText(getString(R.string.login_succesfull));
        registrationDialog.show();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
    }

    private void handleFailedRegistration() {
        tvInfo.setText(getString(R.string.login_succesfull_error));
        registrationDialog.show();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationDialog.dismiss();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
