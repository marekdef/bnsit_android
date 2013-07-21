package pl.bnsit.aa.part2.http;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import pl.bnsit.aa.R;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/12/13
 * Time: 8:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomQuoteHttpUrlConnectionActivity extends Activity implements View.OnClickListener {

    public static final int START_BACKGROUND_OPERATION = 1;
    public static final int DONE_BACKGROUND_OPERATION = 2;
    public static final int ERROR_BACKGROUND_OPERATION = 4;
    private static final String QUOTE = "Quote";

    private View button;
    private ProgressBar progressBar;
    private TextView textView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.random_quote);

        button = findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.quote);

        button.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.

        TextView quoteView = (TextView) findViewById(R.id.quote);
        String quote = getSharedPreferences(QUOTE, MODE_PRIVATE).getString(QUOTE, null);
        if (quote != null)
            quoteView.setText(quote);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(button)) {
            toggleActionButtons(false);

            new Thread() {
                @Override
                public void run() {

                    HttpURLConnection urlConnection = null;
                    try {
                        URL url = new URL(RandomQuoteClickListener.QUOTES_RANDOM_URL);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        int responseCode = urlConnection.getResponseCode();
                        if (isInvalidResponse(responseCode))
                            return;

                        String quote = getStringFromStream(urlConnection);
                        setNewQuote(quote);

                    } catch (MalformedURLException e) {
                        setErrorMessage("MalformedURLException ", e);
                    } catch (IOException e) {
                        setErrorMessage("IOException ", e);
                    } finally {
                        if(urlConnection != null)
                            urlConnection.disconnect();
                    }
                }

                private boolean isInvalidResponse(int responseCode) {
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        setErrorMessage(String.format("HTTP Status was not OK %d", responseCode), null);
                        return true;
                    }
                    return false;
                }

                private String getStringFromStream(HttpURLConnection urlConnection) throws IOException {
                    InputStream inputStream = urlConnection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead = 0;

                    int contentLength = urlConnection.getContentLength();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream(contentLength > 0 ? contentLength : 1024);

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }

                    return bos.toString("utf-8");
                }
            }.start();

        }
    }

    private void setErrorMessage(final String message, final Exception cause) {
        textView.post(new Runnable() {
            @Override
            public void run() {
                toggleActionButtons(true);
                String exceptionMessage = null;
                String errorMessage = message;
                if (cause != null)
                    exceptionMessage = cause.getMessage();

                if (exceptionMessage != null && exceptionMessage.trim().length() > 0)
                    errorMessage += (" " + exceptionMessage);

                Toast.makeText(RandomQuoteHttpUrlConnectionActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setNewQuote(final String quote) {
        textView.post(new Runnable() {
            @Override
            public void run() {
                toggleActionButtons(true);
                textView.setText(quote);
            }
        });
    }

    private void toggleActionButtons(boolean enable) {
        //To change body of created methods use File | Settings | File Templates.
        button.setEnabled(enable);
        progressBar.setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
    }
}