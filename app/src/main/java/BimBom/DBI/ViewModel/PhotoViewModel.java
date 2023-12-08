package BimBom.DBI;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.ByteArrayOutputStream;

import BimBom.DBI.ApiService.ApiService;
import BimBom.DBI.Model.PhotoModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoViewModel extends ViewModel {
    private MutableLiveData<PhotoModel> selectedPhoto = new MutableLiveData<>();
    private MutableLiveData<String> uploadStatus = new MutableLiveData<>();

    public MutableLiveData<String> getUploadStatus() {
        return uploadStatus;
    }

    public MutableLiveData<PhotoModel> getSelectedPhoto() {
        if (selectedPhoto.getValue() == null) {
            // Jeśli selectedPhoto jest null, zainicjuj nowy obiekt PhotoModel
            selectedPhoto.setValue(new PhotoModel(""));
        }
        return selectedPhoto;
    }

    public void setPhoto(String imagePath) {
        // Convert the image to Base64
        String base64Image = convertImageToBase64(imagePath);

        // Send the Base64 image to the REST API
        sendImageToApi(base64Image);
    }

    private String convertImageToBase64(String imagePath) {
        // Convert the image to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Load the image from file
        Bitmap photo = BitmapFactory.decodeFile(imagePath);

        if (photo != null) {
            // Convert byte array to Base64
            photo.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            // Handle the case when the image couldn't be loaded
            // For now, return an empty string, but you might want to handle it differently based on your requirements.
            return "";
        }
    }

    private void sendImageToApi(String base64Image) {
        // Retrofit setup
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://localhost:7219/swagger/index.html/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create API service
        ApiService apiService = retrofit.create(ApiService.class);

        // Create PhotoModel with Base64 image
        PhotoModel photoModel = new PhotoModel(base64Image);

        // Send the photo to the API
        Call<String> call = apiService.uploadPhoto(photoModel);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    uploadStatus.setValue("Zdjęcie zostało pomyślnie przesłane");
                } else {
                    uploadStatus.setValue("Błąd podczas przesyłania zdjęcia");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                uploadStatus.setValue("Błąd podczas przesyłania zdjęcia: " + t.getMessage());
            }
        });
    }
}