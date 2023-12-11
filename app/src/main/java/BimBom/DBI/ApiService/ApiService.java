package BimBom.DBI.ApiService;
import BimBom.DBI.Model.Dto.IdentifyRequestDto;
import BimBom.DBI.Model.Dto.IdentifyResponseDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/DBI/identify")
    Call<IdentifyResponseDto> uploadPhoto(@Body IdentifyRequestDto requestDto);
}