package BimBom.DBI.View;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import BimBom.DBI.R;

public class DogBreedActivity extends AppCompatActivity {
    private TextView tvName;
    private TextView tvDescription;
    private Button btnBack;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_breed);
        initializeViews();
        setupWebView();
        displayDogInformation();
        setButtonClickListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        webView = findViewById(R.id.wvAvatar);
        tvName = findViewById(R.id.tvName);
        tvDescription = findViewById(R.id.tvDescription);
    }

    private void setupWebView() {
        webView.loadUrl("http://193.122.12.41/image.png");
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setTextZoom(100);
    }

    private void displayDogInformation() {
        String dogName = getIntent().getStringExtra("dogName");
        if (dogName != null) {
            tvName.setText(dogName);
        }

        String dogDescription = getIntent().getStringExtra("dogDescription");
        if (dogDescription != null) {
            tvDescription.setText(dogDescription);
        }
    }

    private void setButtonClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
