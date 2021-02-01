package org.hy.microservice.xsso.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;





/**
 * WebConfig：服务Web配置
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-11-20
 * @version     v1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer 
{

    @Resource
    private CommonInterceptor commonInterceptor;
    
    
    
    /**
     * 添加拦截器
     *
     * @author      ZhengWei(HY)
     * @createDate  2020-11-20
     * @version     v1.0
     *
     * @param registry
     *
     * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
     */
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(commonInterceptor).addPathPatterns("/xsso/**");
    }



    /**
     * 添加静态资源
     *
     * @author      ZhengWei(HY)
     * @createDate  2020-11-20
     * @version     v1.0
     *
     * @param i_Registry
     *
     * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry)
     */
    public void addResourceHandlers(ResourceHandlerRegistry i_Registry)
    {
        // 配置server虚拟路径，handler为前台访问的目录，locations为files相对应的本地路径
        // i_Registry.addResourceHandler("/ms/**").addResourceLocations("file:./ms/" ,"file:/ms/" ,"/ms/" ,"classpath:/ms/");
        WebMvcConfigurer.super.addResourceHandlers(i_Registry);
    }

}
