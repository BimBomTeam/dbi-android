package BimBom.DBI.Model.Dto;

import java.util.Date;

public class HistoryDto {
    private int dogBreedId;
    private String userId;
    private Date createdDate;

    public int getDogBreedId() {
        return dogBreedId;
    }

    public void setDogBreedId(int dogBreedId) {
        this.dogBreedId = dogBreedId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
