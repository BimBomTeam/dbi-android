package BimBom.DBI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

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
    private Button btnHistory;
    private Bitmap photo;
    private Bitmap unscaledPhoto;
    private NavigationView nvMenu;
    private Dialog progressDialog;
    private MenuItem menu_item_login;
    private MenuItem menu_item_settings;
    private MenuItem menu_item_help;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        PhotoViewModel photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);
        photoViewModel.getIdentifyResponseLiveData().observe(this, identifyResponseDto -> {
            if (identifyResponseDto != null) {
                progressDialog.dismiss();
                Intent intent = new Intent(MainActivity.this, DogBreedActivity.class);
                intent.putExtra("photoBitmap", photo);
                intent.putExtra("dogName", identifyResponseDto.name);
                intent.putExtra("dogDescription", identifyResponseDto.description);
                startActivity(intent);
            }
        });
        photoViewModel.getErrorLiveData().observe(this, errorMessage -> {
            if (errorMessage != null) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initializeViews() {
        progressDialog = new Dialog(this);
        progressDialog.setContentView(R.layout.progress_dialog);


        if (progressDialog.getWindow() != null) {
            Drawable  drawable = getResources().getDrawable(R.drawable.rounded_progress_dialog);
            progressDialog.getWindow().setBackgroundDrawable(drawable);
        }

        imageView = findViewById(R.id.imageView);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        btnUpload = findViewById(R.id.btnUpload);
        btnMenu = findViewById(R.id.btnMenu);
        btnHistory = findViewById(R.id.btnHistory);
        nvMenu = findViewById(R.id.nvMenu);
        menu_item_login = findViewById(R.id.menu_item_login);
        menu_item_settings = findViewById(R.id.menu_item_settings);
        menu_item_help = findViewById(R.id.menu_item_help);

        setButtonClickListeners();


        setNavigationViewListener();
    }
    private void setButtonClickListeners() {
        setClickListener(btnUpload, this::onClickButtonUpload);
        setClickListener(btnHistory, this::onClickButtonHistory);
        setClickListener(btnMenu, this::onClickButtonMenu);
        setClickListener(btnGallery, this::onClickButtonGallery);
        setClickListener(btnCamera, this::onClickButtonCamera);
    }
    private void setClickListener(View view, View.OnClickListener listener) {
        view.setOnClickListener(listener);
    }
    private void setNavigationViewListener() {
        nvMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

                if (id == R.id.menu_item_help) {
                    Intent helpIntent = new Intent(MainActivity.this, HelpActivity.class);
                    startActivity(helpIntent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.menu_item_settings) {
                    Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (id == R.id.menu_item_login) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void onClickButtonUpload(View view) {
        if (photo != null) {
            progressDialog.show();
            PhotoModel photoModel = new PhotoModel(unscaledPhoto);
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

    private void onClickButtonHistory(View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dogs_history, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void onClickButtonMenu(View view) {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.openDrawer(nvMenu);
    }

    private void onClickButtonGallery(View view) {
        if (EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openGallery();
        } else {
            requestStoragePermission();
        }
    }

    private void onClickButtonCamera(View view) {
        checkCameraPermission();
    }


    private void checkCameraPermission() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            openCamera();
        } else {
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, CAMERA_PERMISSION_REQUEST_CODE, perms)
                            .setRationale(R.string.cameraPermission)
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
                    unscaledPhoto = (Bitmap) extras.get("data");
                    setScaledImage(photo);
                }

            } else if (requestCode == REQUEST_GALLERY && data != null) {
                Uri selectedImage = data.getData();
                try {
                    photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    unscaledPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    setScaledImage(photo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setScaledImage(Bitmap originalBitmap) {
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();

        int newWidth;
        int newHeight;

        if (originalWidth > originalHeight) {
            newWidth = 800;
            newHeight = (int) ((float) originalHeight / originalWidth * newWidth);
        } else {
            newHeight = 800;
            newWidth = (int) ((float) originalWidth / originalHeight * newHeight);
        }

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
                Toast.makeText(this, R.string.PermissionExternalmemory, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            Toast.makeText(this, R.string.noHavePermissionCamera, Toast.LENGTH_SHORT).show();
        } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            Toast.makeText(this, R.string.noHavePermissionGallery, Toast.LENGTH_SHORT).show();
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