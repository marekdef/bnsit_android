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


    @SerializedName("encode_image")
    Bitmap photo;

    public static class BitmapDeserializer implements JsonDeserializer<Bitmap> {

        @Override
        public Bitmap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            byte[] decoded = Base64.decode(json.getAsString(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            return bitmap;
        }
    }
}
