package io.github.luxmixus.mybatisplus.enhancer.query.core;

import java.util.Collection;

/**
 * SQL实体接口
 * <p>
 * 扩展自ISqlTree接口，增加了排序功能的定义
 *
 * @author luxmixus
 */
public interface ISqlEntity extends ISqlTree {

    /**
     * 获取排序字段列表
     *
     * @return 排序字段列表
     */
    Collection<ISqlSort> getSorts();

}