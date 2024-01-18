package BimBom.DBI.View;

public class Dog {
    private String dogBreed;
    private String dogAvatar;

    public Dog(String dogBreed, String dogAvatar) {
        this.dogBreed = dogBreed;
        this.dogAvatar = dogAvatar;
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
