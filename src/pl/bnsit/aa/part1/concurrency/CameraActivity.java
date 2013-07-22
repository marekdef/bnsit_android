package pl.bnsit.aa.part1.concurrency;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import pl.bnsit.aa.R;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/21/13
 * Time: 8:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class CameraActivity extends Activity implements View.OnClickListener {
    private String CAM1 = "http://85.89.179.181/capture/ch2.mjpg";
    private String CAM2 = "http://85.89.179.179/capture/ch2.mjpg";
    private String CAM3 = "http://demo1.stardotcams.com/nph-mjpeg.cgi";
    private String CAM4 = "http://83.64.164.6/axis-cgi/mjpg/video.cgi?resolution=320x240";

    private CameraActivity.CameraDownloadAsyncTask cameraDownloadAsyncTask;
    private View button1;
    private View button2;
    private ImageView imageView;
    private TextView textViewProgress;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        imageView = (ImageView) findViewById(R.id.imageView);
        textViewProgress = (TextView) findViewById(R.id.textViewStatus);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        cancelPendingTask();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(button1)) {
            cancelPendingTask();
            executeDownloadTask(CAM1);
        } else if (v.equals(button2)) {
            cancelPendingTask();
            executeDownloadTask(CAM2);
        }
    }

    private void executeDownloadTask(String cameraUrl) {
        try {
            int width = getWindow().getDecorView().getWidth();
            int height = getWindow().getDecorView().getHeight();

            String absolutePath = File.createTempFile("download", "image", getCacheDir()).getAbsolutePath();

            executeDownloadTask(cameraUrl, width, height, absolutePath);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }

    private void executeDownloadTask(String cameraUrl, int width, int height, String absolutePath) {
        //TODO 1E-1 Create download task in UI thread
        //TODO 1E-2 Execute the task in download thread
    }

    private void cancelPendingTask() {
        if (cameraDownloadAsyncTask != null) {
            //TODO 1E-5 Please cancel the task
        }
        cameraDownloadAsyncTask = null;
    }

    private class CameraDownloadAsyncTask extends AsyncTask<String, String, Bitmap> {
        private static final int EOF = -1;
        private static final String TAG = "CameraDownloadAsyncTask";
        private final String tempPath;
        private int width;
        private int height;
        private int totalRead = 0;
        private int notified = 1024;

        public CameraDownloadAsyncTask(String tempPath, int width, int height) {
            this.tempPath = tempPath;
            this.width = width;
            this.height = height;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);

                URLConnection urlConnection = url.openConnection();

                if (publishProgressOrCancel("Opening connection")) return null;
                InputStream inputStream = urlConnection.getInputStream();

                if (publishProgressOrCancel("Reading header")) return null;

                String frameBoundary = readLine(inputStream);
                String contentType = readLine(inputStream);
                String contentLengthString = readLine(inputStream);
                String test = readLine(inputStream);

                if (publishProgressOrCancel("Copying image to tempFile")) return null;

                copyImageToFile(inputStream, frameBoundary);

                inputStream.close();


                if (publishProgressOrCancel("Decoding height and width")) return null;
                BitmapFactory.Options opts = new BitmapFactory.Options();
                decodeFileBounds(opts);

                int scaleFactor = getScaleFactor(opts.outWidth, opts.outHeight, width, height);

                if (publishProgressOrCancel(String.format("Scaling by %d!", scaleFactor))) return null;

                Bitmap bitmap = getBitmap(opts, scaleFactor);
                publishProgressOrCancel("Reading done!");
                deleteFile(this.tempPath);
                return bitmap;

            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException", e);
            } catch (IOException e) {
                Log.e(TAG, "IOException", e);
            }
            return null;
        }

        private void copyImageToFile(InputStream inputStream, String frameBoundary) throws IOException {
            FileOutputStream fos = new FileOutputStream(this.tempPath, false);
            byte[] buffer = new byte[frameBoundary.length() * 2];
            int read = 0;
            while (read != EOF && notReachedFrameBoundary(frameBoundary, buffer)) {
                read = inputStream.read(buffer, 0, buffer.length);
                updateBytesRead(read);
                if (read != EOF) {
                    fos.write(buffer, 0, read);
                }
            }
            fos.close();
        }

        private Bitmap getBitmap(BitmapFactory.Options opts, int scaleFactor) {
            opts.inJustDecodeBounds = false;
            opts.inScaled = scaleFactor != 1;
            opts.inSampleSize = scaleFactor;
            return BitmapFactory.decodeFile(tempPath, opts);
        }

        private void decodeFileBounds(BitmapFactory.Options opts) {
            opts.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(this.tempPath, opts);
        }

        private String readLine(InputStream is) throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();


            int read = 0;
            while ((read = is.read()) != EOF && read != '\r') {
                updateBytesRead(read);
                bos.write(read);
            }
            is.read();//read new line \n

            return bos.toString("utf-8");
        }

        private boolean notReachedFrameBoundary(String frameBounary, byte[] buffer) {
            return !new String(buffer).contains(frameBounary);
        }

        private boolean publishProgressOrCancel(String message) {
            if (!isCancelled()) {

                //TODO 1E-4 Publish progress with a message
                return false;
            }
            publishProgress("Task cancelled!");
            return true;
        }

        private int getScaleFactor(int outWidth, int outHeight, int width, int height) {
            int scale = 1;
            while (outWidth > width * scale || outHeight > height * scale) {
                scale *= 2;
            }
            return scale;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... values) {
            if(!isCancelled())
                textViewProgress.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //TODO 1E-3 set the bitmap on image view. Note we are in UI
            if (bitmap != null && !isCancelled()) {

            }
        }


        private void updateBytesRead(int read) {
            if (read != EOF)
                totalRead += read;

            if (totalRead > notified) {
                notified = totalRead * 2;
                publishProgress(String.format("Read %s", readableFileSize(totalRead)));
                notified = totalRead;
            }
        }

        public String readableFileSize(long size) {
            if (size <= 0) return "0";
            final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
            int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
            return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
        }

    }
}