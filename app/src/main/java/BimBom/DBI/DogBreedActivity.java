package BimBom.DBI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Base64;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import BimBom.DBI.ViewModel.PhotoViewModel;

public class DogBreedActivity extends AppCompatActivity {
    private ImageView ivPhoto;
    private TextView tvName;
    private TextView tvDescription;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_breed);

        ivPhoto = findViewById(R.id.ivPhoto);
        tvName = findViewById(R.id.tvName);
        tvDescription = findViewById(R.id.tvDescription);
        btnBack = findViewById(R.id.btnBack);
        String user = "ubuntu";
        String pass = "123";
        String sharedFolder = "sambashare";
        String server = "smb://130.162.37.11/";
        String filePath = "image.png";

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", user, pass);
        String path = server + sharedFolder + "/" + filePath;


        try {
            SmbFile smbFile = new SmbFile(path, auth);
            SmbFileInputStream in = new SmbFileInputStream(smbFile);

            Bitmap bitmap = BitmapFactory.decodeStream(in);
            ivPhoto.setImageBitmap(bitmap);
            String dogName = getIntent().getStringExtra("dogName");
            if (dogName != null) {
                tvName.setText(dogName);

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}