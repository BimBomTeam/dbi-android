package BimBom.DBI.Service;

import android.content.Context;
import android.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

public class JwtDecoder {

    private static Context context;

    public static String[] decodeJwt(String jwtToken) {
        String[] parts = jwtToken.split("\\.");

        try {
            String headerJson = decodeBase64Url(parts[0]);
            String payloadJson = decodeBase64Url(parts[1]);

            return new String[]{headerJson, payloadJson};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decodeBase64Url(String base64Url) {
        byte[] decodedBytes = Base64.decode(base64Url, Base64.URL_SAFE);
        return new String(decodedBytes);
    }

    public static void main(String[] args) {
        JwtManager jwtManager = new JwtManager(context);
        jwtManager.retrieveJwtTokenFromServer("http://example.com/getToken");
        String storedJwtToken = jwtManager.getStoredJwtToken();
        String[] decodedData = decodeJwt(storedJwtToken);

        if (decodedData != null) {
            String headerJson = decodedData[0];
            String payloadJson = decodedData[1];

            System.out.println("Decoded Header: " + headerJson);
            System.out.println("Decoded Payload: " + payloadJson);
        } else {
            System.out.println("Failed to decode JWT");
        }
    }
}
