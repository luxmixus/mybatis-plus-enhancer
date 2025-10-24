package io.github.luxmixus.mybatisplus.enhancer.query.entity;

import io.github.luxmixus.mybatisplus.enhancer.enums.SqlKeyword;
import io.github.luxmixus.mybatisplus.enhancer.query.core.ISqlCondition;
import lombok.*;

/**
 * SQL条件实体类
 * <p>
 * 实现ISqlCondition接口，用于表示SQL查询中的单个条件，包含字段名、操作符和值
 *
 * @author luxmixus
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class SqlCondition implements ISqlCondition {

    /**
     * 字段名
     */
    protected String field;
    /**
     * 操作符
     */
    protected String operator;
    /**
     * 值
     */
    protected Object value;

    {
        this.operator = SqlKeyword.EQ.keyword;
    }

    /**
     * 构造函数，使用字段名和值创建条件，默认操作符为等于(EQ)
     *
     * @param field 字段名
     * @param value 值
     */
    public SqlCondition(String field, Object value) {
        this.field = field;
        this.value = value;
    }
    
    public SqlCondition(String field, String operator, Object value) {
        this.field = field;
        this.operator = SqlKeyword.replaceOperator(operator);
        this.value = value;
    }

    /**
     * 从ISqlCondition创建SqlCondition实例
     *
     * @param sqlCondition SQL条件接口实例
     * @return {@link SqlCondition} SQL条件实体实例
     */
    public static SqlCondition of(ISqlCondition sqlCondition) {
        return new SqlCondition(sqlCondition.getField(), sqlCondition.getOperator(), sqlCondition.getValue());
    }


}