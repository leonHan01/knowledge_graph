/**
 *
 */
package com.smart.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author BG204466
 * <p>
 * 获取spring上下文
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {

    /**
     * Spring应用上下文环境
     */
    private static ApplicationContext applicationContext;

    /**
     * 获取对象
     *
     * @param name bean
     * @return Object Object
     * @throws BeansException BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    /**
     * 获取对象
     * <p>
     * bean
     *
     * @return Object Object
     * @throws BeansException BeansException
     */
    public static Object getBean(Class<?> c) throws BeansException {
        return applicationContext.getBean(c);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtil.applicationContext = applicationContext;
    }

}
