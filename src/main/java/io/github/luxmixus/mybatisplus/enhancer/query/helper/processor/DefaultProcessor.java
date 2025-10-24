package io.github.luxmixus.mybatisplus.enhancer.query.helper.processor;

import io.github.luxmixus.mybatisplus.enhancer.enums.SqlKeyword;
import io.github.luxmixus.mybatisplus.enhancer.query.core.ISqlCondition;
import io.github.luxmixus.mybatisplus.enhancer.query.core.ISqlSort;
import io.github.luxmixus.mybatisplus.enhancer.query.core.ISqlTree;
import io.github.luxmixus.mybatisplus.enhancer.query.entity.SqlCondition;
import io.github.luxmixus.mybatisplus.enhancer.query.entity.SqlSort;
import io.github.luxmixus.mybatisplus.enhancer.query.entity.SqlTree;
import io.github.luxmixus.mybatisplus.enhancer.query.helper.AbstractSqlHelper;
import io.github.luxmixus.mybatisplus.enhancer.query.helper.ISqlHelper;
import io.github.luxmixus.mybatisplus.enhancer.query.helper.SqlHelper;
import io.github.luxmixus.mybatisplus.enhancer.util.MybatisPlusReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * 默认SQL处理器
 * <p>
 * 提供SQL条件验证和处理功能，包括字段映射验证、操作符验证等
 *
 * @author luxmixus
 */
@Slf4j
public abstract class DefaultProcessor {

    /**
     * 验证SQL条件
     *
     * @param sqlCondition         SQL条件
     * @param field2JdbcColumnMap  字段到数据库列的映射
     * @param unmapped             未映射参数集合
     * @return {@link ISqlCondition} 验证后的SQL条件，如果验证失败返回null
     */
    public static ISqlCondition validateCondition(ISqlCondition sqlCondition, Map<String, String> field2JdbcColumnMap, Map<String, Object> unmapped) {
        String field = sqlCondition.getField();
        String operator = sqlCondition.getOperator();
        Object value = sqlCondition.getValue();
        if (field == null || field.isEmpty()) {
            return null;
        }
        String jdbcColumn = field2JdbcColumnMap.get(field);
        if (jdbcColumn == null) {
            log.info("condition field [{}] not exist in fieldMap , it will be removed and put into paramMap", field);
            unmapped.putIfAbsent(field, value);
            return null;
        }
        operator = SqlKeyword.replaceOperator(operator);
        if (!SqlKeyword.isNoneArgOperator(operator) && value == null) {
            log.info("condition field [{}] requires value but value is null, it will be removed and put into paramMap", field);
            unmapped.putIfAbsent(field, "");
            return null;
        }
        if (SqlKeyword.isMultiArgOperator(operator)) {
            boolean iterableValue = value instanceof Iterable;
            if (!iterableValue) {
                log.info("condition field [{}] requires collection but value is not iterable, it will be removed and put into paramMap", field);
                unmapped.putIfAbsent(field, value);
                return null;
            }
            Iterable<?> iterable = (Iterable<?>) value;
            if (!iterable.iterator().hasNext()) {
                log.info("condition field [{}] requires collection but value is empty, it will be removed and put into paramMap", field);
                unmapped.putIfAbsent(field, value);
                return null;
            }
        }
        if (SqlKeyword.isLikeOperator(operator) && value instanceof String) {
            if (!value.toString().contains("%")){
                value = "%" + value + "%";    
            }
        }
        return new SqlCondition(jdbcColumn, operator, value);
    }

    public static ISqlSort validateSort(ISqlSort sqlSort, Map<String, String> field2JdbcColumnMap) {
        String jdbcColumn = field2JdbcColumnMap.get(sqlSort.getField());
        if (jdbcColumn==null){
            log.warn("sort field [{}] not exist in fieldMap , it will be removed", sqlSort.getField());
            return null;
        }
        return new SqlSort(jdbcColumn, sqlSort.isDesc());
    }

    /**
     * 包装SQL条件
     *
     * @param sqlHelper     SQL助手
     * @param sqlConditions SQL条件集合
     * @param symbol        连接符号
     */
    public static void warpConditions(AbstractSqlHelper<?, ?> sqlHelper, Collection<ISqlCondition> sqlConditions, String symbol) {
        if (sqlHelper==null || sqlConditions==null || sqlConditions.isEmpty()) {
            return;
        }
        symbol = SqlKeyword.replaceConnector(symbol);
        if (SqlKeyword.AND.keyword.equals(symbol)) {
            sqlHelper.getConditions().addAll(sqlConditions);
            return;
        }
        SqlTree iSqlTrees = new SqlTree(sqlConditions, SqlKeyword.OR.keyword);
        sqlHelper.with(iSqlTrees);
    }
    
    
    public static void wrapSorts(AbstractSqlHelper<?, ?> sqlHelper,Collection<ISqlSort> sqlSorts, Map<String, String> field2JdbcColumnMap) {
        for (ISqlSort sqlSort : sqlSorts) {
            ISqlSort iSqlSort = validateSort(sqlSort, field2JdbcColumnMap);
            if (iSqlSort != null) {
                sqlHelper.getSorts().add(iSqlSort);
            }
        }
    }

    /**
     * 处理SQL助手
     *
     * @param rootHelper 根SQL助手
     * @param <T>        实体类型
     * @return {@link ISqlHelper} 处理后的SQL助手
     * @throws IllegalArgumentException 当无法获取实体类时抛出
     */
    public static <T> ISqlHelper<T> process(ISqlHelper<T> rootHelper) {
        Class<T> entityClass = rootHelper.getEntityClass();
        if (entityClass == null) {
            throw new IllegalArgumentException("can't get entity class from sql helper");
        }
        SqlHelper<T> resultHelper = SqlHelper.of(entityClass);
        Map<String, String> field2JdbcColumnMap = MybatisPlusReflectUtil.field2JdbcColumnMap(entityClass);
        Map<String, Object> unmapped = resultHelper.getUnmapped();
        for (ISqlTree currentHelper : rootHelper) {
            Collection<ISqlCondition> currentHelperConditions = currentHelper.getConditions();
            Iterator<ISqlCondition> conditionIterator = currentHelperConditions.iterator();
            LinkedHashSet<ISqlCondition> validatedConditions = new LinkedHashSet<>(currentHelperConditions.size());
            while (conditionIterator.hasNext()) {
                ISqlCondition sqlCondition = conditionIterator.next();
                ISqlCondition validate = DefaultProcessor.validateCondition(sqlCondition, field2JdbcColumnMap, unmapped);
                if (validate == null) {
                    continue;
                }
                validatedConditions.add(validate);
            }
            DefaultProcessor.warpConditions(resultHelper, validatedConditions, currentHelper.getConnector());
        }
        DefaultProcessor.wrapSorts(resultHelper, rootHelper.getSorts(), field2JdbcColumnMap);
        return resultHelper;
    }
    
}