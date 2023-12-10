package BimBom.DBI.Utils;

import android.content.Context;
import android.util.Pair;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import BimBom.DBI.R;

public class SslHelper {

    public static Pair<SSLContext, X509TrustManager> createSSLContext(Context context) {
        try {
            // Załaduj certyfikat
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context.getResources().openRawResource(R.raw.cert); // Zastąp 'your_certificate' nazwą pliku certyfikatu
            X509Certificate ca = (X509Certificate) cf.generateCertificate(caInput);
            caInput.close();

            // Stwórz KeyStore zawierający nasz certyfikat
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Użyj KeyStore do stworzenia TrustManager
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Stwórz SSLContext z TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            X509TrustManager trustManager = null;
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    trustManager = (X509TrustManager) tm;
                    break;
                }
            }

            if (trustManager == null) {
                throw new IllegalStateException("Nie znaleziono X509TrustManager");
            }

            return new Pair<>(sslContext, trustManager);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
