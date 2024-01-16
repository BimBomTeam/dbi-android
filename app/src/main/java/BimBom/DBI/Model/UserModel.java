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
        String secretKey = "nC4HGoTRMvgUAU52eHmhEMaQdpmpEwCj0wp6NdGbfqk";

        return Jwts.builder()
                .setSubject(this.uid)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}