package io.github.luxmixus.mybatisplus.enhancer.config;

import io.github.luxmixus.mybatisplus.enhancer.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * 自动配置类
 * <p>
 * 使用 ApplicationRunner 确保在所有Bean初始化完成后再执行SQL片段的配置。
 * 使用 @ConditionalOnBean 确保仅在存在SqlSessionFactory Bean时才激活此配置。
 *
 * @author luxmixus
 */
@Slf4j
@AutoConfiguration
@ConditionalOnBean(SqlSessionFactory.class)
public class EnhancerAutoConfiguration implements ApplicationRunner {

    private final ApplicationContext applicationContext;

    public EnhancerAutoConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String, SqlSessionFactory> sqlSessionFactoryMap = applicationContext.getBeansOfType(SqlSessionFactory.class);

        log.info("Found {} SqlSessionFactory bean(s), starting to configure EnhancedMapper sqlFragments...", sqlSessionFactoryMap.size());

        for (Map.Entry<String, SqlSessionFactory> entry : sqlSessionFactoryMap.entrySet()) {
            String beanName = entry.getKey();
            SqlSessionFactory sqlSessionFactory = entry.getValue();
            boolean success = MapperUtil.initSqlFragment(sqlSessionFactory);
            if (success) {
                log.debug("EnhancedMapper sqlFragments configured for SqlSessionFactory bean: {}", beanName);
            } else {
                log.error("EnhancedMapper sqlFragments configuration failed for SqlSessionFactory bean: {}, dynamic sql may not work", beanName);
            }
        }
    }
}