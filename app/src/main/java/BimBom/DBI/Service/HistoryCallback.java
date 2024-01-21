package BimBom.DBI.Service;

import java.util.List;

import BimBom.DBI.Model.Dto.HistoryDto;

public interface HistoryCallback {
    void onHistoryLoaded(List<HistoryDto> historyList);
}