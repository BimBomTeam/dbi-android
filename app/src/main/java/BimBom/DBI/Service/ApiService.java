package BimBom.DBI.Service;
import BimBom.DBI.Model.Dto.IdentifyRequestDto;
import BimBom.DBI.Model.Dto.IdentifyResponseDto;
import BimBom.DBI.Model.Dto.LoginResponseDto;
import BimBom.DBI.Model.Dto.UserCredential;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/DBI/identify")
    Call<IdentifyResponseDto> uploadPhoto(@Body IdentifyRequestDto requestDto);

    @POST("api/Auth/login")
    Call<LoginResponseDto> loginUser(@Body UserCredential userCredential);

    @POST("api/Auth/register")
    Call<LoginResponseDto> registerUser(@Body UserCredential userCredential);
}