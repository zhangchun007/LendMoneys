package com.app.haiercash.base.utils.json;

import com.app.haiercash.base.utils.log.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    /*
     * 处理gson的整形转换为浮点数的问题
     */
    private static final Gson gson;

    static {
        gson = new GsonBuilder().registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
            @Override
            public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                if (src == src.longValue()) {
                    return new JsonPrimitive(src.longValue());
                }
                return new JsonPrimitive(src);
            }
        }).create();
    }

    public static String toJson(Object src) {
        try {
            return gson.toJson(src);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("JsonUtils.toJson() 异常：" + src + "---" + e.getMessage());
        }
        return null;
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
            result = gson.fromJson(src, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("JsonUtils.fromJson(Class) 异常：" + "---" + e.getMessage());
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

    public static <T> Map<String, String> getRequest(T requestParam) {
        Map<String, String> map = null;
        try {
            String request = (requestParam instanceof String) ? (String) requestParam : JsonUtils.toJson(requestParam);
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            map = fromJson(request, type);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("JsonUtils.getRequest() 异常：" + requestParam + "---" + e.getMessage());
        }
        return map == null ? new HashMap<String, String>() : map;
    }

    public static <T> Map<String, Object> getRequestObj(T requestParam) {
        Map<String, Object> map = null;
        try {
            String request = (requestParam instanceof String) ? (String) requestParam : JsonUtils.toJson(requestParam);
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            map = fromJson(request, type);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("JsonUtils.getRequest() 异常：" + requestParam + "---" + e.getMessage());
        }
        return map == null ? new HashMap<String, Object>() : map;
    }

    /**
     * JSON格式数据到目标对象转换
     *
     * @param json      JSON格式数据
     * @param typeToken typeToken 对象
     */
    @Deprecated
    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        return fromJson(json, typeToken.getType());
    }

//    /**
//     * JSON格式数据到目标对象转换
//     * 该方法已经摒弃 采用fromJsonArray代替
//     *
//     * @param json      JSON格式数据
//     * @param typeToken typeToken 对象
//     */
//    @Deprecated
//    public static <T> T fromJson(Object json, TypeToken<T> typeToken) {
//        return gson.fromJson(toJson(json), typeToken.getType());
//    }


    /**
     * JSON格式数据到目标对象转换
     *
     * @param response JSON格式数据
     */
    public static <T> T fromJson(Object response, String tag, TypeToken<T> typeToken) {
        Map map = (Map) response;
        return fromJson(toJson(map.get(tag)), typeToken.getType());
    }

    public static <T> List<T> fromJsonArray(Object response, String tag, Class<T> clazz) {
        List<T> list = null;
        try {
            Map map = (Map) response;
            Type listType = getParameterType(List.class, clazz);
            list = fromJson(toJson(map.get(tag)), listType);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("JsonUtils.fromJsonArray() 异常：" + response + "---" + e.getMessage());
        }
        return list == null ? new ArrayList<T>() : list;
    }

    public static <T> T fromJson(String source, Type typeToken) {
        T result = null;
        try {
            result = gson.fromJson(source, typeToken);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("JsonUtils.fromJson(Type) 异常：" + source + "---" + e.getMessage());
        }
        return result;
    }

    /**
     * JSON格式数据到List<目标对象>转换
     *
     * @param json  JSON格式数据
     * @param clazz 目标对象
     */
    public static <T> List<T> fromJsonArray(Object json, Class<T> clazz) {
        List<T> list = null;
        try {
            Type listType = getParameterType(List.class, clazz);
            list = fromJson(toJson(json), listType);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("JsonUtils.fromJsonArray(Class) 异常：" + json + "---" + e.getMessage());
        }
        return list == null ? new ArrayList<T>() : list;
    }

    private static Type getParameterType(final Class cl, final Class... cls) {
        return new ParameterizedType() {

            @Override
            public Type[] getActualTypeArguments() {
                return cls;
            }

            @Override
            public Type getRawType() {
                return cl;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }
}
