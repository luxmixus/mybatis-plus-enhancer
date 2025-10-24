package io.github.luxmixus.mybatisplus.enhancer.core;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.luxmixus.mybatisplus.enhancer.enums.SqlKeyword;
import io.github.luxmixus.mybatisplus.enhancer.query.entity.SqlCondition;
import io.github.luxmixus.mybatisplus.enhancer.util.MybatisPlusReflectUtil;
import org.apache.ibatis.exceptions.TooManyResultsException;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 扩展查询接口
 * <p>
 * 提供VO查询功能，支持单个查询、列表查询和分页查询
 *
 * @param <V> VO类型
 * @author luxmixus
 */
@SuppressWarnings("unused")
public interface EnhancedQuery<V> {

    /**
     * VO查询
     *
     * @param param 查询参数
     * @param page  分页对象
     * @return {@link List} VO对象列表
     */
    List<V> voQuery(Object param, IPage<V> page);

    /**
     * 获取VO类
     *
     * @return {@link Class} VO类
     */
    @SuppressWarnings("unchecked")
    default Class<V> getVOClass() {
        return (Class<V>) MybatisPlusReflectUtil.resolveTypeArguments(getClass(), EnhancedQuery.class)[0];
    }

    /**
     * 转换为VO对象
     *
     * @param source 源对象
     * @return {@link V} VO对象
     */
    default V toVO(Object source) {
        return MybatisPlusReflectUtil.toTarget(source, getVOClass());
    }

    /**
     * 根据ID查询VO对象
     *
     * @param id ID值
     * @return {@link V} VO对象
     * @throws IllegalArgumentException 当ID为空或实体无ID字段时抛出
     * @throws TooManyResultsException  当查询结果超过一个时抛出
     */
    default V voById(Serializable id) {
        if (id == null) throw new IllegalArgumentException("id can't be null");
        Class<?> clazz = MybatisPlusReflectUtil.resolveTypeArguments(getClass(), IService.class)[0];
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        if (tableInfo == null) throw new IllegalArgumentException("there is no id field in entity");
        String keyProperty = tableInfo.getKeyProperty();
        if (keyProperty == null) throw new IllegalArgumentException("there is no id field in entity");
        SqlCondition condition = new SqlCondition(keyProperty, SqlKeyword.EQ.keyword, id);
        return voByDTO(condition);
    }

    /**
     * 根据ID查询VO对象并转换为目标类型
     *
     * @param id    ID值
     * @param clazz 目标类
     * @param <R>   目标类型
     * @return {@link R} 目标对象
     */
    default <R> R voById(Serializable id, Class<R> clazz) {
        return MybatisPlusReflectUtil.toTarget(voById(id), clazz);
    }

    /**
     * 通过DTO查询单个VO对象
     *
     * @param param 查询参数
     * @return {@link V} VO对象
     * @throws TooManyResultsException 当查询结果超过一个时抛出
     */
    default V voByDTO(Object param) {
        List<V> vs = voList(param);
        if (vs == null || vs.isEmpty()) return null;
        if (vs.size() > 1) throw new TooManyResultsException("error query => required one but found " + vs.size());
        return vs.get(0);
    }

    /**
     * 通过DTO查询单个VO对象并转换为目标类型
     *
     * @param param 查询参数
     * @param clazz 目标类
     * @param <R>   目标类型
     * @return {@link R} 目标对象
     */
    default <R> R voByDTO(Object param, Class<R> clazz) {
        return MybatisPlusReflectUtil.toTarget(voByDTO(param), clazz);
    }

    /**
     * 查询VO对象列表（无条件）
     *
     * @return {@link List} VO对象列表
     */
    default List<V> voList() {
        return voQuery(null, null);
    }

    /**
     * 通过DTO查询VO对象列表
     *
     * @param param 查询参数
     * @return {@link List} VO对象列表
     */
    default List<V> voList(Object param) {
        return voQuery(param, null);
    }

    /**
     * 通过DTO查询VO对象列表并转换为目标类型
     *
     * @param param 查询参数
     * @param clazz 目标类
     * @param <R>   目标类型
     * @return {@link List} 目标对象列表
     */
    default <R> List<R> voList(Object param, Class<R> clazz) {
        return voList(param).stream()
                .map(e -> MybatisPlusReflectUtil.toTarget(e, clazz))
                .collect(Collectors.toList());
    }

    /**
     * 分页查询VO对象（无条件）
     *
     * @param current 查询参数
     * @param size    每页大小
     * @return {@link IPage} 分页结果
     */
    default IPage<V> voPage(Long current, Long size) {
        if (current == null || current < 1) current = 1L;
        if (size == null) size = 10L;
        IPage<V> page = new Page<>(current, size);
        List<V> vs = voQuery(null, page);
        page.setRecords(vs);
        return page;
    }

    /**
     * 通过DTO分页查询VO对象
     *
     * @param param   查询参数
     * @param current 当前页
     * @param size    每页大小
     * @return {@link IPage} 分页结果
     */
    default IPage<V> voPage(Object param, Long current, Long size) {
        if (current == null || current < 1) current = 1L;
        if (size == null) size = 10L;
        IPage<V> page = new Page<>(current, size);
        List<V> vs = voQuery(param, page);
        page.setRecords(vs);
        return page;
    }

    /**
     * 通过DTO分页查询VO对象并转换为目标类型
     *
     * @param param   查询参数
     * @param current 当前页
     * @param size    每页大小
     * @param clazz   目标类
     * @param <R>     目标类型
     * @return {@link IPage} 分页结果
     */
    @SuppressWarnings("unchecked")
    default <R> IPage<R> voPage(Object param, Long current, Long size, Class<R> clazz) {
        IPage<R> vp = (IPage<R>) voPage(param, current, size);
        vp.setRecords(
                vp.getRecords().stream()
                        .map(e -> MybatisPlusReflectUtil.toTarget(e, clazz))
                        .collect(Collectors.toList())
        );
        return vp;
    }

}