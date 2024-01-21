package BimBom.DBI.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import BimBom.DBI.Model.Dto.HistoryDto;
import BimBom.DBI.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.DogViewHolder> {

    private List<HistoryDto> dogList;
    private Context context;


    public HistoryAdapter(List<HistoryDto> dogList, Context context) {
        this.dogList = dogList;
        this.context = context;
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
        holder.dogDateTextView.setText(dog.getCreatedDate().toString());
        WebSettings webSettings = holder.dogAvatarWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setTextZoom(100);

        holder.dogAvatarWebView.setWebViewClient(new WebViewClient());
        holder.dogAvatarWebView.loadUrl("http://193.122.12.41/"+dog.getAvatarLink());

    }


    @Override
    public int getItemCount() {
        return dogList != null ? dogList.size() : 0;
    }

    public static class DogViewHolder extends RecyclerView.ViewHolder {
        TextView dogBreedTextView;
        WebView dogAvatarWebView;

        TextView dogDateTextView;

        public DogViewHolder(@NonNull View itemView) {
            super(itemView);
            dogDateTextView=itemView.findViewById(R.id.tvDate);
            dogBreedTextView = itemView.findViewById(R.id.tvDogBreed);
            dogAvatarWebView = itemView.findViewById(R.id.wvAvatar);
        }
    }
    public void removeDog(int position) {
        if (position >= 0 && position < dogList.size()) {
            dogList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
