package io.github.luxmixus.mybatisplus.enhancer.util;

/**
 * 类型转换工具类
 * <p>
 * 提供安全的类型转换功能
 *
 * @author luxmixus
 */
public class CastUtil {

    /**
     * 安全类型转换
     *
     * @param obj   待转换对象
     * @param clazz 目标类型
     * @param <T>   目标类型泛型
     * @return {@link T} 转换后的对象
     * @throws IllegalArgumentException 当对象无法转换为目标类型时抛出
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj, Class<T> clazz) {
        boolean b = clazz.isAssignableFrom(obj.getClass());
        if (!b) {
            throw new IllegalArgumentException("obj is not a " + clazz.getName());
        }
        return (T) obj;
    }

}