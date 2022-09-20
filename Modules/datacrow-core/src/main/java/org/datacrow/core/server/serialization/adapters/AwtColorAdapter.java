package org.datacrow.core.server.serialization.adapters;

import java.awt.Color;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class AwtColorAdapter implements JsonDeserializer<Color>, JsonSerializer<Color> {

    public JsonElement serialize(
            Color src, 
            Type typeOfSrc, 
            JsonSerializationContext context) {
        
        JsonObject jdco = new JsonObject();
        jdco.addProperty("rgb", src.getRGB());
        return jdco;
    }
    
    public Color deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        int rgb = jsonObject.get("rgb").getAsInt();
        return new Color(rgb);
    }
}
