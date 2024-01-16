package BimBom.DBI;

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
    private Bitmap bitmap;
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


        String dogName = getIntent().getStringExtra("dogName");
        if (dogName != null) {
            tvName.setText(dogName);
        }

        }
        String dogDescription = getIntent().getStringExtra("dogDescription");
        if (dogDescription != null){
            tvDescription.setText(dogDescription);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new SmbConnectionTask().execute();
    }

    private class SmbConnectionTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            SMBClient client = new SMBClient();
            try (Connection connection = client.connect("130.162.37.11")) {
                AuthenticationContext ac = new AuthenticationContext("ubuntu", "123".toCharArray(), "");
                Session session = connection.authenticate(ac);


                try (DiskShare share = (DiskShare) session.connectShare("sambashare")) {
                    String filePath = "image.png";
                    try (InputStream in = share.openFile(filePath, EnumSet.of(AccessMask.FILE_READ_DATA), null, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OPEN, null).getInputStream()) {
                        bitmap = BitmapFactory.decodeStream(in);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ivPhoto.setImageBitmap(bitmap);
        }
    }
}
