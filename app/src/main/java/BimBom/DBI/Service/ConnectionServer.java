package BimBom.DBI.Service;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import BimBom.DBI.Utils.UnsafeOkHttpClient;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConnectionServer {
    private static ConnectionServer instance;
    private ApiService apiService;
    private Context context;


    private ConnectionServer(Context context) {
        this.context = context;

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = UnsafeOkHttpClient.getUnsafeOkHttpClient().newBuilder();
        clientBuilder.readTimeout(200, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(200, TimeUnit.SECONDS);

        // Create JwtInterceptor instance and add it to OkHttpClient
        JwtInterceptor jwtInterceptor = new JwtInterceptor(context); // Pass the context here
        clientBuilder.addInterceptor(jwtInterceptor);

        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://193.122.12.41/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized ConnectionServer getInstance(Context context) {
        if (instance == null) {
            instance = new ConnectionServer(context);
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }
}
