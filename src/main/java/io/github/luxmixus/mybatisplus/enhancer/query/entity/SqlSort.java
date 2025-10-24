package io.github.luxmixus.mybatisplus.enhancer.query.entity;

import io.github.luxmixus.mybatisplus.enhancer.query.core.ISqlSort;
import lombok.*;

/**
 * SQL排序实体类
 * <p>
 * 实现ISqlSort接口，用于表示SQL查询中的排序规则
 *
 * @author luxmixus
 */

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SqlSort implements ISqlSort {

    /**
     * 排序字段
     */
    protected String field;

    /**
     * 是否倒序
     */
    protected boolean desc;

    /**
     * 从ISqlSort创建SqlSort实例
     *
     * @param sort SQL排序接口实例
     * @return {@link SqlSort} SQL排序实体实例
     */
    public static SqlSort of(ISqlSort sort) {
        return new SqlSort(sort.getField(), sort.isDesc());
    }

}