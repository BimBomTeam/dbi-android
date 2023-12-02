package BimBom.DBI.Model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PhotoModel {
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Activity activity;  // Dodana referencja do Activity

    public PhotoModel(Activity activity) {
        this.activity = activity;
        this.sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
    }

}