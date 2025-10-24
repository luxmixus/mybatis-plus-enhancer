package io.github.luxmixus.mybatisplus.enhancer.query.helper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.luxmixus.mybatisplus.enhancer.core.EnhancedQuery;

import java.util.List;

/**
 * SQL助手包装器
 * <p>
 * 提供对EnhancedQuery接口的包装，简化VO查询操作，支持链式调用
 *
 * @param <T> 实体类型
 * @param <V> VO类型
 * @author luxmixus
 */
public class SqlHelperWrapper<T, V> {
    private final EnhancedQuery<V> enhancedQuery;
    private final ISqlHelper<T> sqlHelper;

    /**
     * 构造方法
     *
     * @param sqlHelper      SQL助手
     * @param enhancedQuery  扩展查询接口
     */
    public SqlHelperWrapper(ISqlHelper<T> sqlHelper, EnhancedQuery<V> enhancedQuery) {
        this.sqlHelper = sqlHelper;
        this.enhancedQuery = enhancedQuery;
    }

    /**
     * 查询单个VO对象
     *
     * @return VO对象
     */
    public V one() {
        return enhancedQuery.voByDTO(this.sqlHelper);
    }

    /**
     * 查询VO对象列表
     *
     * @return VO对象列表
     */
    public List<V> list() {
        return enhancedQuery.voList(this.sqlHelper);
    }

    /**
     * 分页查询VO对象
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 分页结果
     */
    public IPage<V> page(Long current, Long size) {
        return enhancedQuery.voPage(this.sqlHelper, current, size);
    }


}