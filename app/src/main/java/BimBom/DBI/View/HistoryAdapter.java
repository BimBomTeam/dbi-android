package BimBom.DBI.View;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import BimBom.DBI.Model.Dto.HistoryDto;
import BimBom.DBI.R;
import BimBom.DBI.Service.ApiService;
import BimBom.DBI.Service.ConnectionServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.DogViewHolder> {

    private List<HistoryDto> dogList;
    private Context context;
    private SimpleDateFormat dateFormat;
    private TextView tvHistoryInfo;

    public HistoryAdapter(List<HistoryDto> dogList, Context context, TextView tvHistoryInfo) {
        this.dogList = dogList;
        this.context = context;
        this.tvHistoryInfo = tvHistoryInfo;
        this.dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_dog, parent, false);
        return new DogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogViewHolder holder, int position) {
        HistoryDto dog = dogList.get(position);
        holder.dogBreedTextView.setText(dog.getDogBreedName());
        String formattedDate = dateFormat.format(dog.getCreatedDate());
        holder.dogDateTextView.setText(formattedDate);

        WebSettings webSettings = holder.dogAvatarWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setTextZoom(100);

        holder.dogAvatarWebView.setWebViewClient(new WebViewClient());
        holder.dogAvatarWebView.loadUrl("http://193.122.12.41/" + dog.getAvatarLink());
    }

    @Override
    public int getItemCount() {
        int itemCount = dogList != null ? dogList.size() : 0;

        if (dogList != null && dogList.isEmpty()) {
            tvHistoryInfo.setVisibility(View.VISIBLE);
        } else {
            tvHistoryInfo.setVisibility(View.INVISIBLE);
        }

        return itemCount;
    }


    public static class DogViewHolder extends RecyclerView.ViewHolder {
        TextView dogBreedTextView;
        WebView dogAvatarWebView;
        TextView dogDateTextView;

        public DogViewHolder(@NonNull View itemView) {
            super(itemView);
            dogDateTextView = itemView.findViewById(R.id.tvDate);
            dogBreedTextView = itemView.findViewById(R.id.tvDogBreed);
            dogAvatarWebView = itemView.findViewById(R.id.wvAvatar);
        }
    }

    public void removeDog(int position) {
        Log.d("REMOVE_DOG", "Received position: " + position);
        if (position >= 0 && position < dogList.size()) {
            int historyId = dogList.get(position).getId();

            ConnectionServer connectionServer = ConnectionServer.getInstance(context);
            ApiService apiService = connectionServer.getApiService();

            Call<Void> call = apiService.deleteHistory(historyId);
            call.enqueue(new Callback<Void>() {
                //TODO
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        dogList.remove(position);
                        notifyItemRemoved(position);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("API_CALL_FAILURE", "Call failed: " + t.getMessage(), t);
                }
            });
        }
    }
}

