package BimBom.DBI;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageDataModel imageDataModel;
    private Button upload,camera,gallery;
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        imageDataModel = new ImageDataModel(this);
    }

    private void initView(){
        upload= findViewById(R.id.btnUpload);
        camera= findViewById(R.id.btnCamera);
        gallery= findViewById(R.id.btnGallery);
    }
    public void  showError(String  error){

    }
}