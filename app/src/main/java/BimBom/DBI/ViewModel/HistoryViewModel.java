package BimBom.DBI.ViewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import BimBom.DBI.Model.Dto.HistoryDto;
import BimBom.DBI.Model.Dto.LoginResponseDto;
import BimBom.DBI.Model.Dto.UserCredential;
import BimBom.DBI.Service.ApiService;
import BimBom.DBI.Service.ConnectionServer;
import BimBom.DBI.Service.HistoryCallback;
import BimBom.DBI.Service.JwtManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryViewModel extends ViewModel {
    MutableLiveData<List<HistoryDto>> historyResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<List<HistoryDto>> getHistoryResponseLiveData() {
        return historyResponseLiveData;
    }
    Context context;
    public void setContext(Context context) {
        this.context = context;
    }
    public HistoryViewModel() {
    }

    public HistoryViewModel(Context context) {
        this.context = context;
    }

    public void loginUser(HistoryCallback callback) {
        ConnectionServer connectionServer = ConnectionServer.getInstance(context);
        ApiService apiService = connectionServer.getApiService();

        Call<List<HistoryDto>> call = apiService.downloadHistory();
        call.enqueue(new Callback<List<HistoryDto>>() {
            @Override
            public void onResponse(Call<List<HistoryDto>> call, Response<List<HistoryDto>> response) {
                if (response.isSuccessful()) {
                    List<HistoryDto> historyList = response.body();
                    if (historyList != null) {
                        historyResponseLiveData.setValue(historyList);
                        callback.onHistoryLoaded(historyList); // Wywołaj callback po pomyślnym pobraniu danych.
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<List<HistoryDto>> call, Throwable t) {
                Log.e("Error", "Błąd podczas logowania: " + t.getMessage());
            }
        });
    }

}
