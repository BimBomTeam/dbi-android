package BimBom.DBI.View;

import java.util.Date;

public class Dog {
    private String dogBreed;
    private String dogAvatar;

    private String dogDate;

    public Dog(String dogBreed, String dogAvatar,String dogDate) {
        this.dogBreed = dogBreed;
        this.dogAvatar = dogAvatar;
        this.dogDate = dogDate;
    }

    public String getDogDate() {
        return dogDate;
    }

    public void setDogDate(String dogDate) {
        this.dogDate = dogDate;
    }

    public String getDogBreed() {
        return dogBreed;
    }

    public void setDogBreed(String dogBreed) {
        this.dogBreed = dogBreed;
    }

    public String getDogAvatar() {
        return dogAvatar;
    }

    public void setDogAvatar(String dogAvatar) {
        this.dogAvatar = dogAvatar;
    }
}
