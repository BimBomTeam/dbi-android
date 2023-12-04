package BimBom.DBI;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 2;

    private ImageView imageView;
    private Button btnCamera;
    private Button btnGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.CAMERA)) {
                    openGallery();
                } else {
                    Toast.makeText(MainActivity.this, "Nie można otworzyć galerii bez uprawnień do aparatu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkCameraPermission() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            openCamera();
        } else {
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, CAMERA_PERMISSION_REQUEST_CODE, perms)
                            .setRationale("Aplikacja potrzebuje dostępu do aparatu")
                            .setPositiveButtonText("OK")
                            .setNegativeButtonText("Anuluj")
                            .build()
            );
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // Użytkownik przyznał uprawnienia
        Log.d("MainActivity", "Uprawnienia przyznane");
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            openCamera();
        } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                openGallery();
            } else {
                Toast.makeText(this, "Nie masz uprawnień do odczytu z pamięci zewnętrznej", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // Użytkownik odmówił uprawnień
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            Toast.makeText(this, "Nie można otworzyć aparatu bez uprawnień", Toast.LENGTH_SHORT).show();
        } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            Toast.makeText(this, "Nie można otworzyć galerii bez uprawnień", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && data != null && data.getExtras() != null) {
                // Zdjęcie z aparatu
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    imageView.setImageBitmap(photo);
                }
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                // Zdjęcie z galerii
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    imageView.setImageBitmap(photo);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("MainActivity", "Błąd przy wczytywaniu zdjęcia z galerii", e);
                }
            }
        }
    }
}