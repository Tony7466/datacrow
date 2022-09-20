package org.datacrow.core.server.serialization.adapters;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.datacrow.core.modules.DcModules;
import org.datacrow.core.objects.DcField;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.objects.DcValue;
import org.datacrow.core.server.serialization.SerializationHelper;
import org.datacrow.core.server.serialization.helpers.DcFieldValue;

import com.google.gson.Gson;
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
        
        jdco.addProperty("moduleIdx", src.getModuleIdx());
        
        JsonElement jvalue;
        List<JsonElement> jvalues = new ArrayList<JsonElement>();
        
        DcValue v;
        for (DcField field :  src.getFields()) {
            if (src.isFilled(field.getIndex()) && !field.isUiOnly()) {
                v = src.getValueDef(field.getIndex());
                DcFieldValue value = new DcFieldValue(
                        field.getIndex(), 
                        v.getJsonValue(field), 
                        v.isChanged());
                jvalue = context.serialize(value);
                jvalues.add(jvalue);
            }
        }

        JsonElement tree = SerializationHelper.getInstance().getGson().toJsonTree(jvalues);
        jdco.add("values", tree);
        
        return jdco;
    }
    
    public DcObject deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        int moduleIdx = jsonObject.get("moduleIdx").getAsInt();

        DcObject dco = DcModules.get(moduleIdx).getItem();
        
        JsonArray values = jsonObject.getAsJsonArray("values");
        
        Gson gson = SerializationHelper.getInstance().getGson();
        DcFieldValue fieldValue;
        for (JsonElement value : values) {
            fieldValue = gson.fromJson(value, DcFieldValue.class);
            
            dco.setValue(fieldValue.getIndex(), fieldValue.getValue());
            dco.setChanged(fieldValue.getIndex(), fieldValue.isChanged());
        }

        return dco;
    }
}
