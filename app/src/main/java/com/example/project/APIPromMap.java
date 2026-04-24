package com.example.project;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public final class APIPromMap {

    private APIPromMap() {
    }

    public static List<PromItem> parsePromotions(JsonObject response) {
        List<PromItem> result = new ArrayList<>();
        if (response == null || !response.has("items") || !response.get("items").isJsonArray()) {
            return result;
        }

        JsonArray items = response.getAsJsonArray("items");
        for (JsonElement element : items) {
            if (!element.isJsonObject()) {
                continue;
            }

            PromItem promotion = mapItem(element.getAsJsonObject());
            if (promotion != null) {
                result.add(promotion);
            }
        }

        return result;
    }

    private static PromItem mapItem(JsonObject item) {
        String id = firstString(item, "id");
        String collectionId = firstString(item, "collectionId");
        String imageName = firstString(item, "newsImage", "image", "imageName", "banner");
        if (id == null || id.trim().isEmpty() || collectionId == null || collectionId.trim().isEmpty()
                || imageName == null || imageName.trim().isEmpty()) {
            return null;
        }

        String title = firstString(item, "title", "name", "newsTitle", "headline");
        String subtitle = firstString(item, "subtitle", "description", "newsSubtitle", "summary");

        return new PromItem(id, collectionId, imageName, title, subtitle);
    }

    private static String firstString(JsonObject obj, String... keys) {
        for (String key : keys) {
            if (obj.has(key) && !obj.get(key).isJsonNull()) {
                JsonElement value = obj.get(key);
                if (value.isJsonPrimitive()) {
                    return value.getAsString();
                }
            }
        }
        return null;
    }
}
