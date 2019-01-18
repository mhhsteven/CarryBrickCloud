package org.mao.utils;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Json转换工具
 *
 * @author mhh
 */
public class JsonUtils {

    public static <T> T fromJson(String json, Class<T> raw, Class<?> clazz) {
        Gson gson = new Gson();
        if (clazz != null) {
            Type objectType = type(raw, clazz);
            return gson.fromJson(json, objectType);
        } else {
            return gson.fromJson(json, raw);
        }
    }

    private static ParameterizedType type(final Class<?> raw, final Type... args) {
        return new ParameterizedType() {
            @Override
            public Type getRawType() {
                return raw;
            }

            @Override
            public Type[] getActualTypeArguments() {
                return args;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }
}
