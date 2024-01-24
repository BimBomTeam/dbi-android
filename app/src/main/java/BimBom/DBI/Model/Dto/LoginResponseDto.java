package BimBom.DBI.Model.Dto;

public class LoginResponseDto {
    public String token;

    public String getToken() {
        return token;
    }

    public LoginResponseDto(String token) {
        this.token = token;
    }
}
