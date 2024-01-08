package BimBom.DBI;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText emailET, passwordET, passwordRepeatET;
    private Button btnSign, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        emailET = findViewById(R.id.etEmail);
        passwordET = findViewById(R.id.etPassword);
        passwordRepeatET = findViewById(R.id.etPasswordRepeat);
        btnSign = findViewById(R.id.btnSingUp);
        btnBack = findViewById(R.id.btnBack);

        setupFieldListeners();
        setupButtonClickListeners();
    }

    private void setupButtonClickListeners() {
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFields()) {
                    register();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupFieldListeners() {
        emailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkEmailField();
                }
            }
        });

        passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkPasswordField();
                }
            }
        });

        passwordRepeatET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkPasswordField();
                }
            }
        });
    }

    private boolean checkFields() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String confirmPassword = passwordRepeatET.getText().toString().trim();

        boolean fieldsValid = true;

        if (TextUtils.isEmpty(email)) {
            emailET.setError(String.valueOf(R.string.email_is_empty ));
            fieldsValid = false;
        } else {
            emailET.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordET.setError(String.valueOf(R.string.password_is_empty));
            fieldsValid = false;
        } else {
            passwordET.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            passwordRepeatET.setError(String.valueOf(R.string.password_is_empty));
            fieldsValid = false;
        } else {
            passwordRepeatET.setError(null);
        }

        boolean passwordsMatch = password.equals(confirmPassword);

        if (!passwordsMatch) {
            passwordRepeatET.setError(String.valueOf(R.string.password_is_same));
            fieldsValid = false;
        } else {
            passwordRepeatET.setError(null);
        }

        return fieldsValid;
    }

    private void checkEmailField() {
        String email = emailET.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailET.setError(String.valueOf(R.string.email_is_empty));
        } else {
            emailET.setError(null);
        }
    }

    private void checkPasswordField() {
        String password = passwordET.getText().toString().trim();
        String confirmPassword = passwordRepeatET.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            passwordET.setError(String.valueOf(R.string.password_is_empty));
        } else {
            passwordET.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            passwordRepeatET.setError(String.valueOf(R.string.password_is_same));
        } else {
            passwordRepeatET.setError(null);
        }
    }

    private void register() {
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showToast(String.valueOf(R.string.registration_successful));
                        } else {
                            showToast(String.valueOf(R.string.registration_failed));
                        }
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
