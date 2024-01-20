package BimBom.DBI.Service;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import BimBom.DBI.Model.Dto.IdentifyResponseDto;
import BimBom.DBI.Utils.UnsafeOkHttpClient;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConnectionServer {
    private static ConnectionServer instance;
    private ApiService apiService;
    private Context context;

    private ConnectionServer() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = UnsafeOkHttpClient.getUnsafeOkHttpClient().newBuilder();
        clientBuilder.readTimeout(200, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(200, TimeUnit.SECONDS);

        OkHttpClient client = clientBuilder.build();
        clientBuilder.addInterceptor(new JwtInterceptor(context));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://193.122.12.41/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized ConnectionServer getInstance() {
        if (instance == null) {
            instance = new ConnectionServer();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }
}