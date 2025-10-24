package io.github.luxmixus.mybatisplus.enhancer.query.helper;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.luxmixus.mybatisplus.enhancer.enums.SqlKeyword;
import io.github.luxmixus.mybatisplus.enhancer.query.entity.SqlCondition;
import io.github.luxmixus.mybatisplus.enhancer.query.entity.SqlSort;
import io.github.luxmixus.mybatisplus.enhancer.util.MybatisPlusReflectUtil;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Lambda SQL助手接口
 * <p>
 * 提供基于Lambda表达式的SQL条件构建方法，支持链式调用
 *
 * @param <T> 实体类型
 * @param <S> 返回类型（用于支持链式调用）
 * @author luxmixus
 */
@SuppressWarnings({"unchecked", "unused"})
public interface ISqlHelperLambda<T, S extends ISqlHelperLambda<T, S>> extends ISqlHelper<T> {

    /**
     * 添加OR条件
     *
     * @param consumer 拼装或条件的函数
     * @return {@link S } 当前实例
     */
    S or(Consumer<S> consumer);

    /**
     * 等于条件
     *
     * @param getter 对象getter方法
     * @param value  值
     * @param <R>    值的类型
     * @return this
     */
    default <R> S eq(SFunction<T, R> getter, R value) {
        if (value == null) {
            return (S) this;
        }
        getConditions().add(new SqlCondition(MybatisPlusReflectUtil.getterFieldName(getter), SqlKeyword.EQ.keyword, value));
        return (S) this;
    }

    /**
     * 不等于条件
     *
     * @param getter 对象getter方法
     * @param value  值
     * @param <R>    值的类型
     * @return this
     */
    default <R> S ne(SFunction<T, R> getter, R value) {
        if (value == null) {
            return (S) this;
        }
        getConditions().add(new SqlCondition(MybatisPlusReflectUtil.getterFieldName(getter), SqlKeyword.NE.keyword, value));
        return (S) this;
    }

    /**
     * 大于条件
     *
     * @param getter 对象getter方法
     * @param value  值
     * @param <R>    值的类型
     * @return this
     */
    default <R> S gt(SFunction<T, R> getter, R value) {
        if (value == null) {
            return (S) this;
        }
        getConditions().add(new SqlCondition(MybatisPlusReflectUtil.getterFieldName(getter), SqlKeyword.GT.keyword, value));
        return (S) this;
    }

    /**
     * 大于等于条件
     *
     * @param getter 对象getter方法
     * @param value  值
     * @param <R>    值的类型
     * @return this
     */
    default <R> S ge(SFunction<T, R> getter, R value) {
        if (value == null) {
            return (S) this;
        }
        getConditions().add(new SqlCondition(MybatisPlusReflectUtil.getterFieldName(getter), SqlKeyword.GE.keyword, value));
        return (S) this;
    }

    /**
     * 小于条件
     *
     * @param getter 对象getter方法
     * @param value  值
     * @param <R>    值的类型
     * @return this
     */
    default <R> S lt(SFunction<T, R> getter, R value) {
        if (value == null) {
            return (S) this;
        }
        getConditions().add(new SqlCondition(MybatisPlusReflectUtil.getterFieldName(getter), SqlKeyword.LT.keyword, value));
        return (S) this;
    }

    /**
     * 小于等于条件
     *
     * @param getter 对象getter方法
     * @param value  值
     * @param <R>    值的类型
     * @return this
     */
    default <R> S le(SFunction<T, R> getter, R value) {
        if (value == null) {
            return (S) this;
        }
        getConditions().add(new SqlCondition(MybatisPlusReflectUtil.getterFieldName(getter), SqlKeyword.LE.keyword, value));
        return (S) this;
    }

