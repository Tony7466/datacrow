package org.datacrow.core.server.serialization.adapters;

import java.lang.reflect.Type;

import org.datacrow.core.objects.DcImageIcon;
import org.datacrow.core.objects.DcSimpleValue;
import org.datacrow.core.utilities.Base64;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DcSimpleValueAdapter implements JsonDeserializer<DcSimpleValue>, JsonSerializer<DcSimpleValue> {

    public JsonElement serialize(
            DcSimpleValue src, 
            Type typeOfSrc, 
            JsonSerializationContext context) {
        
        JsonObject jo = new JsonObject();
        
        jo.addProperty("name", src.getName());
        jo.addProperty("itemId", src.getID());
        
        if (src.getIcon() != null)
            jo.addProperty("icon", new String(Base64.encode(src.getIcon().getBytes())));
        
        return jo;
    }
    
    public DcSimpleValue deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        
        JsonObject jo = json.getAsJsonObject();
        
        String id = jo.get("itemId").getAsString();
        String name = jo.get("name").getAsString();

        DcSimpleValue sv = new DcSimpleValue(id, name);
        
        if (jo.has("icon")) {
            String icon = jo.get("icon").getAsString();
            sv.setIcon(new DcImageIcon(Base64.decode(icon.toCharArray())));
        }

        return sv;
    }
}
