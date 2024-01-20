package BimBom.DBI.ViewModel;

import android.app.Application;
import android.content.Context;
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

import BimBom.DBI.Model.Dto.IdentifyRequestDto;
import BimBom.DBI.Model.Dto.IdentifyResponseDto;
import BimBom.DBI.Model.Dto.LoginResponseDto;
import BimBom.DBI.Model.Dto.UserCredential;
import BimBom.DBI.Service.ApiService;
import BimBom.DBI.Service.ConnectionServer;
import BimBom.DBI.Service.JwtInterceptor;
import BimBom.DBI.Service.JwtManager;
import BimBom.DBI.View.LoginActivity;
import BimBom.DBI.Model.UserModel;
import BimBom.DBI.R;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthViewModel extends ViewModel {
    private  JwtManager jwtManager;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MutableLiveData<UserModel> userLiveData = new MutableLiveData<>();
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "AuthViewModel";
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public MutableLiveData<UserCredential> getUserCredential() {
        return getUserCredential();
    }

    private Application application;
    private MutableLiveData<String> uploadStatus = new MutableLiveData<>();
    private Context context;

    public AuthViewModel() {

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public AuthViewModel(Application application, Context context) {
        if (application == null || context == null) {
            throw new IllegalArgumentException("Application or Context cannot be null");
        }
        this.application = application;
        this.context = context;
        this.jwtManager = new JwtManager(context);
    }


    private GoogleSignInClient getGoogleSignInClient(Application application) {
        if (mGoogleSignInClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(application.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(application, gso);
            Log.d(TAG, "Web client ID: " + application.getString(R.string.default_web_client_id));
        }
        return mGoogleSignInClient;
    }


    public Intent getGoogleSignInIntent() {
        if (context == null) {
            throw new IllegalStateException("Context is null");
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        return mGoogleSignInClient.getSignInIntent();
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
                if (idToken != null && !idToken.isEmpty()) {
                    return GoogleAuthProvider.getCredential(idToken, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void loginUser(String email, String password) {
        ConnectionServer connectionServer = ConnectionServer.getInstance(context);
        ApiService apiService = connectionServer.getApiService();

        UserCredential userCredential = new UserCredential(email, password);

        Call<LoginResponseDto> call = apiService.loginUser(userCredential);
        call.enqueue(new Callback<LoginResponseDto>() {
            @Override
            public void onResponse(Call<LoginResponseDto> call, Response<LoginResponseDto> response) {
                if (response.isSuccessful()) {
                    LoginResponseDto loginResponse = response.body();
                    if (loginResponse != null) {
                        String jwtToken = loginResponse.getToken();
                        JwtManager jwtManager = new JwtManager(context);
                        jwtManager.saveJwtTokenToPreferences(jwtToken);
                        String storedToken = jwtManager.getJwtToken();
                        Log.d("logingit", "JWT token: " + storedToken);

                    }
                } else {
                    uploadStatus.setValue("Błąd podczas logowania. Kod: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDto> call, Throwable t) {
                Log.e("Error", "Błąd podczas logowania: " + t.getMessage());
                errorLiveData.setValue("Błąd podczas logowania: " + t.getMessage());
            }
        });
    }



    public MutableLiveData<UserModel> getUserLiveData() {
        return userLiveData;
    }
}