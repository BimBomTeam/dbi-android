package BimBom.DBI;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class ImageDataModel {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Activity activity;  // Dodana referencja do Activity

    public ImageDataModel(Activity activity) {
        this.activity = activity;
        this.sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
    }

}