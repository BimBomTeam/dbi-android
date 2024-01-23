package BimBom.DBI.ViewModel;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import BimBom.DBI.Model.Dto.ErrorLoginDto;
import BimBom.DBI.Model.Dto.LoginResponseDto;
import BimBom.DBI.Model.Dto.UserCredential;
import BimBom.DBI.R;
import BimBom.DBI.Service.ApiService;
import BimBom.DBI.Service.ConnectionServer;
import BimBom.DBI.Service.JwtManager;
import BimBom.DBI.Model.UserModel;
import BimBom.DBI.Service.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {
    private JwtManager jwtManager;
    private MutableLiveData<UserModel> userLiveData = new MutableLiveData<>();
    private static final String TAG = "AuthViewModel";
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> loginSuccessLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getLoginSuccessLiveData() {
        return loginSuccessLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
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
        TokenManager.setToken(storedToken);
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
                        loginResponseLiveData.setValue(loginResponse);
                        Log.d("logingit", "JWT token: " + storedToken + response.code());
                    }
                } else {
                    errorLiveData.setValue("Invalid login credentials:");
                    handleInvalidLoginError(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDto> call, Throwable t) {
                Log.e("Error", "Błąd podczas logowania: " + t.getMessage());
                errorLiveData.setValue(R.string.error_login + t.getMessage());
            }
        });
    }

    private void handleInvalidLoginError(Response<?> response) {
        try {
            String errorBodyString = response.errorBody().string();
            Gson gson = new Gson();
            ErrorLoginDto errorLoginDto;

            try {
                errorLoginDto = gson.fromJson(errorBodyString, ErrorLoginDto.class);
            } catch (JsonSyntaxException e) {
                errorLoginDto = new ErrorLoginDto();
                errorLoginDto.setError(errorBodyString);
            }

            String errorMessage = errorLoginDto.getError();
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e("Error", "Błąd podczas przetwarzania błędu logowania: " + e.getMessage());
            uploadStatus.setValue(context.getString (R.string.error_login_kod + response.code()));
        }
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
                        loginResponseLiveData.setValue(loginResponse);
                        Log.d("logingit", "JWT token: " + storedToken + response.code());
                    }
                } else {
                    errorLiveData.setValue("Invalid login credentials:");
                    uploadStatus.setValue( context.getString(R.string.error_login_kod)+ response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDto> call, Throwable t) {
                Log.e("Error", "Błąd podczas logowania: " + t.getMessage());
                errorLiveData.setValue(R.string.error_login  + t.getMessage());
            }
        });
    }

    public MutableLiveData<UserModel> getUserLiveData() {
        return userLiveData;
    }
}