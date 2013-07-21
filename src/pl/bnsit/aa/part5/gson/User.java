package pl.bnsit.aa.part5.gson;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/21/13
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class User {
    long id;
    Date created_at;
    Date photo_updated_at;
    Date updated_at;

    String user_name;
    String first_name;
    String last_name;
    String photo_content_type;
    String photo_file_name;
    long photo_file_size;


    //TODO 5A-4 The photo also does not exists in json. We need to find a way to turn encode_image to
    Bitmap photo;

    //TODO 5A-5 We need a custom deserializer. Create a private class BitmapDeserializer that implements a proper interface
    //from Gson look for JsonDeserializer

    public static class BitmapDeserializer  {

        private Bitmap createBitmapFromString(String content) {
            byte[] decoded = Base64.decode(content, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            return bitmap;
        }
    }
}
