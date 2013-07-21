package pl.bnsit.aa.part3.post;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/15/13
 * Time: 7:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class QuotesService {
    private static final String USERS_URL = "http://quotes.herokuapp.com/users";
    private static final String TAG = QuotesService.class.getSimpleName();
    public static final String USER_NAME_PARAM = "user[user_name]";
    public static final String USER_FIRST_NAME_PARAM = "user[first_name]";
    public static final String USER_LAST_NAME_PARAM = "user[last_name]";
    public static final String USER_PASSWORD_PARAM = "user[password]";
    public static final String USER_PASSWORD_CONFIRMATION_PARAM = "user[password_confirmation]";

    public void createUserApache(String userName, String firstName, String lastName, String password) {

        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();

        HttpPost httpMethod = new HttpPost(USERS_URL);

        UrlEncodedFormEntity entity = null;

        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(USER_NAME_PARAM, userName));
        params.add(new BasicNameValuePair(USER_FIRST_NAME_PARAM, firstName));
        params.add(new BasicNameValuePair(USER_LAST_NAME_PARAM, lastName));
        params.add(new BasicNameValuePair(USER_PASSWORD_PARAM, password));
        params.add(new BasicNameValuePair(USER_PASSWORD_CONFIRMATION_PARAM, password));

        try {
            entity = new UrlEncodedFormEntity(params);
        } catch (final UnsupportedEncodingException e) {

        }

        httpMethod.addHeader(entity.getContentType());
        httpMethod.setEntity(entity);



        try {
            HttpResponse execute = defaultHttpClient.execute(httpMethod);
            HttpEntity httpEntity = execute.getEntity();
            InputStream content = httpEntity.getContent();

            String s = getStringFromInput(content);
            Log.d(TAG, s);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
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


    public void createUserHttpUrlConnection(String userName, String firstName, String lastName, String password) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(USERS_URL).openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            String params = new EncodedParamBuilder()
                    .addParam(USER_NAME_PARAM, userName)
                    .addParam(USER_FIRST_NAME_PARAM, firstName)
                    .addParam(USER_LAST_NAME_PARAM, lastName)
                    .addParam(USER_PASSWORD_PARAM, password)
                    .addParam(USER_PASSWORD_CONFIRMATION_PARAM, password).build();

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());

            outputStreamWriter.write(params);
            outputStreamWriter.close();

            InputStream inputStream = urlConnection.getInputStream();
            String response = getStringFromInput(inputStream);

            Log.d(TAG, response);


        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }
    }
}
