package io.github.luxmixus.mybatisplus.enhancer.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.luxmixus.mybatisplus.enhancer.core.EnhancedEntity;
import lombok.SneakyThrows;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MyBatis-Plus反射工具类
 * <p>
 * 提供针对MyBatis-Plus的反射工具方法，包括泛型解析、字段映射等
 *
 * @author luxmixus
 */
public abstract class MybatisPlusReflectUtil extends ReflectUtil {

    /**
     * 实体类字段到数据库列的映射缓存
     */
    private static final Map<Class<?>, Map<String, String>> FIELD_TO_JDBC_COLUMN_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 解析超类泛型参数
     *
     * @param clazz      指定类
     * @param superClass 超类
     * @return {@link Class} 泛型参数数组
     */
    public static Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> superClass) {
        return GenericTypeUtils.resolveTypeArguments(clazz, superClass);
    }

    /**
     * 获取ID字段属性名
     *
     * @param clazz 实体类
     * @return {@link String} ID字段属性名
     */
    public static String idFieldPropertyName(Class<?> clazz) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        if (tableInfo == null) {
            return null;
        }
        return tableInfo.getKeyProperty();
    }

    /**
     * 获取getter方法对应的字段名
     *
     * @param getter getter方法
     * @return {@link String} 字段名
     */
    public static String getterFieldName(SFunction<?, ?> getter) {
        return PropertyNamer.methodToProperty(LambdaUtils.extract(getter).getImplMethodName());
    }

    /**
     * 从MyBatis Plus获取实体类属性与数据库字段转换映射
     *
     * @param clazz 实体类
     * @return {@link Map} 字段到列的映射关系
     */
    public static Map<String, String> field2JdbcColumnByMybatisPlusTableInfo(Class<?> clazz) {
        if (clazz == null) {
            return new HashMap<>();
        }
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        if (tableInfo == null) {
            return new HashMap<>();
        }
        Map<String, String> result = field2JdbcColumnByTableInfo(tableInfo);
        TableFieldInfo logicDeleteFieldInfo = tableInfo.getLogicDeleteFieldInfo();
        if (logicDeleteFieldInfo != null) {
            String name = logicDeleteFieldInfo.getField().getName();
            result.remove(name);
        }
        return result;
    }

    private static Map<String, String> field2JdbcColumnByTableInfo(TableInfo tableInfo) {
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        Map<String, String> result = new HashMap<>();
        String keyProperty = tableInfo.getKeyProperty();
        String keyColumn = tableInfo.getKeyColumn();
        if (keyProperty != null && keyColumn != null) {
            result.put(keyProperty, keyColumn);
        }
        for (TableFieldInfo fieldInfo : fieldList) {
            Field field = fieldInfo.getField();
            String fieldName = field.getName();
            String jdbcColumn = fieldInfo.getColumn();
            result.put(fieldName, jdbcColumn);
        }
        return result;
    }

    /**
     * 实体类与数据库字段转换映射（基于注解）
     *
     * @param clazz 实体类
     * @return {@link Map} 字段到列的映射关系
     */
    public static Map<String, String> field2JdbcColumnByMybatisPlusAnnotation(Class<?> clazz) {
        if (clazz == null) {
            return new HashMap<>();
        }
        HashMap<String, String> result = new HashMap<>();
        Map<String, Field> fieldMap = fieldMap(clazz);
        for (Field field : fieldMap.values()) {
            String fieldName = field.getName();
//            TableLogic tableLogic = field.getAnnotation(TableLogic.class);
//            if (tableLogic != null) {
//                String value = tableLogic.value();
//                if (!value.isEmpty() && !result.containsKey(value)) {
//                    result.putIfAbsent(fieldName, value);
//                    continue;
//                }
//            }
//            TableId tableId = field.getAnnotation(TableId.class);
//            if (tableId != null) {
//                String value = tableId.value();
//                if (!value.isEmpty() && !result.containsKey(value)) {
//                    result.putIfAbsent(fieldName, value);
//                    continue;
//                }
//            }
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField != null) {
                boolean exist = tableField.exist();
                String value = tableField.value();
                if (!exist && value != null && !result.containsKey(value)) {
                    result.putIfAbsent(fieldName, value);
                    continue;
                }
            }
        }
        return result;
    }

    /**
     * 获取实体类属性与数据库字段的映射关系
     * 包含:
     * 1.mybatis-plus实体类属性与字段映射信息
     * 2.mybatis-plus注解指定的映射信息
     * 3.实现了EnhanceEntity接口的映射信息
     *
     * @param entityClass 实体类
     * @return {@link Map} 字段到列的映射关系
     */
    public static Map<String, String> field2JdbcColumnMap(Class<?> entityClass) {
        Map<String, String> map = FIELD_TO_JDBC_COLUMN_CACHE_MAP.get(entityClass);
        if (map != null) {
            return map;
        }
        Map<String, String> result = field2JdbcColumnMap(entityClass, "a.%s", ".");
        FIELD_TO_JDBC_COLUMN_CACHE_MAP.put(entityClass, result);
        return result;
    }

    @SneakyThrows
    public static Map<String, String> field2JdbcColumnMapByEnhancedEntity(Class<?> entityClass) {
        if (entityClass == null) {
            return new HashMap<>();
        }
        if (EnhancedEntity.class.isAssignableFrom(entityClass)) {
            EnhancedEntity enhanceEntity = (EnhancedEntity) entityClass.getConstructor().newInstance();
            Map<String, String> extraFieldColumnMap = enhanceEntity.extraFieldColumnMap();
            return extraFieldColumnMap != null ? extraFieldColumnMap : new HashMap<>();
        }
        return new HashMap<>();
    }

    /**
     * 获取实体类属性与数据库字段的映射关系
     *
     * @param entityClass  实体类
     * @param columnFormat 数据库字段映射格式{@link String#format(String, Object...)}
     * @param ignoreFormat 当字段名包含该值时,不进行字段映射
     * @return {@link Map} 字段到列的映射关系
     */
    public static Map<String, String> field2JdbcColumnMap(Class<?> entityClass, String columnFormat, String ignoreFormat) {
        String format = columnFormat == null || columnFormat.isEmpty() ? "%s" : columnFormat;
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        Map<String, String> tableInfoMap = field2JdbcColumnByMybatisPlusTableInfo(entityClass);
        Map<String, String> annotationMap = field2JdbcColumnByMybatisPlusAnnotation(entityClass);
        Map<String, String> enhancedEntityMap = field2JdbcColumnMapByEnhancedEntity(entityClass);
        // 表信息优先
        tableInfoMap.forEach((key, value) -> {
            if (ignoreFormat != null && value.contains(ignoreFormat)) {
                result.putIfAbsent(key, value);
            } else {
                result.putIfAbsent(key, String.format(format, value));
            }
        });
        annotationMap.forEach((key, value) -> {
            if (ignoreFormat != null && value.contains(ignoreFormat)) {
                result.putIfAbsent(key, value);
            } else {
                result.putIfAbsent(key, String.format(format, value));
            }
        });
        enhancedEntityMap.forEach((key, value) -> {
            if (ignoreFormat != null && value.contains(ignoreFormat)) {
                result.putIfAbsent(key, value);
            } else {
                result.putIfAbsent(key, String.format(format, value));
            }
        });
        return result;
    }


}