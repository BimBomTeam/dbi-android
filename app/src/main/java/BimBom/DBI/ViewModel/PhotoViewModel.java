package BimBom.DBI.ViewModel;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import BimBom.DBI.ApiService.ApiService;
import BimBom.DBI.Model.Dto.IdentifyRequestDto;
import BimBom.DBI.Model.Dto.IdentifyResponseDto;
import BimBom.DBI.Model.PhotoModel;
import BimBom.DBI.Utils.UnsafeOkHttpClient;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoViewModel extends ViewModel {
    private SSLContext context;
    private X509TrustManager trustManager;
    private MutableLiveData<String> uploadStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressBarVisibility = new MutableLiveData<>();
    private MutableLiveData<String> responseFromServer = new MutableLiveData<>();
    private MutableLiveData<IdentifyResponseDto> identifyResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<IdentifyResponseDto> getIdentifyResponseLiveData() {
        return identifyResponseLiveData;
    }
    public MutableLiveData<Boolean> getProgressBarVisibility() {
        return progressBarVisibility;
    }

    public MutableLiveData<String> getResponseFromServer() {
        return responseFromServer;
    }

    public MutableLiveData<String> getUploadStatus() {
        return uploadStatus;
    }

    public void setPhoto(PhotoModel photoModel) {
        sendImageToApi(photoModel);
    }

    public void setSslContext(SSLContext context) {
        this.context = context;
    }

    public void setTrustManager(X509TrustManager manager) {
        this.trustManager = manager;
    }

    private void sendImageToApi(PhotoModel photoModel) {
        progressBarVisibility.setValue(true);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = UnsafeOkHttpClient.getUnsafeOkHttpClient().newBuilder();
        clientBuilder.readTimeout(200, TimeUnit.SECONDS); // Zwiększenie timeout'u odczytu odpowiedzi
        clientBuilder.writeTimeout(200, TimeUnit.SECONDS); // Zwiększenie timeout'u zapisu żądania

        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:7219/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<IdentifyResponseDto> call = apiService.uploadPhoto(new IdentifyRequestDto(photoModel.getBase64Image()));

        call.enqueue(new Callback<IdentifyResponseDto>() {
            @Override
            public void onResponse(Call<IdentifyResponseDto> call, Response<IdentifyResponseDto> response) {
                if (response.isSuccessful()) {
                    IdentifyResponseDto identifyResponseDto = response.body();
                    identifyResponseLiveData.setValue(identifyResponseDto);
                } else {
                    uploadStatus.setValue("Błąd podczas przesyłania zdjęcia. Kod: " + response.code());
                }
                progressBarVisibility.setValue(false);
            }

            @Override
            public void onFailure(Call<IdentifyResponseDto> call, Throwable t) {
                Log.e("Error", "Błąd podczas przesyłania zdjęcia: " + t.getMessage());
                progressBarVisibility.setValue(false);
            }
        });
    }
    private void receiveResponseFromServer(String response) {
        if (response instanceof String) {
            responseFromServer.setValue(response);
        } else {
            Log.e("data Type Error", "Otrzymano nieprawidłowy typ danych w odpowiedzi z serwera");
        }
    }
}
