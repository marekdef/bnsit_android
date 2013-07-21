package pl.bnsit.aa.part5.gson;

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
 * Date: 7/13/13
 * Time: 5:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class Quote {

    String content;
    //TODO 5A-3 note that this name is different than json presented. Find an annotation that will help with that
    Date createdAt;
    long id;
    User user;


}
