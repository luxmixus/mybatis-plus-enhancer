package io.github.luxmixus.mybatisplus.enhancer.exception;

/**
 * 条件映射异常
 * <p>
 * 当SQL条件映射过程中出现错误时抛出该异常，例如参数格式不正确或无法映射到数据库字段时
 *
 * @author luxmixus
 */
public class ParamMappingException extends RuntimeException {

    /**
     * 构造一个新的条件映射异常
     *
     * @param message 异常消息模板
     * @param args    消息参数
     */
    public ParamMappingException(String message, Object... args) {
        super(String.format(message, args));
    }

}