package org.hy.microservice.common;

import org.hy.common.Date;
import org.hy.common.xml.SerializableDef;





/**
 * 响应的基础类
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-05-06
 * @version     v1.0
 */
public class BaseResp extends SerializableDef
{
    
    private static final long serialVersionUID = 8010547387071937940L;

    public  static final String $Succeed = "200";
    

    /** 响应代码 */
    protected String code;
    
    /** 响应消息 */
    protected String message;
    
    /** 响应时间 */
    protected Date   respTime;
    
    
    
    public BaseResp()
    {
        this.code    = $Succeed;
        this.message = "成功";
    }
    
    
    /**
     * 获取：响应代码
     */
    public String getCode()
    {
        return code;
    }

    
    /**
     * 获取：响应消息
     */
    public String getMessage()
    {
        return message;
    }

    
    /**
     * 设置：响应代码
     * 
     * @param code
     */
    public BaseResp setCode(String code)
    {
        this.code = code;
        return this;
    }

    
    /**
     * 设置：响应消息
     * 
     * @param message
     */
    public BaseResp setMessage(String message)
    {
        this.message = message;
        return this;
    }


    /**
     * 获取：响应时间
     * 
     * @return
     */
    public Date getRespTime()
    {
        return respTime;
    }


    /**
     * 设置：响应时间
     * 
     * @return
     */
    public BaseResp setRespTime(Date respTime)
    {
        this.respTime = respTime;
        return this;
    }
    
}
