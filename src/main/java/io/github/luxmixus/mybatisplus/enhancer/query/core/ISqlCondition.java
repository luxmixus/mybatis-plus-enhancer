package io.github.luxmixus.mybatisplus.enhancer.query.core;

/**
 * SQL条件接口
 * <p>
 * 定义SQL查询条件的基本结构，包含字段名、操作符和值三个基本属性
 *
 * @author luxmixus
 */
public interface ISqlCondition {

    /**
     * 获取属性名
     *
     * @return {@link String } 属性名
     */
    String getField();

    /**
     * 获取操作符
     *
     * @return {@link String } 操作符
     */
    String getOperator();

    /**
     * 获取值
     *
     * @return {@link Object } 值
     */
    Object getValue();

}