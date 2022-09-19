package org.datacrow.core.server.serialization.serializers;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class AwtFontAdapter implements JsonDeserializer<Font>, JsonSerializer<Font> {

    public JsonElement serialize(
            Font src, 
            Type typeOfSrc, 
            JsonSerializationContext context) {
        
        JsonObject jdco = new JsonObject();
        jdco.addProperty("fontname", src.getFontName());
        return jdco;
    }
    
    public Font deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        String fontName = jsonObject.get("fontname").getAsString();
        
        // not thread safe, declare and construct locally:
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font result = null;
        for (Font font : ge.getAllFonts()) {
            if (font.getFontName().equals(fontName))
                result = font;
        }
        
        return result;
    }
}
