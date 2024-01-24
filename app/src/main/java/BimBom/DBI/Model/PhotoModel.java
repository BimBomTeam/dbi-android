package BimBom.DBI.Model;

import android.graphics.Bitmap;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class PhotoModel {
    private String base64;

    public PhotoModel(Bitmap imageView) {
        this.base64 = convertImageToBase64(imageView);
    }

    private String convertImageToBase64(Bitmap imageView) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageView.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public String getBase64Image() {
        return base64;
    }
}