package BimBom.DBI.Service;
import java.util.List;

import BimBom.DBI.Model.Dto.HistoryDto;
import BimBom.DBI.Model.Dto.IdentifyRequestDto;
import BimBom.DBI.Model.Dto.IdentifyResponseDto;
import BimBom.DBI.Model.Dto.LoginResponseDto;
import BimBom.DBI.Model.Dto.UserCredential;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    @POST("api/DBI/identify")
    Call<IdentifyResponseDto> uploadPhoto(@Body IdentifyRequestDto requestDto);

    @POST("api/Auth/login")
    Call<LoginResponseDto> loginUser(@Body UserCredential userCredential);

    @POST("api/Auth/register")
    Call<LoginResponseDto> registerUser(@Body UserCredential userCredential);

    @GET("api/History/get-all")
    Call<List<HistoryDto>> downloadHistory();

    @DELETE("api/History/delete/{id}")
    Call<Void> deleteHistory(@Path("id") int id);
}