package io.github.luxmixus.mybatisplus.enhancer.core;

import java.util.Map;

/**
 * 
 * @author luxmixus
 */
public interface EnhancedEntity {
    /**
     * 非实体类对应表的属性字段映射
     * key为实体类属性名
     * value为数据库字段名
     *
     * @return {@link Map } 非实体类对应表的属性字段映射
     */
    Map<String, String> extraFieldColumnMap();
}
