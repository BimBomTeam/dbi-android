package BimBom.DBI.Model;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class UserModel {
    private String email;
    private String uid;

    public UserModel(String email, String uid) {
        this.email = email;
        this.uid = uid;
    }


    public String generateJwtToken() {
        String secretKey = "eyJhbGciOiJIUzUxMiJ9meyJzdWIiOiJudkNvaDQ0bkxRaDM1OWR4WlpHdWViUDJjY0QyIn0mQlESTFmpgHVbUCUIQml5NaCMMJSFQWFuMmbf8iFvV1vdZsZrKuJ9bXgE6Ha63hD8LT6gLwDkA3te0xcg8GaFlQ";

        return Jwts.builder()
                .setSubject(this.uid)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}