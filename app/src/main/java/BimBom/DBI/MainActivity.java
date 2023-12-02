package BimBom.DBI;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import BimBom.DBI.Model.PhotoModel;

public class MainActivity extends AppCompatActivity {

    private PhotoModel photoModel;
    private Button upload,camera,gallery;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        photoModel = new PhotoModel(this);
    }

    private void initView(){
        upload= findViewById(R.id.btnUpload);
        camera= findViewById(R.id.btnCamera);
        gallery= findViewById(R.id.btnGallery);
    }
    public void  showError(String  error){

    }
}