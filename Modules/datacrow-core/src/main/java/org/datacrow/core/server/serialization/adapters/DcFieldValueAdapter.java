package org.datacrow.core.server.serialization.adapters;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.datacrow.core.DcRepository;
import org.datacrow.core.log.DcLogManager;
import org.datacrow.core.modules.DcModules;
import org.datacrow.core.objects.DcField;
import org.datacrow.core.objects.DcImageIcon;
import org.datacrow.core.objects.DcMapping;
import org.datacrow.core.objects.DcObject;
import org.datacrow.core.objects.Picture;
import org.datacrow.core.server.serialization.helpers.DcFieldValue;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DcFieldValueAdapter implements JsonDeserializer<DcFieldValue>, JsonSerializer<DcFieldValue> {

    private transient static Logger logger = DcLogManager.getLogger(DcFieldValueAdapter.class.getName());
    
    @SuppressWarnings("unchecked")
    public JsonElement serialize(
            DcFieldValue src, 
            Type typeOfSrc, 
            JsonSerializationContext context) {
        
        JsonObject jdco = new JsonObject();
        
        jdco.addProperty("fieldindex", src.getFieldIndex());
        jdco.addProperty("moduleindex", src.getModuleIndex());
        jdco.addProperty("changed", src.isChanged());
        
        DcField field = DcModules.get(src.getModuleIndex()).getField(src.getFieldIndex());
        
        Object value = src.getValue();
        JsonElement je;
        if (value instanceof DcObject) {
            je = context.serialize(value, DcObject.class);
        }  else if (value instanceof DcImageIcon) {
            je = context.serialize(value, DcImageIcon.class);            
        }  else if (value instanceof Picture) {
            value = ((Picture) value).getValue(Picture._D_IMAGE);
            je = context.serialize(value, DcImageIcon.class);
        } else if (value instanceof Number) {
            je = context.serialize(value.toString());
        } else if (value instanceof Date) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            je = context.serialize(formatter.format((Date) value));
        } else if (
                field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION &&
                value instanceof Collection) {

            JsonArray references = new JsonArray();
            for (DcMapping mapping : (Collection<DcMapping>) value) {
                // TODO: check if this calls the DcObjectAdapter;
                 references.add(context.serialize(mapping.getReferencedObject()));
            }
            je = context.serialize(references);
        } else {
            je = context.serialize(value);
        }
            
        jdco.add("fieldvalue", je);
        
        return jdco;
    }
    
    public DcFieldValue deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        int fieldIndex = jsonObject.get("fieldindex").getAsInt();
        int moduleIndex = jsonObject.get("moduleindex").getAsInt();
        boolean changed = jsonObject.get("changed").getAsBoolean();
        
        DcField field = DcModules.get(moduleIndex).getField(fieldIndex);
        Object result = null;
        
        JsonElement e = jsonObject.get("fieldvalue");
        
        if (e != null) { 
            if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE) {
                JsonObject jo = jsonObject.getAsJsonObject("fieldvalue");
                result = jo != null ? context.deserialize(jo, DcObject.class) : null;
            } else if (field.getValueType() == DcRepository.ValueTypes._IMAGEICON ||
                       field.getValueType() == DcRepository.ValueTypes._PICTURE) {
                
                JsonObject jo = jsonObject.getAsJsonObject("fieldvalue");
                if (jo != null) {
                    result = context.deserialize(jsonObject.getAsJsonObject("fieldvalue"), DcImageIcon.class);
                    
                    if (field.getValueType() == DcRepository.ValueTypes._PICTURE) {
                        // TODO: we need a specialize adapter for pictures....
                        Picture pic = new Picture();
                        pic.setValue(Picture._D_IMAGE, result);
                        result = pic;
                    }
                }
            } else if (field.getValueType() == DcRepository.ValueTypes._BIGINTEGER ||
                       field.getValueType() == DcRepository.ValueTypes._LONG) {
                result = Long.valueOf(e.getAsString());
            } else if (field.getValueType() == DcRepository.ValueTypes._DOUBLE) { 
                result = Double.valueOf(e.getAsString());   
            } else if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
                result = null;
            } else if (field.getValueType() == DcRepository.ValueTypes._STRING) {
                result = e.getAsString();
            } else if (field.getValueType() == DcRepository.ValueTypes._BOOLEAN) {
                result = jsonObject.get("fieldvalue").getAsBoolean();
            } else if (field.getValueType() == DcRepository.ValueTypes._DATE ||
                       field.getValueType() == DcRepository.ValueTypes._DATETIME) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    result = formatter.parse(jsonObject.get("fieldvalue").getAsString());
                } catch (Exception exp) {
                    logger.debug("Could not parse datem from [" + jsonObject.get("fieldvalue").getAsString() + "]", exp);
                }
            }
        }
            
        return new DcFieldValue(moduleIndex, fieldIndex, result, changed);
    }
}
