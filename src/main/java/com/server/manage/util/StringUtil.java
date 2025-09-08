package com.server.manage.util;

import java.util.Collection;
import java.util.UUID;

/**
 * 字符串工具类
 */
public class StringUtil {

    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 字符串
     * @return 是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空白
     *
     * @param str 字符串
     * @return 是否为空白
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否不为空白
     *
     * @param str 字符串
     * @return 是否不为空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 将字符串首字母大写
     *
     * @param str 字符串
     * @return 首字母大写的字符串
     */
    public static String capitalize(String str) {
        if (isBlank(str)) {
            return str;
        }
        char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            return str;
        }
        return new StringBuilder(str.length())
                .append(Character.toTitleCase(firstChar))
                .append(str.substring(1))
                .toString();
    }

    /**
     * 生成UUID
     *
     * @return UUID字符串
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 将集合连接成字符串
     *
     * @param collection 集合
     * @param separator  分隔符
     * @return 连接后的字符串
     */
    public static String join(Collection<?> collection, String separator) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object obj : collection) {
            if (first) {
                first = false;
            } else {
                sb.append(separator);
            }
            sb.append(obj);
        }
        return sb.toString();
    }
}