package BimBom.DBI.ApiService;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/DBI/identify")
    Call<String> uploadPhoto(@Body String base64);
}