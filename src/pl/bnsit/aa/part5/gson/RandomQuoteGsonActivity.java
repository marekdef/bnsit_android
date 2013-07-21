package pl.bnsit.aa.part5.gson;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pl.bnsit.aa.R;
import pl.bnsit.aa.part2.http.RandomQuoteClickListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class RandomQuoteGsonActivity extends Activity implements View.OnClickListener {
    private static final String QUOTE = "Quote";

    private View button;
    private ProgressBar progressBar;
    private TextView quoteText;
    private ImageView imageView;
    private TextView authorTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.random_quote);

        button = findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        quoteText = (TextView) findViewById(R.id.quote);
        authorTextView = (TextView) findViewById(R.id.author);
        imageView = (ImageView) findViewById(R.id.imageView);

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
                        URL url = new URL(RandomQuoteClickListener.QUOTES_RANDOM_URL + ".json");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.addRequestProperty("Accept-Encoding", "application/json");
                        int responseCode = urlConnection.getResponseCode();
                        if (isInvalidResponse(responseCode))
                            return;

                        String json = getStringFromStream(urlConnection);
                        Quote quote = parseJSON(json);
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

                private Quote parseJSON(String json) {
                    //TODO 5A-1 Prepare gson parser for parsing first using new
                    Gson gson1 = null;
                    //TODO 5A-6 for more sophisticated options needed later we need a builder

                    //TODO 5A-2 We want to create a quote from json
                    Quote quote = gson1.fromJson((String)null, null);
                    return quote;
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
        quoteText.post(new Runnable() {
            @Override
            public void run() {
                toggleActionButtons(true);
                String exceptionMessage = null;
                String errorMessage = message;
                if (cause != null)
                    exceptionMessage = cause.getMessage();

                if (exceptionMessage != null && exceptionMessage.trim().length() > 0)
                    errorMessage += (" " + exceptionMessage);

                Toast.makeText(RandomQuoteGsonActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setNewQuote(final Quote quote) {
        this.quoteText.post(new Runnable() {
            @Override
            public void run() {
                toggleActionButtons(true);
                RandomQuoteGsonActivity.this.quoteText.setText(quote.content);
                if(quote.user.photo != null) {
                    imageView.setImageBitmap(quote.user.photo);

                }
                imageView.setVisibility(quote.user.photo != null ? View.VISIBLE : View.GONE);
                authorTextView.setText(quote.user.first_name + " " + quote.user.last_name);
            }
        });
    }

    private void toggleActionButtons(boolean enable) {
        //To change body of created methods use File | Settings | File Templates.
        button.setEnabled(enable);
        progressBar.setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
    }
}