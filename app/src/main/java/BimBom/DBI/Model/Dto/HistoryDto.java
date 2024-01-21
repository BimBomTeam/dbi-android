package BimBom.DBI.Model.Dto;

import java.util.Date;

public class HistoryDto {
    private int Id;
    private int dogBreedId;
    private String dogBreedName;
    private Date createdDate;
    private String avatarLink;

    public HistoryDto(int id, int dogBreedId, String dogBreedName, Date createdDate, String avatarLink) {
        this.Id = id;
        this.dogBreedId = dogBreedId;
        this.dogBreedName = dogBreedName;
        this.createdDate = createdDate;
        this.avatarLink = avatarLink;
    }

    public String getDogBreedName() {
        return dogBreedName;
    }

    public void setDogBreedName(String dogBreedName) {
        this.dogBreedName = dogBreedName;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getDogBreedId() {
        return dogBreedId;
    }

    public void setDogBreedId(int dogBreedId) {
        this.dogBreedId = dogBreedId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }
}
