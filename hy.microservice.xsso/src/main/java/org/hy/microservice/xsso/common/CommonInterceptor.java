package org.hy.microservice.xsso.common;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.app.Param;
import org.hy.common.xml.XJava;
import org.hy.common.xml.log.Logger;
import org.hy.microservice.xsso.user.UserSSO;
import org.hy.microservice.xsso.user.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;





/**
 * 通用spring-MVC拦截器
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-03-20
 * @version     v1.0
 *              v2.0  2022-06-08  1. 添加：脚本攻击检查
 *                                2. 添加：CORS跨域检查
 */
@Component
public class CommonInterceptor implements HandlerInterceptor
{
    
    private static final Logger $Logger = new Logger(CommonInterceptor.class);
    
    

    @Override
    public boolean preHandle(HttpServletRequest i_Request ,HttpServletResponse i_Response ,Object handler) throws Exception
    {
        if ( !this.attackJSCheck(i_Request ,i_Response) )
        {
            return false;
        }
        
        if ( !Help.isNull(i_Request.getCookies()) )
        {
            int v_CSize = i_Request.getCookies().length;
            
            try
            {
                for (int i=0; i<v_CSize; i++)
                {
                    i_Request.getCookies()[i].setHttpOnly(true);
                    // i_Request.getCookies()[i].setSecure(true);
                    // 要小心添加上行代码。可能会出现每次请求SesssionID均不一样的问题
                }
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
        }
        
        this.corsCheck(i_Request ,i_Response);
        
        return true;
    }
    
    
    
    /**
     * 脚本攻击：检查
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-08
     * @version     v1.0
     * 
     * @param i_Request
     * @param i_Response
     * @return            true:检查通过，否则为攻击行为
     */
    private boolean attackJSCheck(HttpServletRequest i_Request ,HttpServletResponse i_Response)
    {
        if ( !Help.isNull(i_Request.getParameterMap()) )
        {
            for (Map.Entry<String, String[]> v_Item : i_Request.getParameterMap().entrySet())
            {
                if ( StringHelp.isContains(v_Item.getKey() ,"<" ,">" ,"script" ,"alert" ,"console") )
                {
                    attackJSSendAlarm(i_Request ,StringHelp.replaceAll(v_Item.getKey() ,new String[]{"<" ,">"} ,new String[] {"@"}));
                    return false;
                }
                
                if ( !Help.isNull(v_Item.getValue()) )
                {
                    for (String v_Value : v_Item.getValue())
                    {
                        if ( StringHelp.isContains(v_Value ,"<" ,">" ,"script" ,"alert" ,"console") )
                        {
                            attackJSSendAlarm(i_Request ,StringHelp.replaceAll(v_Value ,new String[]{"<" ,">"} ,new String[] {"@"}));
                            return false;
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    
    
    /**
     * 脚本攻击：发送警告消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-07
     * @version     v1.0
     * 
     * @param i_Request
     * @param i_ParamInfo
     */
    private void attackJSSendAlarm(HttpServletRequest i_Request ,String i_ParamInfo)
    {
        UserSSO v_LoginUser = ((UserService)XJava.getObject("UserService")).sessionGetUser(i_Request.getSession());
        String  v_UserName  = "匿名用户";
        String  v_UserCode  = "未登录";
        
        if ( v_LoginUser != null )
        {
            v_UserName = Help.NVL(v_LoginUser.getUserName());
            v_UserCode = Help.NVL(v_LoginUser.getUserCode());
        }
        
        String v_Msg = v_UserName + "(" + v_UserCode + ") 请求页面：" + i_Request.getRequestURL().toString() + ":" + Help.NVL(i_Request.getQueryString()) + ":" + i_ParamInfo + "。" + Date.getNowTime().getFull();
        $Logger.warn("单点登录：注入攻击：" + v_Msg);
    }
    
    
    
    /**
     * 跨域访问：发送警告消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-07
     * @version     v1.0
     * 
     * @param i_Request
     * @param i_ParamInfo
     */
    private void corsSendAlarm(HttpServletRequest i_Request ,String i_ParamInfo)
    {
        UserSSO v_LoginUser = ((UserService)XJava.getObject("UserService")).sessionGetUser(i_Request.getSession());
        String  v_UserName  = "匿名用户";
        String  v_UserCode  = "未登录";
        
        if ( v_LoginUser != null )
        {
            v_UserName = v_LoginUser.getUserName();
            v_UserCode = v_LoginUser.getUserCode();
        }
        
        String v_Msg = v_UserName + "(" + v_UserCode + ") 请求页面：" + i_Request.getRequestURL().toString() + ":" + Help.NVL(i_Request.getQueryString()) + ":" + i_ParamInfo + "。" + Date.getNowTime().getFull();
        $Logger.warn("单点登录：非法跨域：" + v_Msg);
    }
    
    
    
    /**
     * 跨域访问：CORS跨域资源允许策略
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-07
     * @version     v1.0
     * 
     * @param i_Request
     * @param i_Response
     */
    private void corsCheck(HttpServletRequest i_Request ,HttpServletResponse i_Response)
    {
        try
        {
            String v_OriginHead = i_Request.getHeader("Origin");
//            if ( Help.isNull(v_OriginHead) )
//            {
//                if ( i_Request.getProtocol().toLowerCase().startsWith("https") )
//                {
//                    v_OriginHead = "https://";
//                }
//                else
//                {
//                    v_OriginHead = "http://";
//                }
//
//                v_OriginHead += i_Request.getServerName();
//
//                if ( i_Request.getServerPort() != 80 )
//                {
//                    v_OriginHead += ":" + i_Request.getServerPort();
//                }
//            }
            
            if ( !Help.isNull(v_OriginHead) )
            {
                String v_OriginHeadLC = StringHelp.replaceAll(v_OriginHead.toLowerCase().trim() ,new String[] {"https://" ,"http://"} ,StringHelp.$ReplaceNil);
                if ( this.corsIsAllowDNS(v_OriginHeadLC) || this.corsIsAllowIP(v_OriginHeadLC) )
                {
                    i_Response.setHeader("Access-Control-Allow-Origin"      ,v_OriginHead);                   // 支持跨域请求
                    i_Response.setHeader("Access-Control-Allow-Credentials" ,"true");                         // 支持cookie跨域
                    i_Response.setHeader("Access-Control-Allow-Methods"     ,"*");
                    i_Response.setHeader("Access-Control-Allow-Headers"     ,"Authorization,Origin, X-Requested-With, Content-Type, Accept,Access-Token");//Origin, X-Requested-With, Content-Type, Accept,Access-Token
                }
                else
                {
                    corsSendAlarm(i_Request ,v_OriginHead);
                }
            }
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
    }
    
    
    
    /**
     * 跨域访问：是否为允许的域名
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-07
     * @version     v1.0
     * 
     * @param i_OriginHeadLC
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean corsIsAllowDNS(String i_OriginHeadLC)
    {
        Map<String ,Param> v_DNSMap = (Map<String ,Param>)XJava.getObject("MS_XSSO_DNSConfigs");
        return v_DNSMap.containsKey(i_OriginHeadLC);
    }
    
    
    
    /**
     * 跨域访问：是否为允许的IP段
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-07
     * @version     v1.0
     * 
     * @param i_OriginHeadLC
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean corsIsAllowIP(String i_OriginHeadLC)
    {
        List<Param> v_DNSIPList = (List<Param>)XJava.getObject("MS_XSSO_DNSIPConfigs");
        
        for (Param v_Item : v_DNSIPList)
        {
            if ( i_OriginHeadLC.startsWith(v_Item.getName()) )
            {
                return true;
            }
        }
        
        return false;
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