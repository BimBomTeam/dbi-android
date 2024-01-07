package BimBom.DBI.ViewModel;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import BimBom.DBI.LoginActivity;
import BimBom.DBI.Model.UserModel;
import BimBom.DBI.R;

public class AuthViewModel extends ViewModel {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MutableLiveData<UserModel> userLiveData = new MutableLiveData<>();
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "AuthViewModel";

    public AuthViewModel() {
    }

    public AuthViewModel(Application application) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(application.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(application, gso);
    }

    public Intent getGoogleSignInIntent() {
        if (mGoogleSignInClient != null) {
            return mGoogleSignInClient.getSignInIntent();
        } else {
            // Możesz obsłużyć ten przypadek, np. przez logowanie błędu
            Log.e(TAG, "GoogleSignInClient is null");
            return null;
        }
    }

    public void handleGoogleSignInResult(Intent data, LoginActivity activity) {
        AuthCredential credential = getGoogleAuthCredential(data);
        if (credential != null) {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(activity, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserModel userModel = new UserModel(user.getEmail(), user.getUid());
                                userLiveData.setValue(userModel);
                            }
                        } else {
                            // Dodaj logowanie błędu w przypadku niepowodzenia
                            Log.e(TAG, "Google Sign-In failed", task.getException());
                            userLiveData.setValue(null);
                        }
                    });
        } else {
            // Dodaj logowanie błędu w przypadku braku Credentials
            Log.e(TAG, "Google Auth Credential is null");
            userLiveData.setValue(null);
        }
    }

    private AuthCredential getGoogleAuthCredential(Intent data) {
        try {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult();
            if (account != null) {
                String idToken = account.getIdToken();
                return GoogleAuthProvider.getCredential(idToken, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserModel userModel = new UserModel(user.getEmail(), user.getUid());
                            userLiveData.setValue(userModel);
                        }
                    } else {
                        userLiveData.setValue(null);
                    }
                });
    }

    public MutableLiveData<UserModel> getUserLiveData() {
        return userLiveData;
    }
}
