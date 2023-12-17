package BimBom.DBI;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;

import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;

import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import BimBom.DBI.Model.Dto.IdentifyResponseDto;
import BimBom.DBI.Model.PhotoModel;
import BimBom.DBI.Utils.SslHelper;
import BimBom.DBI.ViewModel.PhotoViewModel;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 2;
    private static final int REQUEST_STORAGE_PERMISSION = 3;

    private ImageView imageView;
    private Button btnCamera;
    private Button btnGallery;
    private Button btnUpload;
    private Button btnMenu;
    private Bitmap photo;
    private TextView twResult;
    private NavigationView nvMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        btnUpload = findViewById(R.id.btnUpload);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        twResult = findViewById(R.id.tvResult);
        nvMenu = findViewById(R.id.nvMenu);
        btnMenu = findViewById(R.id.btnMenu);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    openGallery();
                } else {
                    requestStoragePermission();
                }
            }
        });
        PhotoViewModel photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
        photoViewModel.getIdentifyResponseLiveData().observe(this, identifyResponseDto -> {
            if (identifyResponseDto != null) {
                twResult.setVisibility(View.VISIBLE);
                twResult.setText(identifyResponseDto.name);
            }
        });
        photoViewModel.getErrorLiveData().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        photoViewModel.getProgressBarVisibility().observe(this, visible -> {
            if (visible != null) {
                if (visible) {
                    progressBar.setVisibility(View.VISIBLE);
                } else {

                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageView.getDrawable() != null) {
                    PhotoModel photoModel = new PhotoModel(imageView);
                    PhotoViewModel photoViewModel = new ViewModelProvider(MainActivity.this).get(PhotoViewModel.class);
                    Pair<SSLContext, X509TrustManager> sslPair = SslHelper.createSSLContext(getApplicationContext());
                    SSLContext sslContext = sslPair.first;
                    X509TrustManager trustManager = sslPair.second;

                    photoViewModel.setSslContext(sslContext);
                    photoViewModel.setTrustManager(trustManager);
                    photoViewModel.setPhoto(photoModel);

                } else {
                    Toast.makeText(MainActivity.this, "Proszę wybrać zdjęcie", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(nvMenu);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    photo = (Bitmap) extras.get("data");
                    setScaledImage(photo);
                }
            } else if (requestCode == REQUEST_GALLERY && data != null) {
                Uri selectedImage = data.getData();
                try {
                    photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    setScaledImage(photo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setScaledImage(Bitmap originalBitmap) {
        int newWidth = 800;
        int newHeight = 800;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
        imageView.setImageBitmap(scaledBitmap);
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
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            openCamera();
        } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                openGallery();
            } else {
                Toast.makeText(this, "Brak uprawnień do odczytu z pamięci zewnętrznej", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            Toast.makeText(this, "Nie można otworzyć aparatu bez uprawnień", Toast.LENGTH_SHORT).show();
        } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            Toast.makeText(this, "Nie można otworzyć galerii bez uprawnień", Toast.LENGTH_SHORT).show();
        }
    }
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                openGallery();
            });

    private void requestStoragePermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }
}
