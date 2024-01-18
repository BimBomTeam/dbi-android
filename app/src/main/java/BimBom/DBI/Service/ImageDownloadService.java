package BimBom.DBI.Service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ImageDownloadService {

    public Bitmap downloadImage(){
        Bitmap bitmap = null;
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<Bitmap>> resultList = new ArrayList<>();

        // Uruchomienie zadań i zapisanie wyników do listy Future
            Callable<Bitmap> worker = new SmbConnectionTask();
            Future<Bitmap> result = executor.submit(worker);
            resultList.add(result);


        // Oczekiwanie na zakończenie wszystkich zadań i pobieranie wyników
        for (Future<Bitmap> future : resultList) {
            try {
                bitmap = future.get();
                // Przetwarzanie pobranego Bitmap
            } catch (Exception e) {
                System.err.println("Wyjątek podczas pobierania wyniku: " + e.getMessage());
            }
        }

        // Zamknięcie ExecutorService
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            System.err.println("Zadania zostały przerwane: " + e.getMessage());
        }

        System.out.println("Zakończono wszystkie wątki.");
        return bitmap;
    }
    private class SmbConnectionTask implements Callable<Bitmap> {
        public Bitmap bitmap;

        @Override
        public Bitmap call() {
            SMBClient client = new SMBClient();
            try (Connection connection = client.connect("193.122.12.41")) {
                AuthenticationContext ac = new AuthenticationContext("ubuntu", "123".toCharArray(), "");
                Session session = connection.authenticate(ac);

                try (DiskShare share = (DiskShare) session.connectShare("sambashare")) {
                    String filePath = "image.png";
                    try (InputStream in = share.openFile(filePath, EnumSet.of(AccessMask.FILE_READ_DATA), null, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OPEN, null).getInputStream()) {
                        bitmap = BitmapFactory.decodeStream(in);
                        return bitmap;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

          // runOnUiThread(new Runnable() {
          //     @Override
          //     public void run() {
          //         strartDownload = true;
          //         dogBreedIntent.putExtra("avatar", bitmap);
          //     }
          // });
        }
    }
}
