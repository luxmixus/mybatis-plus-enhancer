package io.github.luxmixus.mybatisplus.enhancer.query.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * SQL树接口
 * <p>
 * 定义SQL条件树结构，支持嵌套条件和复杂查询条件的构建
 *
 * @author luxmixus
 */
public interface ISqlTree extends Iterable<ISqlTree> {

    /**
     * 获取条件列表
     *
     * @return 条件列表
     */
    Collection<ISqlCondition> getConditions();

    /**
     * 获取用于连接本层级条件的符号
     *
     * @return 连接符号，如AND或OR
     */
    String getConnector();

    /**
     * 获取子条件
     *
     * @return {@link ISqlTree } 子条件树
     */
    ISqlTree getChild();

    @Override
    @SuppressWarnings("all")
    default Iterator<ISqlTree> iterator() {
        return new Itr(this);
    }

    /**
     * SQL树迭代器内部类
     */
    class Itr implements Iterator<ISqlTree> {

        private ISqlTree current;

        /**
         * 构造函数
         *
         * @param root 根节点
         */
        public Itr(ISqlTree root) {
            current = root;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public ISqlTree next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            ISqlTree result = current;
            current = current.getChild();
            return result;
        }
    }

}