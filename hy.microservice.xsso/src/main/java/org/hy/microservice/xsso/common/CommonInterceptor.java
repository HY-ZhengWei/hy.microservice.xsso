package org.hy.microservice.xsso.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hy.common.Help;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;





/**
 * 通用spring-MVC拦截器
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-03-20
 * @version     v1.0
 */
@Component
public class CommonInterceptor implements HandlerInterceptor 
{

    @Override
    public boolean preHandle(HttpServletRequest i_Request ,HttpServletResponse i_Response ,Object handler) throws Exception
    {
        if ( !Help.isNull(i_Request.getCookies()) )
        {
            int v_CSize = i_Request.getCookies().length;
            
            try 
            {
                for (int i=0; i<v_CSize; i++)
                {
                    i_Request.getCookies()[i].setHttpOnly(true);
                    i_Request.getCookies()[i].setSecure(true);
                }
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
        }
        
        i_Response.setHeader("Access-Control-Allow-Origin"      ,i_Request.getHeader("Origin"));  // 支持跨域请求
        i_Response.setHeader("Access-Control-Allow-Credentials" ,"true");                         // 支持cookie跨域
        i_Response.setHeader("Access-Control-Allow-Methods"     ,"*");
        i_Response.setHeader("Access-Control-Allow-Headers"     ,"Authorization,Origin, X-Requested-With, Content-Type, Accept,Access-Token");//Origin, X-Requested-With, Content-Type, Accept,Access-Token
        
        return true;
    }



    @Override
    public void postHandle(HttpServletRequest request ,HttpServletResponse response ,Object handler ,ModelAndView modelAndView) throws Exception
    {
    }



    @Override
    public void afterCompletion(HttpServletRequest request ,HttpServletResponse response ,Object handler ,Exception ex) throws Exception
    {
    }

}