package BimBom.DBI.Service;


import android.content.Context;

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
        String jwtToken = jwtManager.getStoredJwtToken();

        if (jwtToken != null) {
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + jwtToken)
                    .build();
            return chain.proceed(newRequest);
        }

        return chain.proceed(originalRequest);
    }
}
