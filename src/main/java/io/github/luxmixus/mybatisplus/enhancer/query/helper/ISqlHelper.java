package io.github.luxmixus.mybatisplus.enhancer.query.helper;

import io.github.luxmixus.mybatisplus.enhancer.query.core.ISqlEntity;

import java.util.Map;
import java.util.function.Function;

/**
 * SQL助手接口
 * <p>
 * 定义SQL助手的基本功能，包括获取实体类、未映射参数等
 *
 * @param <T> 实体类型
 * @author luxmixus
 */
public interface ISqlHelper<T> extends ISqlEntity {

    /**
     * 获取实体类
     *
     * @return 实体类
     */
    Class<T> getEntityClass();

    /**
     * 获取未映射的参数
     *
     * @return 未映射参数的Map
     */
    Map<String, Object> getUnmapped();
    
    /**
     * 处理SQL助手
     *
     * @param processor 处理函数
     * @return {@link ISqlHelper} 处理后的SQL助手
     */
    default ISqlHelper<T> process(Function<ISqlHelper<T>,ISqlHelper<T>> processor){
        return processor.apply(this);
    }
    
}