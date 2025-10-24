package io.github.luxmixus.mybatisplus.enhancer.util;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.luxmixus.mybatisplus.enhancer.core.EnhancedQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Mapper工具类
 * <p>
 * 提供Mapper相关工具方法，包括SQL片段初始化和Mapper内容生成
 *
 * @author luxmixus
 */
@Slf4j
public abstract class MapperUtil {

    /**
     * 初始化SQL片段
     *
     * @param sqlSessionFactory SQL会话工厂
     * @return boolean 初始化是否成功
     */
    public static boolean initSqlFragment(SqlSessionFactory sqlSessionFactory) {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        String resource = "luxmixus/mapper/EnhancedMapper.xml";
        try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
            XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
            mapperBuilder.parse();
            return true;
        } catch (IOException e) {
            log.error("error creating EnhancedMapper sqlFragments", e);
            return false;
        }
    }

    /**
     * 获取Mapper内容
     *
     * @param enhancedQueryClass 扩展查询类
     * @return {@link String} Mapper内容
     * @throws IllegalArgumentException 当无法解析实体信息时抛出
     */
    @SuppressWarnings("unchecked")
    public static <T> String getMapperContent(Class<? extends EnhancedQuery<?>> enhancedQueryClass) {
        Class<?> voClass = MybatisPlusReflectUtil.resolveTypeArguments(enhancedQueryClass, EnhancedQuery.class)[0];
        Class<?>[] mapperClasses = MybatisPlusReflectUtil.resolveTypeArguments(enhancedQueryClass, BaseMapper.class);
        Class<?>[] serviceClasses = MybatisPlusReflectUtil.resolveTypeArguments(enhancedQueryClass, IService.class);
        Class<T> entityClass = null;
        if (mapperClasses != null && mapperClasses.length > 0) {
            entityClass = (Class<T>) mapperClasses[0];
        } else if (serviceClasses != null && serviceClasses.length > 0) {
            entityClass = (Class<T>) serviceClasses[0];
        } else {
            throw new IllegalArgumentException("no base entity info in " + enhancedQueryClass.getName());
        }
        return getMapperContent(entityClass, voClass);
    }

    /**
     * 获取Mapper内容
     *
     * @param entityClass 实体类
     * @param voClass     VO类
     * @param <T>         实体类型
     * @return {@link String} Mapper内容
     */
    public static <T> String getMapperContent(Class<T> entityClass, Class<?> voClass) {
        return "    <select id=\"voSelectByXml\" resultType=\"" + voClass.getName() + "\">\n" +
                getSqlContent(entityClass) +
                "    </select>"
                ;
    }

    /**
     * 获取SQL内容
     *
     * @param entityClass 实体类
     * @param <T>         实体类型
     * @return {@link String} SQL内容
     */
    public static <T> String getSqlContent(Class<T> entityClass) {
        String tableName;
        TableName annotation = entityClass.getAnnotation(TableName.class);
        if (annotation != null && !annotation.value().isEmpty()) {
            tableName = annotation.value();
        } else {
            tableName = entityClass.getName();
        }
        return getSqlContent(tableName);
    }

    /**
     * 获取SQL内容
     *
     * @param tableName 表名
     * @return {@link String} SQL内容
     */
    public static String getSqlContent(String tableName) {
        return "        SELECT\n" +
                "        a.*\n" +
                "        FROM\n" +
                "        " + tableName + " a\n" +
                "        <where>\n" +
                "            <include refid=\"io.github.luxmixus.mybatisplus.enhancer.EnhancedMapper.queryFragment\"/>\n" +
                "        </where>\n" +
                "        <trim prefix=\"ORDER BY\" prefixOverrides=\",\">\n" +
                "            <include refid=\"io.github.luxmixus.mybatisplus.enhancer.EnhancedMapper.sortFragment\"/>\n" +
                "        </trim>\n"
                ;
    }


}