package BimBom.DBI.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import BimBom.DBI.ApiService.ApiService;
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

    public MutableLiveData<String> getUploadStatus() {
        return uploadStatus;
    }

    public void setPhoto(PhotoModel photoModel) {
        sendImageToApi(photoModel);
    }
    public void setSslContext(SSLContext context)
    {
        this.context = context;
    }
    public void setTrustManager(X509TrustManager manager)
    {
        this.trustManager = manager;
    }

    private void sendImageToApi(PhotoModel photoModel) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(loggingInterceptor)
//                .sslSocketFactory(context.getSocketFactory(), trustManager)
//                .build();
        OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:7219/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<String> call = apiService.uploadPhoto(photoModel.getBase64Image());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    uploadStatus.setValue("Zdjęcie zostało pomyślnie przesłane");
                } else {
                    uploadStatus.setValue("Błąd podczas przesyłania zdjęcia");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                uploadStatus.setValue("Błąd podczas przesyłania zdjęcia: " + t.getMessage());
            }
        });
    }
}