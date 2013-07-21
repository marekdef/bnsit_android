package pl.bnsit.aa.part2.http;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
        //TODO 2A-1 we should run this in background!
                handler.obtainMessage(RandomQuoteApacheClientActivity.START_BACKGROUND_OPERATION).sendToTarget();

                //TODO 2A-2 we missed client instantation
                HttpClient client = null;
                //TODO 2A-3 - is this a correct method to fetch
                HttpPost get = new HttpPost(QUOTES_RANDOM_URL);
                HttpResponse execute = null;
                try {
                    execute = client.execute(get);
                    //TODO 2A-4 well this is some code for testing isn't it?
                    int statusCode = HttpStatus.SC_SERVICE_UNAVAILABLE;
                    if (statusCode != HttpStatus.SC_OK) {
                        handler.obtainMessage(RandomQuoteApacheClientActivity.ERROR_BACKGROUND_OPERATION, String.format("Http status is not  OK %s ", statusCode));
                        return;
                    }

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    HttpEntity entity = execute.getEntity();
                    entity.writeTo(os);
                    //TODO 2A-5 this must be something else than null?
                    String quote = null;
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
                finally {

                }

    }
}
