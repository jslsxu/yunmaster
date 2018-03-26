package com.yun.yunmaster.network.base.apis;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class GsonUtil {
    public static <T> List<T> listFromJson(String json, Class<T> clazz) {
        ArrayList<T> list = new ArrayList<>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (JsonElement element : array) {
            list.add(new Gson().fromJson(element, clazz));
        }
        return list;
    }
}

