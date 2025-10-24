package io.github.luxmixus.mybatisplus.enhancer.core;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.luxmixus.mybatisplus.enhancer.util.CastUtil;
import io.github.luxmixus.mybatisplus.enhancer.util.MybatisPlusReflectUtil;


/**
 * 扩展IService接口
 * <p>
 * 提供额外的服务层功能，包括DTO转换、实体操作等
 *
 * @author luxmixus
 */
@SuppressWarnings("unused")
public interface EnhancedIService {
//public interface EnhancedIService<T> extends IService<T> {

//    default T toEntity(Object source) {
//        return MybatisPlusReflectHelper.toTarget(source, getEntityClass());
//    }
    
    /**
     * 获取对象的ID值
     *
     * @param source 源对象
     * @return {@link Object} ID值
     */
    default Object toId(Object source) {
        IService<?> iService = CastUtil.cast(this,IService.class);
        TableInfo tableInfo = TableInfoHelper.getTableInfo(iService.getEntityClass());
        if (tableInfo == null) return null;
        String keyProperty = tableInfo.getKeyProperty();
        if (keyProperty == null) return null;
        return tableInfo.getPropertyValue(source, keyProperty);
    }

    /**
     * 通过DTO插入数据
     *
     * @param s DTO对象
     * @return {@link Object} ID值
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    default Object insertByDTO(Object s) {
        IService iService = CastUtil.cast(this,IService.class);
        Object entity = MybatisPlusReflectUtil.toTarget(s, iService.getEntityClass());
        iService.save(entity);
        return toId(entity);
    }

    /**
     * 通过DTO更新数据
     *
     * @param s DTO对象
     * @return boolean 是否更新成功
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    default boolean updateByDTO(Object s) {
        IService iService = CastUtil.cast(this,IService.class);
        Object entity = MybatisPlusReflectUtil.toTarget(s, iService.getEntityClass());
        return iService.updateById(entity);
    }


}