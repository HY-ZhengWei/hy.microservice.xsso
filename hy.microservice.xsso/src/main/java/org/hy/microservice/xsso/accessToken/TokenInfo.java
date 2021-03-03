package org.hy.microservice.xsso.accessToken;

import org.hy.microservice.common.BaseViewMode;





/**
 * 票据信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-02-01
 * @version     v1.0
 */
public class TokenInfo extends BaseViewMode
{
    
    private static final long serialVersionUID = 2814465090860934703L;
    
    /** 应用编码 */
    private String  appKey;
    
    /** 时间戳 */
    private Long    timestamp;
    
    /** 签名 */
    private String  signature;
    
    /** 访问Token */
    private String  accessToken;
    
    /** 会话Token */
    private String  sessionToken;
    
    /** 过期时长（单位：秒） */
    private Integer expire;
    
    /** 临时登录Code */
    private String  code;
    
    
    
    /**
     * 获取：应用编码
     */
    public String getAppKey()
    {
        return appKey;
    }

    
    /**
     * 获取：时间戳
     */
    public Long getTimestamp()
    {
        return timestamp;
    }

    
    /**
     * 获取：签名
     */
    public String getSignature()
    {
        return signature;
    }

    
    /**
     * 获取：访问Token
     */
    public String getAccessToken()
    {
        return accessToken;
    }

    
    /**
     * 获取：过期时长（单位：秒）
     */
    public Integer getExpire()
    {
        return expire;
    }

    
    /**
     * 设置：应用编码
     * 
     * @param appKey 
     */
    public void setAppKey(String appKey)
    {
        this.appKey = appKey;
    }

    
    /**
     * 设置：时间戳
     * 
     * @param timestamp 
     */
    public void setTimestamp(Long timestamp)
    {
        this.timestamp = timestamp;
    }

    
    /**
     * 设置：签名
     * 
     * @param signature 
     */
    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    
    /**
     * 设置：访问Token
     * 
     * @param accessToken 
     */
    public void setAccessToken(String accessToken)
    {
        this.accessToken = accessToken;
    }

    
    /**
     * 设置：过期时长（单位：秒）
     * 
     * @param expire 
     */
    public void setExpire(Integer expire)
    {
        this.expire = expire;
    }


    /**
     * 获取：临时登录Code
     */
    public String getCode()
    {
        return code;
    }


    /**
     * 设置：临时登录Code
     * 
     * @param code 
     */
    public void setCode(String code)
    {
        this.code = code;
    }

    
    /**
     * 获取：会话Token
     */
    public String getSessionToken()
    {
        return sessionToken;
    }

    
    /**
     * 设置：会话Token
     * 
     * @param sessionToken 
     */
    public void setSessionToken(String sessionToken)
    {
        this.sessionToken = sessionToken;
    }

}
