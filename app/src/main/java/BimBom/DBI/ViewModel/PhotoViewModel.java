package BimBom.DBI.ViewModel;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import BimBom.DBI.Model.PhotoModel;

public class PhotoViewModel {
    private MutableLiveData<PhotoModel> selectedPhoto = new MutableLiveData<>();

    public LiveData<PhotoModel> getSelectedPhoto() {
        return selectedPhoto;
    }

    public void setPhoto(String imagePath) {
        selectedPhoto.setValue(new PhotoModel(imagePath));
    }
}
