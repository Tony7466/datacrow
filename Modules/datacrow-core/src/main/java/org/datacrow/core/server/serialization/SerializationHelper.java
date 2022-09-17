package org.datacrow.core.server.serialization;

import org.datacrow.core.objects.DcObject;
import org.datacrow.core.server.serialization.serializers.DcObjectSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SerializationHelper {
    
    private static final SerializationHelper instance;
    
    private Gson builder;
    
    static {
        instance = new SerializationHelper();
    }
    
    public static SerializationHelper getInstance() {
        return instance;
    }
    
    public Gson getBuilder() {
        return builder;
    }
    
    private SerializationHelper() {
        builder = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(DcObject.class, new DcObjectSerializer())
                //.registerTypeAdapter(DcValue.class, new DcValueSerializer())
                .create();
    }
    
    public void serialize(Object o) {
//        try {
//            String s = builder.toJson(o);
//            s=s;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
