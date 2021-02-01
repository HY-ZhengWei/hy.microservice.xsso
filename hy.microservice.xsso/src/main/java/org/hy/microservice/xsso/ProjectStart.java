package org.hy.microservice.xsso;

import org.hy.common.xml.plugins.XJavaSpringAnnotationConfigServletWebServerApplicationContext;
import org.hy.common.xml.plugins.analyse.AnalyseObjectServlet;
import org.hy.common.xml.plugins.analyse.AnalyseServerServlet;
import org.hy.common.xml.plugins.analyse.AnalysesServlet;
import org.hy.microservice.common.VueServlet;
import org.hy.microservice.xsso.config.XJavaSpringInitialzer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.WebApplicationContext;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;





/**
 * 微服务的启动类
 *
 * @author      ZhengWei(HY)、马龙
 * @createDate  2020-11-19
 * @version     v1.0
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class ProjectStart extends SpringBootServletInitializer
{
    
    @SuppressWarnings("unused")
    public static void main(String[] args) 
    {
        SpringApplication v_SpringApp = new SpringApplication(ProjectStart.class);
        v_SpringApp.addInitializers(new XJavaSpringInitialzer());
        v_SpringApp.setApplicationContextClass(XJavaSpringAnnotationConfigServletWebServerApplicationContext.class);
        ConfigurableApplicationContext v_CAC = v_SpringApp.run(args);
    }
    
    
    
    /**
     * Tomcat 启动Spring Boot
     *
     * @author      ZhengWei(HY)
     * @createDate  2019-01-13
     * @version     v1.0
     *
     * @param i_Application
     * @return
     *
     * @see org.springframework.boot.web.servlet.support.SpringBootServletInitializer#run(org.springframework.boot.SpringApplication)
     */
    protected WebApplicationContext run(SpringApplication i_Application) 
    {
        i_Application.addInitializers(new XJavaSpringInitialzer());
        i_Application.setApplicationContextClass(XJavaSpringAnnotationConfigServletWebServerApplicationContext.class);
        
        return (WebApplicationContext) i_Application.run();
    }
    
    
    
    /**
     * 注册Vue独立处理机制
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-11-20
     * @version     v1.0
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean<VueServlet> vueServlet()
    {
        return new ServletRegistrationBean<VueServlet>(new VueServlet() ,"/ms/*");
    }
    
    
    
    /**
     * 注册分析中心
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-12-24
     * @version     v1.0
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean<AnalysesServlet> analysesServlet()
    {
        return new ServletRegistrationBean<AnalysesServlet>(new AnalysesServlet() ,"/analyses/*");
    }
    
    
    
    /**
     * 注册数据库性能分析
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-12-24
     * @version     v1.0
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean<AnalyseServerServlet> analyseServerServlet()
    {
        return new ServletRegistrationBean<AnalyseServerServlet>(new AnalyseServerServlet() ,"/analyses/analyseDB");
    }
    
    
    
    /**
     * 注册Java对象池分析
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-12-24
     * @version     v1.0
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean<AnalyseObjectServlet> analyseObjectServlet()
    {
        return new ServletRegistrationBean<AnalyseObjectServlet>(new AnalyseObjectServlet() ,"/analyses/analyseObject");
    }

}
