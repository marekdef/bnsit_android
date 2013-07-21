package pl.bnsit.aa.part3.post;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/20/13
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class EncodedParamBuilder {
    private StringBuilder sb = new StringBuilder();


    public EncodedParamBuilder addParam(String param, String value) {
        sb.append(Uri.encode(param));
        sb.append("=");
        sb.append(Uri.encode(value));
        sb.append("&");
        return this;
    }


    public String build() {
        String s = sb.toString();
        if(s.endsWith("&"))
            return s.substring(0, s.length() - 1);
        return s;
    }
}
