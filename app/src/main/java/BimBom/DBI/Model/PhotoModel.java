package BimBom.DBI.Model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PhotoModel {
    private String imagePath;

    public PhotoModel(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
}