package BimBom.DBI.View;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
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
        initializeViews();
        setupRecyclerView();
        setupItemTouchHelper();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerView);
        setOnClickBtnBack();
    }

    private void setOnClickBtnBack() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupRecyclerView() {
        List<Dog> dogList = generateSampleDogList();
        historyAdapter = new HistoryAdapter(dogList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(historyAdapter);
    }

    private List<Dog> generateSampleDogList() {
        List<Dog> sampleDogList = new ArrayList<>();
        sampleDogList.add(new Dog("Labrador Retriever", "http://193.122.12.41/image.png","data"));
        sampleDogList.add(new Dog("German Shepherd", "http://193.122.12.41/image.png","data"));
        sampleDogList.add(new Dog("Golden Retriever", "http://193.122.12.41/image.png","data"));
        sampleDogList.add(new Dog("Golden Retriever", "http://193.122.12.41/image.png","data"));
        sampleDogList.add(new Dog("Golden Retriever", "http://193.122.12.41/image.png","data"));
        sampleDogList.add(new Dog("Golden Retriever", "http://193.122.12.41/image.png","data"));
        return sampleDogList;
    }

    private void setupItemTouchHelper() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            historyAdapter.removeDog(position);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            final double magicNumber = 0.25;
            View itemView = viewHolder.itemView;
            Paint paint = new Paint();

            paint.setColor(ContextCompat.getColor(recyclerView.getContext(), R.color.colorSwipeBackground));
            c.drawRect(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom(), paint);

            if (Math.abs(dX) >= itemView.getWidth() * magicNumber) {
                Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.trash);
                if (icon != null) {
                    int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconBottom = iconTop + icon.getIntrinsicHeight();

                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;

                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    icon.draw(c);
                }
            }
        }
    };
}
