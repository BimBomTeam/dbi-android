package BimBom.DBI.ViewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import BimBom.DBI.Service.ApiService;
import BimBom.DBI.Model.Dto.IdentifyRequestDto;
import BimBom.DBI.Model.Dto.IdentifyResponseDto;
import BimBom.DBI.Model.PhotoModel;
import BimBom.DBI.Service.ConnectionServer;
import BimBom.DBI.Utils.UnsafeOkHttpClient;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoViewModel extends ViewModel {
    private SSLContext context1;
    private Context context;
    public PhotoViewModel(Context context) {
        this.context = context;
    }
    public PhotoViewModel() {

    }
    private X509TrustManager trustManager;
    private MutableLiveData<String> uploadStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressBarVisibility = new MutableLiveData<>();
    private MutableLiveData<String> responseFromServer = new MutableLiveData<>();
    private MutableLiveData<IdentifyResponseDto> identifyResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
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
        this.context1 = context;
    }

    public void setTrustManager(X509TrustManager manager) {
        this.trustManager = manager;
    }

    private void sendImageToApi(PhotoModel photoModel) {
        progressBarVisibility.setValue(true);

        ConnectionServer connectionServer = ConnectionServer.getInstance(context);
        ApiService apiService = connectionServer.getApiService();

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
                errorLiveData.setValue("Błąd podczas przesyłania zdjęcia: " + t.getMessage());
                progressBarVisibility.setValue(false);
            }
        });
    }
}