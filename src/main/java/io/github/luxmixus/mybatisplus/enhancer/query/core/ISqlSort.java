package io.github.luxmixus.mybatisplus.enhancer.query.core;

/**
 * SQL排序接口
 * <p>
 * 定义SQL查询中的排序规则，包括排序字段和排序方向
 *
 * @author luxmixus
 */
public interface ISqlSort {

    /**
     * 获取属性名
     *
     * @return {@link String } 属性名
     */
    String getField();

    /**
     * 是否倒序排列
     *
     * @return boolean true表示倒序，false表示正序
     */
    boolean isDesc();

}