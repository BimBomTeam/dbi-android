package BimBom.DBI.ViewModel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.annotation.Annotation;

import BimBom.DBI.Model.Dto.ErrorLoginDto;
import BimBom.DBI.Model.Dto.LoginResponseDto;
import BimBom.DBI.Model.Dto.UserCredential;
import BimBom.DBI.Service.ApiService;
import BimBom.DBI.Service.ConnectionServer;
import BimBom.DBI.Service.JwtManager;
import BimBom.DBI.Model.UserModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {
    private JwtManager jwtManager;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private MutableLiveData<UserModel> userLiveData = new MutableLiveData<>();
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "AuthViewModel";
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> loginSuccessLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getLoginSuccessLiveData() {
        return loginSuccessLiveData;
    }

    private MutableLiveData<LoginResponseDto> loginResponseLiveData = new MutableLiveData<>();

    public MutableLiveData<LoginResponseDto> getLoginResponseLiveData() {
        return loginResponseLiveData;
    }

    private void setLoginSuccess(boolean success) {
        loginSuccessLiveData.setValue(success);
    }

    private Application application;
    private MutableLiveData<String> uploadStatus = new MutableLiveData<>();
    private Context context;

    public AuthViewModel() {

    }

    private void handleLoginResponse(LoginResponseDto loginResponse) {
        String jwtToken = loginResponse.getToken();
        JwtManager jwtManager = new JwtManager(context);
        jwtManager.saveJwtTokenToPreferences(jwtToken);
        String storedToken = jwtManager.getJwtToken();
        Log.d("logingit", "JWT token: " + storedToken);

        setLoginSuccess(true);
    }

    private void handleLoginFailure(String errorMessage) {
        errorLiveData.setValue(errorMessage);
        setLoginSuccess(false);
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
                        Log.d("logingit", "JWT token: " + storedToken+response.code());

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
    public void registerUser(String email, String password) {
        ConnectionServer connectionServer = ConnectionServer.getInstance(context);
        ApiService apiService = connectionServer.getApiService();

        UserCredential userCredential = new UserCredential(email, password);

        Call<LoginResponseDto> call = apiService.registerUser(userCredential);
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
                        Log.d("logingit", "JWT token: " + storedToken+response.code());

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

    private void handleSuccessfulLogin(LoginResponseDto loginResponse) {
        String jwtToken = loginResponse.getToken();
        JwtManager jwtManager = new JwtManager(context);
        jwtManager.saveJwtTokenToPreferences(jwtToken);
        String storedToken = jwtManager.getJwtToken();
        Log.d("logingit", "JWT token: " + storedToken);
        // Dodatkowe czynności po udanym zalogowaniu
    }

    private void handleErrorResponse(Response<?> response) {
        // Obsługa różnych typów błędów w zależności od kodu błędu
        int errorCode = response.code();
        switch (errorCode) {
            case 400:
                // Błąd związany z nieprawidłowymi danymi logowania
                // Obsługa przy użyciu innego DTO, np. InvalidLoginResponseDto
                handleInvalidLoginError(response);
                break;
            case 401:
                // Błąd autentykacji
                // Obsługa przy użyciu innego DTO, np. AuthenticationErrorDto
                handleAuthenticationError(response);
                break;
            // Dodaj obsługę innych kodów błędów, jeśli to konieczne
            default:
                uploadStatus.setValue("Błąd podczas logowania. Kod: " + errorCode);
                break;
        }
    }

    private void handleInvalidLoginError(Response<?> response) {
        try {
            // Manually parse the error response body
            String errorBodyString = response.errorBody().string();
            Gson gson = new Gson(); // You can use your preferred JSON parsing library
            ErrorLoginDto errorLoginDto = gson.fromJson(errorBodyString, ErrorLoginDto.class);

            // Use information from invalidLoginResponse for additional error handling
            // For example, you can get the error message using invalidLoginResponse.getMessage()
        } catch (IOException e) {
            Log.e("Error", "Błąd podczas przetwarzania błędu logowania: " + e.getMessage());
            uploadStatus.setValue("Błąd podczas logowania. Kod: " + response.code());
        }
    }

    private void handleAuthenticationError(Response<?> response) {
        // Obsługa błędu autentykacji
        // Możesz użyć innego DTO, np. AuthenticationErrorDto
        // np. AuthenticationErrorDto authenticationError = (AuthenticationErrorDto) response.body();
    }

    private void handleFailure(Throwable t) {
        Log.e("Error", "Błąd podczas logowania: " + t.getMessage());
        errorLiveData.setValue("Błąd podczas logowania: " + t.getMessage());
    }


    public MutableLiveData<UserModel> getUserLiveData() {
        return userLiveData;
    }
}