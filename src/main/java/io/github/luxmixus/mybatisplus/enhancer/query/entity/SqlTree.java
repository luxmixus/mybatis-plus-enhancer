package io.github.luxmixus.mybatisplus.enhancer.query.entity;

import io.github.luxmixus.mybatisplus.enhancer.enums.SqlKeyword;
import io.github.luxmixus.mybatisplus.enhancer.query.core.ISqlCondition;
import io.github.luxmixus.mybatisplus.enhancer.query.core.ISqlTree;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * SQL条件树实体类
 * <p>
 * 实现ISqlTree接口，用于表示SQL查询条件的树形结构，支持嵌套条件和复杂查询
 *
 * @author luxmixus
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class SqlTree implements ISqlTree {
    /**
     * 条件列表
     */
    protected Collection<ISqlCondition> conditions;
    /**
     * 条件列表中条件的连接符
     */
    protected String connector;
    /**
     * 子条件
     */
    protected SqlTree child;

    {
        this.conditions = new LinkedHashSet<>();
        connector = SqlKeyword.AND.keyword;
    }


    /**
     * 使用条件集合构造SqlTree
     *
     * @param conditions 条件集合
     */
    public SqlTree(Collection<ISqlCondition> conditions) {
        this.conditions.addAll(conditions);
    }

    /**
     * 使用条件集合和连接符构造SqlTree
     *
     * @param conditions 条件集合
     * @param connector     连接符，只能是AND或OR
     * @throws IllegalArgumentException 当symbol不是AND或OR时抛出
     */
    public SqlTree(Collection<ISqlCondition> conditions, String connector) {
        this.conditions.addAll(conditions);
        this.connector = SqlKeyword.replaceConnector(connector);
    }

    /**
     * 添加单个子节点
     *
     * @param child 子节点
     * @return {@link SqlTree} 当前实例
     */
    private SqlTree addSingleChild(ISqlTree child) {
        if (child == null || child.getConditions().isEmpty()) {
            return this;
        }
        // create a new child to avoid circling reference
        SqlTree newChild = new SqlTree(child.getConditions(), SqlKeyword.OR.keyword);
        SqlTree current = this;
        while (current.getChild() != null) {
            current = current.getChild();
        }
        current.child = newChild;
        return this;
    }

    /**
     * 添加子节点
     *
     * @param sqlTree SQL树
     * @return {@link SqlTree} 当前实例
     */
    protected SqlTree addChild(ISqlTree sqlTree) {
        if (sqlTree == null) {
            return this;
        }
        for (ISqlTree node : sqlTree) {
            if (node.getConditions().isEmpty()) {
                continue;
            }
            String symbol1 = node.getConnector();
            if (SqlKeyword.OR.keyword.equals(symbol1)) {
                // put or conditions as child
                this.addSingleChild(node);
            } else {
                // put and conditions to current level
                this.getConditions().addAll(node.getConditions());
            }
        }
        return this;
    }

}