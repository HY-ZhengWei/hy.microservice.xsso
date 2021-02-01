package org.hy.microservice.common;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.file.FileHelp;
import org.hy.common.xml.log.Logger;





/**
 * 将Vue整合到Spring Boot项目中，使其即可成为一个整体，也能分隔部署的方案 
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-11-20
 * @version     v1.0
 */
public class VueServlet extends HttpServlet
{
    
    private static final long serialVersionUID = -1077494040114326163L;
    
    private static final Logger $Logger = new Logger(VueServlet.class);
    
    
    
    public VueServlet()
    {
        super();
    }
    
    
    
    public void doGet(HttpServletRequest i_Request, HttpServletResponse i_Response) throws ServletException, IOException 
    {
        String v_BasePath     = i_Request.getScheme()+"://" + i_Request.getServerName() + (i_Request.getServerPort() == 80 ? "" : ":" + i_Request.getServerPort()) + i_Request.getContextPath() + "/";
        String v_RequestURL   = i_Request.getRequestURL().toString();
        String v_ResourceURL  = StringHelp.replaceAll(v_RequestURL ,v_BasePath ,"");
        String v_ResourceFile = Help.getWebHomePath() + v_ResourceURL;
        
        v_RequestURL = v_RequestURL.toLowerCase();
        
        if ( StringHelp.isContains(v_RequestURL ,".css") )
        {
            i_Response.setContentType("text/css;charset=UTF-8");
        }
        else if ( StringHelp.isContains(v_RequestURL ,".js") )
        {
            i_Response.setContentType("application/x-javascript;charset=UTF-8");
        }
        else if ( StringHelp.isContains(v_RequestURL ,".png" ,".jpg" ,".png" ,".gif" ,".bmp") )
        {
            i_Response.setContentType("image/png");
        }
        else if ( StringHelp.isContains(v_RequestURL ,".svg") )
        {
            i_Response.setContentType("text/xml;charset=UTF-8");
        }
        else if ( StringHelp.isContains(v_RequestURL ,".eot") )
        {
            i_Response.setContentType("application/vnd.ms-fontobject");
        }
        else if ( StringHelp.isContains(v_RequestURL ,".ttf") )
        {
            i_Response.setContentType("application/x-font-ttf");
        }
        else if ( StringHelp.isContains(v_RequestURL ,".woff2") )
        {
            i_Response.setContentType("application/x-font-woff2");
        }
        else if ( StringHelp.isContains(v_RequestURL ,".woff") )
        {
            i_Response.setContentType("application/x-font-woff");
        }
        
        i_Response.getWriter().println(this.getResource(v_ResourceFile));
    }
    
    
    
    public void doPost(HttpServletRequest i_Request, HttpServletResponse i_Response) throws ServletException, IOException 
    {
        this.doGet(i_Request ,i_Response);
    }
    
    
    
    /**
     * 获取资源内存
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-11-20
     * @version     v1.0
     *
     * @param i_LocationFullPath
     * @return
     */
    private String getResource(String i_LocationFullPath)
    {
        FileHelp v_FileHelp = new FileHelp();
        
        try
        {
            File v_File = new File(i_LocationFullPath);
            if ( v_File.isFile() )
            {
                return v_FileHelp.getContent(i_LocationFullPath ,"UTF-8");
            }
            else
            {
                return v_FileHelp.getContent(i_LocationFullPath + Help.getSysPathSeparator() + "index.html" ,"UTF-8");
            }
        }
        catch (Exception exce)
        {
            $Logger.error("资源不存在" + i_LocationFullPath ,exce);
        }
        
        return "";
    }
    
}
