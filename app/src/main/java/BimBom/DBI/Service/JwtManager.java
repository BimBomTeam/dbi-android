package BimBom.DBI.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JwtManager {

    private static final String TAG = JwtManager.class.getSimpleName();
    private static final String PREF_NAME = "jwt_pref";
    private static final String KEY_JWT_TOKEN = "jwt_token";

    private Context context;

    public JwtManager(Context context) {
        this.context = context;
    }

    public String getJwtToken() {
        String storedToken = getStoredJwtToken();
        if (storedToken != null) {
            return storedToken;
        }
        return null;
    }


    public String getStoredJwtToken() {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_JWT_TOKEN, null);
    }

    public void saveJwtTokenToPreferences(String jwtToken) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_JWT_TOKEN, jwtToken);
        editor.apply();
    }

    public boolean hasJwtToken() {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String jwtToken = preferences.getString(KEY_JWT_TOKEN, null);
        return jwtToken != null && !jwtToken.isEmpty();
    }
    public void clearJwtToken() {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_JWT_TOKEN);
        editor.apply();
    }

    private class FetchTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String serverUrl = params[0];
            String jwtToken = null;

            try {
                URL url = new URL(serverUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    InputStream in = urlConnection.getInputStream();
                    jwtToken = readStream(in);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error fetching JWT token from server", e);
            }

            return jwtToken;
        }

        @Override
        protected void onPostExecute(String jwtToken) {
            if (jwtToken != null) {
                saveJwtTokenToPreferences(jwtToken);
                Log.d(TAG, "JWT token saved: " + jwtToken);
            } else {
                Log.e(TAG, "Failed to retrieve JWT token from server");
            }
        }

        private String readStream(InputStream in) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            reader.close();
            return stringBuilder.toString();
        }
    }
}
