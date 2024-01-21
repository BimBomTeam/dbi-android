package BimBom.DBI.View;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import BimBom.DBI.R;
import BimBom.DBI.ViewModel.AuthViewModel;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText emailET, passwordET, passwordRepeatET;
    private TextView tvInfo;
    private Button btnSign, btnBack, btnOk;
    private Dialog registrationDialog;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initializeViews();
        setupFieldListeners();
        setupButtonClickListeners();
    }

    private void initializeViews() {
        firebaseAuth = FirebaseAuth.getInstance();
        emailET = findViewById(R.id.etEmail);
        passwordET = findViewById(R.id.etPassword);
        passwordRepeatET = findViewById(R.id.etPasswordRepeat);
        btnSign = findViewById(R.id.btnSingUp);
        btnBack = findViewById(R.id.btnBack);

        registrationDialog = new Dialog(this);
        registrationDialog.setContentView(R.layout.info_dialog);

        if (registrationDialog.getWindow() != null) {
            Drawable drawable = getResources().getDrawable(R.drawable.rounded_login_dialog);
            registrationDialog.getWindow().setBackgroundDrawable(drawable);
        }

        btnOk = registrationDialog.findViewById(R.id.btnOk);
        tvInfo = registrationDialog.findViewById(R.id.tvInfo);
    }

    private void setupButtonClickListeners() {
        btnSign.setOnClickListener(v -> {
            if (checkFields()) {
                register();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupFieldListeners() {
        emailET.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                checkEmailField();
                checkFields();
            }
        });

        passwordET.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                checkPasswordField();
                checkFields();
            }
        });

        passwordRepeatET.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                checkPasswordField();
                checkFields();
            }
        });

        // Additional checkFields() on text change
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };

        emailET.addTextChangedListener(textWatcher);
        passwordET.addTextChangedListener(textWatcher);
        passwordRepeatET.addTextChangedListener(textWatcher);
    }

    private boolean checkFields() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String confirmPassword = passwordRepeatET.getText().toString().trim();

        boolean fieldsValid = true;

        if (TextUtils.isEmpty(email)) {
            setError(emailET, R.string.email_is_empty);
            fieldsValid = false;
        } else {
            clearError(emailET);
        }

        if (TextUtils.isEmpty(password)) {
            setError(passwordET, R.string.password_is_empty);
            fieldsValid = false;
        } else if (!isSixCharacters(password)) {
            setError(passwordET, R.string.password_should_be_six_characters);
            fieldsValid = false;
        } else {
            clearError(passwordET);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            setError(passwordRepeatET, R.string.password_same_is_empty);
            fieldsValid = false;
        } else if (!isSixCharacters(confirmPassword)) {
            setError(passwordRepeatET, R.string.password_should_be_six_characters);
            fieldsValid = false;
        } else {
            clearError(passwordRepeatET);
        }

        boolean passwordsMatch = password.equals(confirmPassword);

        if (!passwordsMatch) {
            setError(passwordRepeatET, R.string.password_is_same);
            fieldsValid = false;
        } else {
            clearError(passwordRepeatET);
        }

        btnSign.setEnabled(fieldsValid);

        return fieldsValid;
    }

    private void checkEmailField() {
        String email = emailET.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            setError(emailET, R.string.email_is_empty);
        } else if (!email.contains("@") || !email.contains(".")) {
            setError(emailET, R.string.invalid_email);
        } else {
            clearError(emailET);
        }
    }

    private void checkPasswordField() {
        String password = passwordET.getText().toString().trim();
        String confirmPassword = passwordRepeatET.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            setError(passwordET, R.string.password_is_empty);
        } else if (!isSixCharacters(password)) {
            setError(passwordET, R.string.password_should_be_six_characters);
        } else {
            clearError(passwordET);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            setError(passwordRepeatET, R.string.password_is_same);
        } else if (!isSixCharacters(confirmPassword)) {
            setError(passwordRepeatET, R.string.password_should_be_six_characters);
        } else {
            clearError(passwordRepeatET);
        }
    }

    private void setError(EditText editText, int errorMessageResId) {
        editText.setError(getString(errorMessageResId));
    }

    private void clearError(EditText editText) {
        editText.setError(null);
    }

    private boolean isSixCharacters(String text) {
        return text.matches("^.{6}$");
    }

    private void register() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        authViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(AuthViewModel.class);
        authViewModel.setContext(getApplicationContext());
        authViewModel.registerUser(email, password);
        authViewModel.getLoginResponseLiveData().observe(this, loginResponseDto -> {
            if (loginResponseDto != null) {
                handleSuccessfulLogin();
            } else {
                handleRegistrationFailure();
            }
        });
    }

    private void handleSuccessfulLogin() {
        tvInfo.setText(getString(R.string.SIGN_UP));
        registrationDialog.show();
        btnOk.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void handleRegistrationFailure() {
        tvInfo.setText(getString(R.string.registration_failed));
        registrationDialog.show();
        btnOk.setOnClickListener(v -> {
            registrationDialog.dismiss();
            setError(emailET, R.string.registration_failed);
            setError(passwordET, R.string.registration_failed);
            setError(passwordRepeatET, R.string.registration_failed);
        });
    }
}
