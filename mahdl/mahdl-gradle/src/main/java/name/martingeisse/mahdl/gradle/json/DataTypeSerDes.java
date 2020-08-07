package name.martingeisse.mahdl.gradle.json;

import com.google.gson.*;
import name.martingeisse.mahdl.common.processor.type.ProcessedDataType;

import java.lang.reflect.Type;

public final class DataTypeSerDes implements JsonSerializer<ProcessedDataType>, JsonDeserializer<ProcessedDataType> {

    @Override
    public ProcessedDataType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("expected data type (object)");
        }
        JsonObject object = json.getAsJsonObject();
        JsonElement familyElement = object.get("family");
        if (familyElement == null || !familyElement.isJsonPrimitive() || !familyElement.getAsJsonPrimitive().isString()) {
            throw new JsonParseException("field 'family': expected string");
        }
        ProcessedDataType.Family family;
        try {
            family = ProcessedDataType.Family.valueOf(familyElement.getAsString());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("invalid data type family: " + familyElement.getAsString());
        }
        switch (family) {

            case UNKNOWN:
                return ProcessedDataType.Unknown.INSTANCE;

            case BIT:
                return ProcessedDataType.Bit.INSTANCE;

            case VECTOR:
                return new ProcessedDataType.Vector(parseSize(object, "size"));

            case MATRIX:
                return new ProcessedDataType.Matrix(parseSize(object, "firstSize"), parseSize(object, "secondSize"));

            case INTEGER:
                return ProcessedDataType.Integer.INSTANCE;

            case TEXT:
                return ProcessedDataType.Text.INSTANCE;

            case CLOCK:
                return ProcessedDataType.Clock.INSTANCE;

            default:
                throw new JsonParseException("internal error: unknown data type family: " + family);

        }
    }

    private int parseSize(JsonObject object, String name) {
        JsonElement element = object.get(name);
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            int value = element.getAsJsonPrimitive().getAsNumber().intValue();
            if (value < 0) {
                throw new JsonParseException("size cannot be negative");
            } else {
                return value;
            }
        }
        throw new JsonParseException("field '" + name + "': expected size (integer)");
    }

    @Override
    public JsonElement serialize(ProcessedDataType dataType, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("family", dataType.getFamily().name());
        switch (dataType.getFamily()) {

            case VECTOR:
                result.addProperty("size", ((ProcessedDataType.Vector)dataType).getSize());
                break;

            case MATRIX:
                result.addProperty("firstSize", ((ProcessedDataType.Matrix)dataType).getFirstSize());
                result.addProperty("secondSize", ((ProcessedDataType.Matrix)dataType).getSecondSize());
                break;

        }
        return result;
    }

}