    /**
     * 模糊查询条件
     *
     * @param getter 对象getter方法
     * @param value  值
     * @param <R>    值的类型
     * @return this
     */
    default <R> S like(SFunction<T, R> getter, R value) {
        if (value == null) {
            return (S) this;
        }
        getConditions().add(new SqlCondition(MybatisPlusReflectUtil.getterFieldName(getter), SqlKeyword.LIKE.keyword, value));
        return (S) this;
    }

    /**
     * 不模糊查询条件
     *
     * @param getter 对象getter方法
     * @param value  值
     * @param <R>    值的类型
     * @return this
     */
    default <R> S notLike(SFunction<T, R> getter, R value) {
        if (value == null) {
            return (S) this;
        }
        getConditions().add(new SqlCondition(MybatisPlusReflectUtil.getterFieldName(getter), SqlKeyword.NOT_LIKE.keyword, value));
        return (S) this;
    }

    /**
     * in查询条件
     *
     * @param getter 对象getter方法
     * @param value  值
     * @param <R>    值的类型
     * @return this
     */
    default <R> S in(SFunction<T, R> getter, Collection<? extends R> value) {
        if (value == null) {
            return (S) this;
        }
        getConditions().add(new SqlCondition(MybatisPlusReflectUtil.getterFieldName(getter), SqlKeyword.IN.keyword, value));
        return (S) this;
    }

    /**
     * not in查询条件
     *
     * @param getter 对象getter方法
     * @param value  值
     * @param <R>    值的类型
     * @return this
     */
    default <R> S notIn(SFunction<T, R> getter, Collection<? extends R> value) {
        if (value == null) {
            return (S) this;
        }
        getConditions().add(new SqlCondition(MybatisPlusReflectUtil.getterFieldName(getter), SqlKeyword.NOT_IN.keyword, value));
        return (S) this;
    }

    /**
     * 指定字段为空条件
     *
     * @param getter 对象getter方法
     * @return this
     */
    default S isNull(SFunction<T, ?> getter) {
        getConditions().add(new SqlCondition(MybatisPlusReflectUtil.getterFieldName(getter), SqlKeyword.IS_NULL.keyword, null));
        return (S) this;
    }

    /**
     * 指定字段不为空条件
     *
     * @param getter 对象getter方法
     * @return this
     */
    default S isNotNull(SFunction<T, ?> getter) {
        getConditions().add(new SqlCondition(MybatisPlusReflectUtil.getterFieldName(getter), SqlKeyword.IS_NOT_NULL.keyword, null));
        return (S) this;
    }

    /**
     * 按指定字段正序排序
     *
     * @param getter 对象getter方法
     * @return this
     */
    default S orderByAsc(SFunction<T, ?> getter) {
        getSorts().add(new SqlSort(MybatisPlusReflectUtil.getterFieldName(getter), false));
        return (S) this;
    }

    /**
     * 按指定字段倒序排序
     *
     * @param getter 对象getter方法
     * @return this
     */
    default S orderByDesc(SFunction<T, ?> getter) {
        getSorts().add(new SqlSort(MybatisPlusReflectUtil.getterFieldName(getter), true));
        return (S) this;
    }

    /**
     * 位运算条件，具有指定值对应的位码
     *
     * @param getter 对象getter方法
     * @param value  值
     * @param <R>    值的类型
     * @return this
     */
    default <R> S bitWith(SFunction<T, R> getter, R value) {
        if (value == null) {
            return (S) this;
        }
        getConditions().add(new SqlCondition(MybatisPlusReflectUtil.getterFieldName(getter), SqlKeyword.BIT_WITH.keyword, value));
        return (S) this;
    }

    /**
     * 位运算条件，不具有指定值对应的位码
     *
     * @param getter 对象getter方法
     * @param value  值
     * @param <R>    值的类型
     * @return this
     */
    default <R> S bitWithout(SFunction<T, R> getter, R value) {
        if (value == null) {
            return (S) this;
        }
        getConditions().add(new SqlCondition(MybatisPlusReflectUtil.getterFieldName(getter), SqlKeyword.BIT_WITHOUT.keyword, value));
        return (S) this;
    }

}