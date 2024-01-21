package BimBom.DBI.Model.Dto;

public class IdentifyResponseDto {
    public String id;
    public String avatarLink;
    public String name;
    public String description;

    public IdentifyResponseDto(String id, String avatarLink, String name, String description) {
        this.id = id;
        this.avatarLink = avatarLink;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }
}
