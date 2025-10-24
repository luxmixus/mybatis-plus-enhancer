package io.github.luxmixus.mybatisplus.enhancer.core;

import com.baomidou.mybatisplus.extension.service.IService;
import io.github.luxmixus.mybatisplus.enhancer.util.CastUtil;
import io.github.luxmixus.mybatisplus.enhancer.util.ExcelUtil;
import io.github.luxmixus.mybatisplus.enhancer.util.ReflectUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 扩展Excel接口
 * <p>
 * 提供Excel导入导出功能，支持模板导出、数据导入和数据导出
 *
 * @author luxmixus
 */
@SuppressWarnings("unused")
public interface EnhancedExcel {

    /**
     * 导出Excel模板
     *
     * @param os    输出流
     * @param clazz 数据类
     */
    default void excelTemplate(OutputStream os, Class<?> clazz) {
        ExcelUtil.write(os, clazz, Collections.emptyList());
    }

    /**
     * 导入Excel数据
     *
     * @param is    输入流
     * @param clazz 数据类
     * @return int 导入数据条数
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default int excelImport(InputStream is, Class<?> clazz) {
        List<?> dataList = ExcelUtil.read(is, clazz);
        if (dataList == null || dataList.isEmpty()) return 0;
        IService iService = CastUtil.cast(this, IService.class);
        List<?> entityList = dataList.stream()
                .map(e -> ReflectUtil.toTarget(e, iService.getEntityClass()))
                .collect(Collectors.toList());
        iService.saveBatch(entityList);
        return entityList.size();
    }

    /**
     * 导出Excel数据
     *
     * @param param         查询参数
     * @param os            输出流
     * @param clazz         数据类
     * @param includeFields 包含的字段
     */
    default void excelExport(Object param, OutputStream os, Class<?> clazz, String... includeFields) {
        excelExport(param, os, clazz, 1L, -1L, includeFields);
    }

    /**
     * 导出Excel数据（分页）
     *
     * @param param         查询参数
     * @param os            输出流
     * @param clazz         数据类
     * @param current       当前页
     * @param size          每页大小
     * @param includeFields 包含的字段
     */
    @SuppressWarnings("rawtypes")
    default void excelExport(Object param, OutputStream os, Class<?> clazz, Long current, Long size, String... includeFields) {
        EnhancedQuery enhancedQuery = CastUtil.cast(this, EnhancedQuery.class);
        List<?> voList = enhancedQuery.voPage(param, current, size).getRecords();
        ExcelUtil.write(os, clazz, voList, includeFields);
    }

}