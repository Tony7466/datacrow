package org.datacrow.core.server.serialization.adapters;

import java.lang.reflect.Type;

import org.datacrow.core.modules.DcModules;
import org.datacrow.core.objects.DcField;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.objects.DcValue;
import org.datacrow.core.server.serialization.helpers.DcFieldValue;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DcObjectAdapter implements JsonDeserializer<DcObject>, JsonSerializer<DcObject> {

    public JsonElement serialize(
            DcObject src, 
            Type typeOfSrc, 
            JsonSerializationContext context) {
        
        JsonObject jdco = new JsonObject();
        
        src.loadImageData();
        
        jdco.addProperty("moduleIdx", src.getModuleIdx());
        
        JsonArray array = new JsonArray();
        
        DcValue v;
        for (DcField field :  src.getFields()) {
            if (!field.isUiOnly()) {
                
                v = src.getValueDef(field.getIndex());
                DcFieldValue value = new DcFieldValue(
                        field.getModule(),
                        field.getIndex(), 
                        v.getValue(), 
                        v.isChanged());
                
                array.add(context.serialize(value));
            }
        }
        
        jdco.add("values", array);
        
        return jdco;
    }
    
    public DcObject deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        int moduleIdx = jsonObject.get("moduleIdx").getAsInt();

        DcObject dco = DcModules.get(moduleIdx).getItem();
        
        JsonArray values = jsonObject.getAsJsonArray("values");
        
        DcFieldValue fieldValue;
        for (JsonElement value : values) {
            fieldValue = jsonDeserializationContext.deserialize(value, DcFieldValue.class);
            dco.setValue(fieldValue.getFieldIndex(), fieldValue.getValue());
            dco.setChanged(fieldValue.getFieldIndex(), fieldValue.isChanged());
        }
            
        return dco;
    }
}
