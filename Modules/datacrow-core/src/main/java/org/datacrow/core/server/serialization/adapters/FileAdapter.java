package org.datacrow.core.server.serialization.adapters;

import java.io.File;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class FileAdapter implements JsonDeserializer<File>, JsonSerializer<File> {
    
    public JsonElement serialize(
            File src, 
            Type typeOfSrc, 
            JsonSerializationContext context) {
        
        JsonObject jdco = new JsonObject();
        
        jdco.addProperty("absolutepath", src.getAbsolutePath());
        
        return jdco;
    }

    public File deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        String absFilename = jsonObject.get("absolutepath").getAsString();
        return new File(absFilename);
    }
}
