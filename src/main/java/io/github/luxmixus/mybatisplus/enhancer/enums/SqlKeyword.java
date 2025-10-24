package io.github.luxmixus.mybatisplus.enhancer.enums;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * SQL操作符枚举
 * <p>
 * 定义了常用的SQL操作符及其分类，用于SQL条件构建和验证
 *
 * @author luxmixus
 */
@AllArgsConstructor
public enum SqlKeyword {

    /**
     * AND连接符
     */
    AND("AND"),
    /**
     * OR连接符
     */
    OR("OR"),

    /**
     * 等于操作符
     */
    EQ("="),
    /**
     * 不等于操作符
     */
    NE("<>"),
    /**
     * 不等于操作符（别名）
     */
    NE2("!="),
    /**
     * 小于操作符
     */
    LT("<"),
    /**
     * 小于等于操作符
     */
    LE("<="),
    /**
     * 大于操作符
     */
    GT(">"),
    /**
     * 大于等于操作符
     */
    GE(">="),
    /**
     * 模糊匹配操作符
     */
    LIKE("LIKE"),
    /**
     * 不模糊匹配操作符
     */
    NOT_LIKE("NOT LIKE"),

    /**
     * IS NULL操作符
     */
    IS_NULL("IS NULL"),
    /**
     * IS NOT NULL操作符
     */
    IS_NOT_NULL("IS NOT NULL"),

    /**
     * IN操作符
     */
    IN("IN"),
    /**
     * NOT IN操作符
     */
    NOT_IN("NOT IN"),

    /**
     * 位运算包含操作符
     */
    BIT_WITH("&>"),
    /**
     * 位运算不包含操作符
     */
    BIT_WITHOUT("&="),

//    NOT("NOT"),
//    EXISTS("EXISTS"),
//    NOT_EXISTS("NOT EXISTS"),
//    BETWEEN("BETWEEN"),
//    NOT_BETWEEN("NOT BETWEEN"),
    ;
    /**
     * 操作符关键字
     */
    public final String keyword;

    /**
     * 条件连接符列表
     */
    public static final List<String> CONDITION_CONNECTORS;
    /**
     * 无参数操作符列表
     */
    public static final List<String> CONDITION_OPERATORS_NONE;
    /**
     * 单参数操作符列表
     */
    public static final List<String> CONDITION_OPERATORS_SINGLE;
    /**
     * 多参数操作符列表
     */
    public static final List<String> CONDITION_OPERATORS_MULTI;
    /**
     * 完整操作符列表
     */
    public static final List<String> CONDITION_OPERATORS_COMPLETE;
    /**
     * LIKE操作符列表
     */
    public static final List<String> CONDITION_OPERATORS_LIKE;
    /**
     * 比较操作符列表
     */
    public static final List<String> CONDITION_OPERATORS_COMPARE;

    static {
        List<String> connector = Arrays.asList(AND.keyword, OR.keyword);
        CONDITION_CONNECTORS = Collections.unmodifiableList(connector);
        List<String> none = Arrays.asList(IS_NULL.keyword, IS_NOT_NULL.keyword);
        CONDITION_OPERATORS_NONE = Collections.unmodifiableList(none);
        List<String> single = Arrays.asList(EQ.keyword, NE.keyword, NE2.keyword, GT.keyword, GE.keyword, LT.keyword, LE.keyword, LIKE.keyword, NOT_LIKE.keyword, BIT_WITH.keyword, BIT_WITHOUT.keyword);
        CONDITION_OPERATORS_SINGLE = Collections.unmodifiableList(single);
        List<String> multi = Arrays.asList(IN.keyword, NOT_IN.keyword);
        CONDITION_OPERATORS_MULTI = Collections.unmodifiableList(multi);
        List<String> all = new ArrayList<>();
        all.addAll(none);
        all.addAll(single);
        all.addAll(multi);
        CONDITION_OPERATORS_COMPLETE = Collections.unmodifiableList(all);
        List<String> like = Arrays.asList(LIKE.keyword, NOT_LIKE.keyword);
        CONDITION_OPERATORS_LIKE = Collections.unmodifiableList(like);
        List<String> compare = Arrays.asList(GT.keyword, GE.keyword, LT.keyword, LE.keyword);
        CONDITION_OPERATORS_COMPARE = Collections.unmodifiableList(compare);
    }

    /**
     * 替换连接符
     *
     * @param connector 连接符
     * @return {@link String} 标准化后的连接符
     * @throws IllegalArgumentException 当连接符非法时抛出
     */
    public static String replaceConnector(String connector) {
        if (connector == null || connector.isEmpty()) {
            return AND.keyword;
        }
        connector = connector.toUpperCase();
        if (CONDITION_CONNECTORS.contains(connector)) {
            return connector;
        }
        throw new IllegalArgumentException("illegal operator: " + connector);
    }

    /**
     * 替换操作符
     *
     * @param operator 操作符
     * @return {@link String} 标准化后的操作符
     * @throws IllegalArgumentException 当操作符非法时抛出
     */
    public static String replaceOperator(String operator) {
        if (operator == null || operator.isEmpty()) {
            return EQ.keyword;
        }
        operator = operator.toUpperCase();
        if (CONDITION_OPERATORS_COMPLETE.contains(operator)) {
            if (NE2.keyword.equals(operator)) {
                return NE.keyword;
            }
            return operator;
        }
        throw new IllegalArgumentException("illegal operator: " + operator);
    }

    /**
     * 判断是否为无参数操作符
     *
     * @param operator 操作符
     * @return boolean 是否为无参数操作符
     */
    public static boolean isNoneArgOperator(String operator) {
        return CONDITION_OPERATORS_NONE.contains(operator);
    }

    /**
     * 判断是否为单参数操作符
     *
     * @param operator 操作符
     * @return boolean 是否为单参数操作符
     */
    public static boolean isSingleArgOperator(String operator) {
        return CONDITION_OPERATORS_SINGLE.contains(operator);
    }

    /**
     * 判断是否为多参数操作符
     *
     * @param operator 操作符
     * @return boolean 是否为多参数操作符
     */
    public static boolean isMultiArgOperator(String operator) {
        return CONDITION_OPERATORS_MULTI.contains(operator);
    }

    /**
     * 判断是否为有效操作符
     *
     * @param operator 操作符
     * @return boolean 是否为有效操作符
     */
    public static boolean isOperator(String operator) {
        return CONDITION_OPERATORS_COMPLETE.contains(operator);
    }

    /**
     * 判断是否为LIKE操作符
     *
     * @param operator 操作符
     * @return boolean 是否为LIKE操作符
     */
    public static boolean isLikeOperator(String operator) {
        return CONDITION_OPERATORS_LIKE.contains(operator);
    }

}