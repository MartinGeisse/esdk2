package name.martingeisse.mahdl.gradle.json;

import com.baggonius.gson.immutable.ImmutableListDeserializer;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import name.martingeisse.mahdl.common.ModuleIdentifier;

public class ModuleApiGson {

    public static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ImmutableList.class, new ImmutableListDeserializer())
            .registerTypeAdapter(ModuleIdentifier.class, new ModuleIdentifierSerDes())
            .create();

}
