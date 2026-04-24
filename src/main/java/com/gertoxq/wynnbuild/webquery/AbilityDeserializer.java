package com.gertoxq.wynnbuild.webquery;

import com.gertoxq.wynnbuild.webquery.providers.BuilderAbilitySchema;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.TreeSet;

public class AbilityDeserializer implements JsonDeserializer<BuilderAbilitySchema>, JsonSerializer<BuilderAbilitySchema> {
    @Override
    public BuilderAbilitySchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject obj = json.getAsJsonObject();

        int id = obj.get("id").getAsInt();
        String archetype = obj.has("archetype") ? obj.get("archetype").getAsString() : null;
        int archetypeReq = obj.has("archetype_req") ? obj.get("archetype_req").getAsInt() : 0;

        if (!obj.has("parents")) {
            throw new JsonParseException("Ability JSON object is missing 'parents' field");
        }
        Set<Integer> parents = context.deserialize(obj.get("parents"), new TypeToken<Set<Integer>>() {
        }.getType());

        TreeSet<Integer> children;
        if (obj.has("children")) {
            children = context.deserialize(obj.get("children"), new TypeToken<TreeSet<Integer>>() {
            }.getType());
        } else {
            children = new TreeSet<>();
        }

        if (!obj.has("dependencies")) {
            throw new JsonParseException("Ability JSON object is missing 'dependencies' field");
        }
        Set<Integer> dependencies = context.deserialize(obj.get("dependencies"), new TypeToken<Set<Integer>>() {
        }.getType());

        if (!obj.has("display") || !obj.getAsJsonObject("display").has("col")) {
            throw new JsonParseException("Ability JSON object is missing 'display.col' field");
        }
        int col = obj.getAsJsonObject("display").get("col").getAsInt();

        int row = obj.getAsJsonObject("display").get("row").getAsInt();

        return new BuilderAbilitySchema(
                id, parents, dependencies, children, archetype, archetypeReq, col, row
        );
    }

    @Override
    public JsonElement serialize(BuilderAbilitySchema src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        obj.addProperty("id", src.id());
        obj.add("parents", context.serialize(src.parents()));
        obj.add("dependencies", context.serialize(src.dependencies()));
        obj.add("children", context.serialize(src.children()));
        if (src.archetype() != null) {
            obj.addProperty("archetype", src.archetype());
        }
        if (src.archetype_req() != 0) {
            obj.addProperty("archetype_req", src.archetype_req());
        }

        JsonObject displayObj = new JsonObject();
        displayObj.addProperty("col", src.col());
        displayObj.addProperty("row", src.row());
        obj.add("display", displayObj);

        return obj;
    }
}
