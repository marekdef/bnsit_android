package pl.bnsit.aa.part2.http;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import pl.bnsit.aa.R;
import pl.bnsit.aa.part3.post.EncodedParamBuilder;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/21/13
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class PolishPostHttpTracking extends Activity implements View.OnClickListener {
    public static final String POCZTA_POLSKA_URL_WS = "http://sledzenie.poczta-polska.pl/wssClient.php";
    private EditText editText;
    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post);

        findViewById(R.id.button).setOnClickListener(this);
        editText = (EditText) findViewById(R.id.editText);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

    }

    @Override
    public void onClick(View v) {
        final String trackingNumber = editText.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URLConnection urlConnection = new URL(POCZTA_POLSKA_URL_WS).openConnection();

                    urlConnection.setDoOutput(true);
                    OutputStream outputStream = urlConnection.getOutputStream();

                    String params = new EncodedParamBuilder()
                            .addParam("n", trackingNumber)
                            .build();

                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());

                    outputStreamWriter.write(params);
                    outputStreamWriter.close();

                    InputStream inputStream = urlConnection.getInputStream();
                    String response = getStringFromInput(inputStream);
                    webView.loadDataWithBaseURL(null, response, "text/html",
                            "utf-8", null);

                } catch (MalformedURLException e) {

                } catch (IOException e) {

                }
            }

            private String getStringFromInput(InputStream inputStream) throws IOException {
                int bytesRead = 0;
                byte[] buffer = new byte[1024];

                ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);

                while((bytesRead = inputStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }

                return bos.toString("utf-8");
            }
        }).start();
    }
}