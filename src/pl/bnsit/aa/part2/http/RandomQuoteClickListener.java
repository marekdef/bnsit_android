package pl.bnsit.aa.part2.http;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/12/13
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomQuoteClickListener implements View.OnClickListener {
    public static final String QUOTES_RANDOM_URL =   "http://10.0.2.2:3000/random";
    //"http://quotesapi.herokuapp.com/random";
    public static final String QUOTES_RANDOM_URL_JSON = QUOTES_RANDOM_URL + ".json";

    private static final String TAG = RandomQuoteClickListener.class.getSimpleName();
    private final Handler handler;

    public RandomQuoteClickListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onClick(View view) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                handler.obtainMessage(RandomQuoteApacheClientActivity.START_BACKGROUND_OPERATION).sendToTarget();

                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(QUOTES_RANDOM_URL);
                try {
                    HttpResponse execute = client.execute(get);
                    int statusCode = execute.getStatusLine().getStatusCode();
                    if (statusCode != HttpStatus.SC_OK) {
                        handler.obtainMessage(RandomQuoteApacheClientActivity.ERROR_BACKGROUND_OPERATION, String.format("Http status is not  OK %s ", statusCode));
                        return;
                    }

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    execute.getEntity().writeTo(os);

                    String quote = os.toString("UTF-8");
                    handler.obtainMessage(RandomQuoteApacheClientActivity.DONE_BACKGROUND_OPERATION, quote).sendToTarget();
                } catch (ClientProtocolException e) {
                    Log.e(TAG, "ClientProtocolException", e);
                    handler.obtainMessage(RandomQuoteApacheClientActivity.ERROR_BACKGROUND_OPERATION, e.getMessage());
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "UnsupportedEncodingException", e);
                    handler.obtainMessage(RandomQuoteApacheClientActivity.ERROR_BACKGROUND_OPERATION, e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "IOException", e);
                    handler.obtainMessage(RandomQuoteApacheClientActivity.ERROR_BACKGROUND_OPERATION, e.getMessage());
                }
            }
        };
        thread.start();
    }
}
