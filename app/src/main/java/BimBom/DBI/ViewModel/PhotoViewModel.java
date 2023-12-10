package BimBom.DBI.ViewModel;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import BimBom.DBI.ApiService.ApiService;
import BimBom.DBI.Model.Dto.IdentifyRequestDto;
import BimBom.DBI.Model.PhotoModel;
import BimBom.DBI.Utils.UnsafeOkHttpClient;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoViewModel extends ViewModel {
    private SSLContext context;
    private X509TrustManager trustManager;
    private MutableLiveData<String> uploadStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressBarVisibility = new MutableLiveData<>();
    private MutableLiveData<String> responseFromServer = new MutableLiveData<>();

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

        OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:7219/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<String> call = apiService.uploadPhoto(new IdentifyRequestDto(photoModel.getBase64Image()));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    // Zdjęcie zostało przesłane pomyślnie, możesz przetwarzać odpowiedź
                    String responseData = response.body();
                    Log.d("CHUJ", "Odpowiedź: " + responseData);
                    if (responseData != null && !responseData.isEmpty()) {
                        // Przetwarzanie danych odpowiedzi
                        receiveResponseFromServer(responseData);
                    } else {
                        // Odpowiedź z serwera jest pusta lub niepoprawna
                        uploadStatus.setValue("Odpowiedź z serwera jest pusta lub niepoprawna");
                    }
                } else {
                    // Błąd podczas przesyłania zdjęcia - kod stanu odpowiedzi HTTP nie jest OK
                    uploadStatus.setValue("Błąd podczas przesyłania zdjęcia. Kod: " + response.code());
                    // Tutaj możesz także dodać logikę obsługi różnych kodów stanu odpowiedzi
                    // np. 404 - nie znaleziono, 500 - błąd serwera, itp.
                }
                progressBarVisibility.setValue(false);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Błąd podczas przesyłania żądania
                Log.d("CWEL", "kurwa ");
                uploadStatus.setValue("Błąd podczas przesyłania zdjęcia: " + t.getMessage());
                progressBarVisibility.setValue(false);
            }
        });

    }

    private void receiveResponseFromServer(String response) {
        // Logika odbierania odpowiedzi z serwera
        responseFromServer.setValue(response);
    }
}
