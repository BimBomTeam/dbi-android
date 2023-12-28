package BimBom.DBI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

        Bitmap photoBitmap = getIntent().getParcelableExtra("photoBitmap");
        if (photoBitmap != null) {
            ivPhoto.setImageBitmap(photoBitmap);
        }
        String dogName = getIntent().getStringExtra("dogName");
        if (dogName != null) {
            tvName.setText(dogName);
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}