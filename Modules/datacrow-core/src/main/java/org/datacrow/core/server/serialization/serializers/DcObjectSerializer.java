package org.datacrow.core.server.serialization.serializers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.datacrow.core.objects.DcField;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.objects.DcValue;
import org.datacrow.core.server.serialization.SerializationHelper;
import org.datacrow.core.server.serialization.helpers.DcFieldValue;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DcObjectSerializer implements JsonSerializer<DcObject> {
    
    public JsonElement serialize(
            DcObject src, 
            Type typeOfSrc, 
            JsonSerializationContext context) {
        
        JsonObject jdco = new JsonObject();
        
        jdco.addProperty("moduleIdx", src.getModuleIdx());
        
        JsonElement jvalue;
        List<JsonElement> jvalues = new ArrayList<JsonElement>();
        
        for (DcField field :  src.getFields()) {
            if (src.isFilled(field.getIndex()) && !field.isUiOnly()) {
                DcFieldValue value = new DcFieldValue(field.getIndex(), src.getValue(field.getIndex()), src.isChanged(field.getIndex()));
                jvalue = context.serialize(value);
                jvalues.add(jvalue);
            }
        }

        JsonElement tree = SerializationHelper.getInstance().getBuilder().toJsonTree(jvalues);
        jdco.add("values", tree);
        
        return jdco;
    }
}
