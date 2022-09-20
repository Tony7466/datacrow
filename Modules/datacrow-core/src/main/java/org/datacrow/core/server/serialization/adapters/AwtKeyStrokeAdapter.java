package org.datacrow.core.server.serialization.adapters;

import java.awt.AWTKeyStroke;
import java.lang.reflect.Type;

import javax.swing.KeyStroke;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class AwtKeyStrokeAdapter implements JsonDeserializer<AWTKeyStroke>, JsonSerializer<AWTKeyStroke> {

    public JsonElement serialize(
            AWTKeyStroke src, 
            Type typeOfSrc, 
            JsonSerializationContext context) {
        
        JsonObject jdco = new JsonObject();
        jdco.addProperty("keycode", src.getKeyCode());
        jdco.addProperty("modifiers", src.getModifiers());
        return jdco;
    }

    public KeyStroke deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        int keyCode = jsonObject.get("keycode").getAsInt();
        int modifiers = jsonObject.get("modifiers").getAsInt();
        return KeyStroke.getKeyStroke(keyCode, modifiers);
    }
}
