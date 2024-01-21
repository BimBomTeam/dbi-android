package BimBom.DBI.Service;

public class TokenManager {
    private static String jwtToken;

    public static void setToken(String token) {
        jwtToken = token;
    }

}
