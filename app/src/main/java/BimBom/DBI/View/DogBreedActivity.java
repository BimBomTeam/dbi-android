package BimBom.DBI.View;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import java.io.InputStream;
import java.util.EnumSet;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Base64;

import BimBom.DBI.R;
import BimBom.DBI.Service.ImageDownloadService;
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

        Bitmap avatar = getIntent().getParcelableExtra("avatar");
        if (avatar != null){
            ivPhoto.setImageBitmap(avatar);
        }
        String dogName = getIntent().getStringExtra("dogName");
        if (dogName != null) {
            tvName.setText(dogName);
        }

        String dogDescription = getIntent().getStringExtra("dogDescription");
        if (dogDescription != null) {
            tvDescription.setText(dogDescription);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ImageDownloadService imageDownloadService = new ImageDownloadService();
        imageDownloadService.downloadImage();
    }
}
