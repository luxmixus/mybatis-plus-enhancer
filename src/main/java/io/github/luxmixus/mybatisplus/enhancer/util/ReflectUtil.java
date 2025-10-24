package io.github.luxmixus.mybatisplus.enhancer.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射工具类
 * <p>
 * 提供通用的反射工具方法，包括实例创建、字段映射、属性复制等
 *
 * @author luxmixus
 */
public abstract class ReflectUtil {

    /**
     * 类字段映射缓存
     */
    private static final Map<Class<?>, Map<String, Field>> FIELD_MAP_CACHE = new ConcurrentHashMap<>();


    /**
     * 判断是否为Java核心类
     *
     * @param clazz 类
     * @return boolean 是否为Java核心类
     */
    public static boolean isJavaCoreClass(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.getClassLoader() == null;
    }

    /**
     * 新建实例
     *
     * @param clazz 类
     * @param <T>   实例类型
     * @return {@link T} 新实例
     */
    @SneakyThrows
    public static <T> T newInstance(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must not be null");
        }
        return clazz.getConstructor().newInstance();
    }

    /**
     * 获取指定类的字段映射
     *
     * @param clazz 类
     * @return {@link Map} 字段名到字段的映射
     */
    public static Map<String, Field> fieldMap(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must not be null");
        }
        if (isJavaCoreClass(clazz)) {
            throw new IllegalArgumentException("clazz must not be java class");
        }
        Map<String, Field> stringFieldMap = FIELD_MAP_CACHE.get(clazz);
        if (stringFieldMap != null) {
            return stringFieldMap;
        }
        Map<String, Field> map = new HashMap<>();
        Class<?> originalClass = clazz; // 保存原始类用于缓存键
        while (clazz != null && Object.class != clazz && !clazz.isInterface()) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (isSpecialModifier(field.getModifiers())) {
                    continue;
                }
                map.putIfAbsent(field.getName(), field);
            }
            clazz = clazz.getSuperclass();
        }
        FIELD_MAP_CACHE.put(originalClass, map); // 使用原始类作为缓存键
        return map;
    }

    /**
     * 判断是否为特殊修饰符
     *
     * @param modifiers 修饰符
     * @return boolean 是否为特殊修饰符
     */
    public static boolean isSpecialModifier(int modifiers) {
        return Modifier.isStatic(modifiers)
                || Modifier.isFinal(modifiers)
                || Modifier.isNative(modifiers)
                || Modifier.isVolatile(modifiers)
                || Modifier.isTransient(modifiers)
                ;
    }


    /**
     * 复制属性
     *
     * @param source 来源对象
     * @param target 目标对象
     * @param <T>    目标对象类型
     * @return {@link T} 目标对象
     */
    @SneakyThrows
    public static <T> T copyFieldProperties(Object source, T target) {
        if (source == null || target == null || source.equals(target)) return target;
        Map<String, Field> sourceMap = fieldMap(source.getClass());
        Map<String, Field> targetMap = fieldMap(target.getClass());
        for (Field field : sourceMap.values()) {
            Object o = field.get(source);
            if (o == null) continue;
            Field targetFiled = targetMap.get(field.getName());
            if (targetFiled != null && targetFiled.getType().isAssignableFrom(field.getType())) {
                targetFiled.set(target, o);
            }
        }
        return target;
    }


    /**
     * 对象转map
     *
     * @param source 来源对象
     * @return {@link Map} 映射关系
     */
    @SneakyThrows
    public static Map<?, ?> objectToMap(Object source) {
        if (source == null) return null;
        if (source instanceof Map) return (Map<?, ?>) source;
        HashMap<String, Object> map = new HashMap<>();
        Collection<Field> fields = fieldMap(source.getClass()).values();
        for (Field field : fields) {
            Object o = field.get(source);
            if (o == null) continue;
            map.put(field.getName(), o);
        }
        return map;
    }

    /**
     * 对象转对象
     *
     * @param source 来源对象
     * @param clazz  目标类
     * @param <U>    目标类型
     * @return {@link U} 目标对象
     */
    public static <U> U toTarget(Object source, Class<U> clazz) {
        if (source == null) {
            return null;
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must not be null");
        }
        return copyFieldProperties(source, newInstance(clazz));
    }

}