package name.martingeisse.mahdl.gradle.json;

import com.google.gson.*;
import name.martingeisse.mahdl.common.ModuleIdentifier;

import java.lang.reflect.Type;

public final class ModuleIdentifierSerDes implements JsonSerializer<ModuleIdentifier>, JsonDeserializer<ModuleIdentifier> {

    @Override
    public ModuleIdentifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
            throw new JsonParseException("expected module identifier (string)");
        }
        return new ModuleIdentifier(json.getAsString());
    }

    @Override
    public JsonElement serialize(ModuleIdentifier moduleIdentifier, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(moduleIdentifier.toString());
    }

}
