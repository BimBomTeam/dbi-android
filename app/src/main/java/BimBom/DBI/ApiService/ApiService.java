package BimBom.DBI.ApiService;
import BimBom.DBI.Model.PhotoModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("uploadEndpoint")  // Zastąp to właściwym endpointem
    Call<String> uploadPhoto(@Body PhotoModel photoModel);
}