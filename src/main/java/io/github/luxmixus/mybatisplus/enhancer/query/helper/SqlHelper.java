package io.github.luxmixus.mybatisplus.enhancer.query.helper;

import io.github.luxmixus.mybatisplus.enhancer.core.EnhancedQuery;
import io.github.luxmixus.mybatisplus.enhancer.enums.SqlKeyword;

import java.util.function.Consumer;

/**
 * SQL助手实现类
 * <p>
 * 提供SQL构建的具体实现，支持链式调用和Lambda表达式
 *
 * @param <T> 实体类型
 * @author luxmixus
 */
@SuppressWarnings("unused")
public class SqlHelper<T> extends AbstractSqlHelper<T, SqlHelper<T>> {

    /**
     * 创建指定实体类型的SQL助手实例
     *
     * @param clazz 实体类
     * @param <T>   实体类型
     * @return {@link SqlHelper} SQL助手实例
     */
    public static <T> SqlHelper<T> of(Class<T> clazz) {
        SqlHelper<T> sqlHelper = new SqlHelper<>();
        sqlHelper.entityClass = clazz;
        return sqlHelper;
    }

    /**
     * 添加OR条件
     *
     * @param sqlHelper 拼装或条件的函数
     * @return {@link SqlHelper} 当前实例
     */
    @Override
    public SqlHelper<T> or(Consumer<SqlHelper<T>> sqlHelper) {
        SqlHelper<T> child = new SqlHelper<>();
        child.connector = SqlKeyword.OR.keyword;
        sqlHelper.accept(child);
        this.addChild(child);
        return this;
    }

    /**
     * 创建SQL助手包装器，用于将SQL助手实例包装为EnhancedQuery实例
     *
     * @param enhancedQuery EnhancedQuery实例
     * @param <V>           封装的VO类型
     * @return {@link SqlHelperWrapper} SQL助手包装器实例
     */
    public <V> SqlHelperWrapper<T, V> wrap(EnhancedQuery<V> enhancedQuery) {
        return new SqlHelperWrapper<>(this, enhancedQuery);
    }

}