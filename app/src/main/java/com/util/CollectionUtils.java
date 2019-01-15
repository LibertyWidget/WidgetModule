package com.util;

import java.util.Collection;
import java.util.Map;

/**
 * 集合工具类
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection<?> list) {
        return list == null || list.size() == 0;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }
}
