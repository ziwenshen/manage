package com.server.manage.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * JSON工具类
 */
public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 配置ObjectMapper
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat(DateUtil.DEFAULT_PATTERN));
    }

    /**
     * 将对象转换为JSON字符串
     *
     * @param obj 对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize object to JSON", e);
            return null;
        }
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param json  JSON字符串
     * @param clazz 对象类型
     * @param <T>   泛型
     * @return 对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtil.isBlank(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            logger.error("Failed to deserialize JSON to object", e);
            return null;
        }
    }

    /**
     * 将JSON字符串转换为复杂类型对象（如List、Map等）
     *
     * @param json          JSON字符串
     * @param typeReference 类型引用
     * @param <T>           泛型
     * @return 对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (StringUtil.isBlank(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            logger.error("Failed to deserialize JSON to object", e);
            return null;
        }
    }

    /**
     * 检查字符串是否为有效的JSON
     *
     * @param json JSON字符串
     * @return 是否有效
     */
    public static boolean isValidJson(String json) {
        if (StringUtil.isBlank(json)) {
            return false;
        }
        try {
            objectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}