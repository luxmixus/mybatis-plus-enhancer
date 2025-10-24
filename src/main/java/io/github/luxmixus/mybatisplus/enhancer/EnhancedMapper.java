package io.github.luxmixus.mybatisplus.enhancer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.luxmixus.mybatisplus.enhancer.core.EnhancedQuery;
import io.github.luxmixus.mybatisplus.enhancer.query.helper.SqlHelper;
import io.github.luxmixus.mybatisplus.enhancer.query.helper.processor.FieldSuffixProcessor;
import io.github.luxmixus.mybatisplus.enhancer.util.MybatisPlusReflectUtil;

import java.util.List;

/**
 * 动态Mapper接口
 * <p>
 * 提供动态SQL查询功能，支持通过条件对象进行VO查询
 *
 * @param <V> VO类型
 * @author luxmixus
 */
public interface EnhancedMapper<V> extends EnhancedQuery<V> {

    /**
     * 通过XML进行VO查询
     *
     * @param param 参数对象
     * @param page 分页对象
     * @return {@link List} VO对象列表
     */
    List<V> voQueryByXml(Object param, IPage<V> page);

    /**
     * VO查询
     *
     * @param param 查询参数
     * @param page  分页对象
     * @return {@link List} VO对象列表
     */
    @Override
    default List<V> voQuery(Object param, IPage<V> page) {
        Class<?> entityClass = MybatisPlusReflectUtil.resolveTypeArguments(getClass(), BaseMapper.class)[0];
        return voQueryByXml(SqlHelper.of(entityClass).with(param).process(FieldSuffixProcessor.of()::process), page);
    }

}