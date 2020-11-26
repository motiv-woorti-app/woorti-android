package inesc_id.pt.motivandroid.deprecated;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

@Deprecated
class LongOrDoubleJsonDeserializer implements JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            Number number = json.getAsNumber();

            return number.longValue();
        }

        return null;
    }
}
