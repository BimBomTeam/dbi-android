package BimBom.DBI.ApiService;

import BimBom.DBI.Model.Dto.IdentifyRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/DBI/identify")
    Call<String> uploadPhoto(@Body IdentifyRequestDto requestDto);
}