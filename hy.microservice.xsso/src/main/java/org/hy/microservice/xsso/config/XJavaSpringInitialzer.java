package org.hy.microservice.xsso.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;





/**
 * SpringBoot启动时，拉起XJava的初始化
 *
 * @author      ZhengWei(HY)、马龙
 * @createDate  2020-11-19
 * @version     v1.0
 */
public class XJavaSpringInitialzer implements ApplicationContextInitializer<ConfigurableApplicationContext> 
{
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) 
    {
        new XJavaInit();
    }
}
