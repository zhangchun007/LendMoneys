package com.haiercash.gouhua.unity;

import com.alibaba.fastjson.JSON;
import com.app.haiercash.base.utils.log.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by hackzhang on 2018/5/22.
 */

public class JsonUtils {
    //类转json
    public static <T> String class2Json(T cls) {
        if (null != cls) {
            return JSON.toJSON(cls).toString();
        } else {
            return "";
        }
    }

    /**
     * json转类
     */
    public static <T> T json2Class(String json, Class<T> cls) {
        return JSON.parseObject(json, cls);
    }

    //json转list
    public static <T> List<T> json2list(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    public static <T> Map<String, String> getRequest(T requestParam) {
        Map map = null;
        try {
            String request = (requestParam instanceof String) ? (String) requestParam : JsonUtils.class2Json(requestParam);
            map = json2Class(request, HashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("JsonUtils.getRequest() 异常：" + requestParam + "---" + e.getMessage());
        }
        return map == null ? new HashMap<String, String>() : map;
    }
    public static <T> Map<String, Object> getRequestObj(T requestParam) {
        Map map = null;
        try {
            String request = (requestParam instanceof String) ? (String) requestParam : JsonUtils.class2Json(requestParam);
            map = json2Class(request, HashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("JsonUtils.getRequest() 异常：" + requestParam + "---" + e.getMessage());
        }
        return map == null ? new HashMap<String, Object>() : map;
    }

    public static String toJson(Object src) {
        try {
            Gson gson = new Gson();
            return gson.toJson(src);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("JsonUtils.toJson() 异常：" + src + "---" + e.getMessage());
        }
        return null;
    }

    public static <T> T fromJson(String source, Type typeToken) {
        T result = null;
        try {
            Gson gson = new Gson();
            result = gson.fromJson(source, typeToken);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("JsonUtils.fromJson(Type) 异常：" + source + "---" + e.getMessage());
        }
        return result;
    }

    /**
     * JSON格式数据到目标对象转换
     *
     * @param src      JSON格式数据
     * @param classOfT 目标对象類型
     * @return 目标对象
     */
    public static <T> T fromJson(Object src, Class<T> classOfT) {
        if (src instanceof String) {
            return fromJson((String) src, classOfT);
        } else {
            return fromJson(toJson(src), classOfT);
        }
    }

    /**
     * JSON格式数据到目标对象转换
     *
     * @param src      JSON格式数据
     * @param classOfT 目标对象類型
     * @return 目标对象
     */
    public static <T> T fromJson(String src, Class<T> classOfT) {
        T result = null;
        try {
            Gson gson = new Gson();
            result = gson.fromJson(src, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("JsonUtils.fromJson(Class) 异常：" + src + "---" + e.getMessage());
        }
        return result;
    }

    public static <T> T jsonToObj(String json, Class<T> cls) {
        Gson gson = new Gson();
        if (Objects.isNull(json)) {
            return null;
        }
        T obj = gson.fromJson(json, cls);
        if (Objects.isNull(obj)) {
            return null;
        } else {
            return obj;
        }
    }

    public static <T> T jsonToList(String json, Class<T> cls) {
        if (Objects.isNull(json)) {
            return null;
        }
        try {
            Gson gson = new Gson();
            // 需要注意这里的type
            Type type = new TypeToken<List<T>>() {
            }.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
