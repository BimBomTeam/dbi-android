package BimBom.DBI.Service;


import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class JwtInterceptor implements Interceptor {

    private JwtManager jwtManager;

    public JwtInterceptor(Context context) {
        this.jwtManager = new JwtManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String jwtToken = jwtManager.getJwtToken();

        Log.d("interceptor", "Intercepting request with JWT token: " + jwtToken);//to  do

        if (jwtToken != null) {
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + jwtToken)
                    .build();
            return chain.proceed(newRequest);
        }

        return chain.proceed(originalRequest);
    }
}
