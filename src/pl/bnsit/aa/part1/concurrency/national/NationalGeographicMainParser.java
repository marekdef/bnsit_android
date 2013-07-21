package pl.bnsit.aa.part1.concurrency.national;

import android.os.Handler;
import android.util.Log;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NationalGeographicMainParser implements Runnable {
    public static final String URL = "http://www.national-geographic.pl/foto/najpopularniejsze";
    private static final String TAG = NationalGeographicMainParser.class.getSimpleName();
    private final ExecutorService executorService;
    private final File storeDirectory;
    private Handler handler;

    public NationalGeographicMainParser(Handler handler, File filesDir) {
        executorService = Executors.newFixedThreadPool(4);
        this.storeDirectory = filesDir;
        this.handler = handler;
    }

    @Override
    public void run() {
        final Connection connect = Jsoup.connect(URL);

        try {
            handler.obtainMessage(NationalGeographicExample.MESSAGE_INFO, "Connecting to national geographic").sendToTarget();
            final Connection.Response execute = connect.execute();
            handler.obtainMessage(NationalGeographicExample.MESSAGE_INFO, "Parsing webpage").sendToTarget();
            final Document parse = execute.parse();

            final Elements elementsByAttributeValue = parse.getElementsByTag("img");

            handler.obtainMessage(NationalGeographicExample.MESSAGE_INFO, "Extracting images").sendToTarget();
            for(Element e: elementsByAttributeValue) {
                String src = e.attr("src");
                if(!src.startsWith("http"))
                    continue;
                executorService.submit(new NationalGeographicImageFetcher(storeDirectory, src, handler));
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
        }

        try {
            executorService.shutdown();
            while(!executorService.awaitTermination(5, TimeUnit.SECONDS));
        } catch (InterruptedException e) {

        }
        handler.obtainMessage(NationalGeographicExample.MESSAGE_DONE, "Done").sendToTarget();
    }
}


class NationalGeographicImageFetcher implements Runnable {

    private static final String TAG = NationalGeographicImageFetcher.class.getSimpleName();
    public static final int EOF = -1;
    public static final int BUFFER_SIZE = 1024;
    private final String src;
    private final File filesDir;
    private final Handler handler;

    public NationalGeographicImageFetcher(File filesDir, String src, Handler handler) {
        this.src = src;
        this.filesDir = filesDir;
        this.handler = handler;
    }

    @Override
    public void run() {

        URL url = null;
        try {
            url = new URL(src);

            String file = new File(url.getFile()).getName();


            File destination = new File(filesDir, file);
//            if(alreadyStored(destination))
//                return;

            FileOutputStream fileWriter = null;
            InputStream inputStream = null;
            try {
                fileWriter = new FileOutputStream(destination);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                inputStream = urlConnection.getInputStream();

                byte[] buffer = new byte[BUFFER_SIZE];
                int readBytes = 0;
                while ((readBytes = inputStream.read(buffer)) != EOF)
                    fileWriter.write(buffer, 0, readBytes);

                handler.obtainMessage(NationalGeographicExample.MESSAGE_READY, file).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "IOException", e);
            } finally {
                closeQuietly(inputStream);
                closeQuietly(fileWriter);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException", e);
        } finally {

        }

    }

    private boolean alreadyStored(File destination) {
        return destination.exists() && destination.length() > 0;
    }

    private void closeQuietly(Closeable closeable) {
        if(closeable != null)
            try {
                closeable.close();
            } catch (IOException ignore) {

            }

    }
}