package BimBom.DBI.View;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import BimBom.DBI.R;

public class HistoryActivity extends AppCompatActivity {
    private Button btnBack;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerView);

        List<Dog> dogList = generateSampleDogList();

        historyAdapter = new HistoryAdapter(dogList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(historyAdapter);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private List<Dog> generateSampleDogList() {
        List<Dog> sampleDogList = new ArrayList<>();
        sampleDogList.add(new Dog("Labrador Retriever", "http://193.122.12.41/image.png"));
        sampleDogList.add(new Dog("German Shepherd", "http://193.122.12.41/image.png"));
        sampleDogList.add(new Dog("Golden Retriever", "http://193.122.12.41/image.png"));
        sampleDogList.add(new Dog("Golden Retriever", "http://193.122.12.41/image.png"));
        sampleDogList.add(new Dog("Golden Retriever", "http://193.122.12.41/image.png"));
        sampleDogList.add(new Dog("Golden Retriever", "http://193.122.12.41/image.png"));
        return sampleDogList;
    }
}
