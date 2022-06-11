package org.hy.microservice.xsso.user;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.xml.SerializableDef;





/**
 * 用户单点登录信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-12-30
 * @version     v1.0
 */
public class UserSSO extends SerializableDef
{

    private static final long serialVersionUID = -6709011909036487332L;
    
    /** 用户主键ID */
    private String id;
    
    /** 应用appKey */
    private String appKey;
    
    /** 用户在第三方用户中心的标识 */
    private String userId;
    
    /** 用户在第三方用户中心的工号 */
    private String userNo;
    
    /** 用户在第三方系统的编码 */
    private String userCode;
    
    /** 微信应用编号 */
    private String appID;
    
    /** 微信用户应用ID */
    private String openID;
        
    /** 微信用户统一ID */
    private String unionID;
    
    /** 用户在第三方系统的手机号 */
    private String mobile;
    
    /** 用户在第三方系统的手机号 */
    private String phone;
    
    /** 用户在第三方系统的邮箱 */
    private String email;
    
    /** 用户在第三方系统的用户名 */
    private String userName;
    
    /** 用户在第三方系统的昵称 */
    private String nickname;
    
    /** 用户在第三方系统的用户类型 */
    private String userType;
    
    /** 用户在第三方系统的用户级别 */
    private String userLevel;
    
    /** 用户在第三方系统的头像地址 */
    private String userLogoUrl;
    
    /** 用户在第三方系统的用户来源 */
    private String userSource;
    
    /** 用户在第三方系统的登陆账号 */
    private String loginAccount;
    
    /** 用户在第三方系统的组织编码 */
    private String orgCode;
    
    /** 用户在第三方系统的组织名称 */
    private String orgName;
    
    /** 用户性别 */
    private String sex;
    
    /** 全局会话票据 */
    private String usid;
    
    /** 会话Cookie编码 */
    private String ucid;
    
    /** 会话ID */
    private String sessionID;
    
    /** 登录时间 */
    private Date loginTime;
    
    /** 活动时间 */
    private Date aliveTime;
    
    /** 附加用户数据 */
    private Map<String ,String> datas;

    
    
    /**
     * 获取：微信应用编号
     */
    public String getAppID()
    {
        return this.appID;
    }

    
    /**
     * 设置：微信应用编号
     * 
     * @param i_AppID
     */
    public void setAppID(String i_AppID)
    {
        this.appID = i_AppID;
    }
    
    
    /**
     * 获取：用户主键ID
     */
    public String getId()
    {
        return id;
    }


    /**
     * 设置：用户主键ID
     * 
     * @param id
     */
    public void setId(String id)
    {
        this.id = id;
    }
    
    
    /**
     * 获取：用户在第三方用户中心的标识
     */
    public String getUserId()
    {
        return userId;
    }

    
    /**
     * 获取：用户在第三方系统的手机号
     */
    public String getMobile()
    {
        return mobile;
    }

    
    /**
     * 获取：用户在第三方系统的手机号
     */
    public String getPhone()
    {
        return phone;
    }

    
    /**
     * 获取：用户在第三方系统的邮箱
     */
    public String getEmail()
    {
        return email;
    }

    
    /**
     * 获取：用户在第三方系统的用户名
     */
    public String getUserName()
    {
        return userName;
    }

    
    /**
     * 获取：用户在第三方系统的编码
     */
    public String getUserCode()
    {
        return userCode;
    }

    
    /**
     * 获取：用户在第三方系统的用户类型
     */
    public String getUserType()
    {
        return userType;
    }

    
    /**
     * 获取：用户在第三方系统的登陆账号
     */
    public String getLoginAccount()
    {
        return loginAccount;
    }

    
    /**
     * 获取：用户性别
     */
    public String getSex()
    {
        return sex;
    }

    
    /**
     * 获取：会话ID
     */
    public String getSessionID()
    {
        return sessionID;
    }

    
    /**
     * 获取：附加用户数据
     */
    public Map<String ,String> getDatas()
    {
        return datas;
    }

    
    /**
     * 设置：用户在第三方用户中心的标识
     * 
     * @param userId
     */
    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    
    /**
     * 设置：用户在第三方系统的手机号
     * 
     * @param mobile
     */
    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    
    /**
     * 设置：用户在第三方系统的手机号
     * 
     * @param phone
     */
    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    
    /**
     * 设置：用户在第三方系统的邮箱
     * 
     * @param email
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    
    /**
     * 设置：用户在第三方系统的用户名
     * 
     * @param userName
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    
    /**
     * 设置：用户在第三方系统的编码
     * 
     * @param userCode
     */
    public void setUserCode(String userCode)
    {
        this.userCode = userCode;
    }

    
    /**
     * 设置：用户在第三方系统的用户类型
     * 
     * @param userType
     */
    public void setUserType(String userType)
    {
        this.userType = userType;
    }

    
    /**
     * 设置：用户在第三方系统的登陆账号
     * 
     * @param loginAccount
     */
    public void setLoginAccount(String loginAccount)
    {
        this.loginAccount = loginAccount;
    }

    
    /**
     * 设置：用户性别
     * 
     * @param sex
     */
    public void setSex(String sex)
    {
        this.sex = sex;
    }

    
    /**
     * 设置：会话ID
     * 
     * @param sessionID
     */
    public void setSessionID(String sessionID)
    {
        this.sessionID = sessionID;
    }

    
    /**
     * 设置：附加用户数据
     * 
     * @param datas
     */
    public void setDatas(Map<String ,String> datas)
    {
        this.datas = datas;
    }

    
    /**
     * 获取：用户在第三方系统的组织编码
     */
    public String getOrgCode()
    {
        return orgCode;
    }

    
    /**
     * 获取：用户在第三方系统的组织名称
     */
    public String getOrgName()
    {
        return orgName;
    }

    
    /**
     * 设置：用户在第三方系统的组织编码
     * 
     * @param orgCode
     */
    public void setOrgCode(String orgCode)
    {
        this.orgCode = orgCode;
    }

    
    /**
     * 设置：用户在第三方系统的组织名称
     * 
     * @param orgName
     */
    public void setOrgName(String orgName)
    {
        this.orgName = orgName;
    }

    
    /**
     * 获取：应用appKey
     */
    public String getAppKey()
    {
        return appKey;
    }

    
    /**
     * 设置：应用appKey
     * 
     * @param appKey
     */
    public void setAppKey(String appKey)
    {
        this.appKey = appKey;
    }

    
    /**
     * 获取：会话票据
     */
    public String getUsid()
    {
        return usid;
    }

    
    /**
     * 设置：会话票据
     * 
     * @param usid
     */
    public void setUsid(String usid)
    {
        this.usid = usid;
    }

    
    /**
     * 获取：会话Cookie编码
     */
    public String getUcid()
    {
        return ucid;
    }


    /**
     * 设置：会话Cookie编码
     * 
     * @param ucid
     */
    public void setUcid(String ucid)
    {
        this.ucid = ucid;
    }

    
    /**
     * 获取：用户在第三方用户中心的工号
     */
    public String getUserNo()
    {
        return userNo;
    }

    
    /**
     * 设置：用户在第三方用户中心的工号
     * 
     * @param userNo
     */
    public void setUserNo(String userNo)
    {
        this.userNo = userNo;
    }


    /**
     * 获取：用户在第三方系统的用户级别
     */
    public String getUserLevel()
    {
        return userLevel;
    }

    
    /**
     * 设置：用户在第三方系统的用户级别
     * 
     * @param userLevel
     */
    public void setUserLevel(String userLevel)
    {
        this.userLevel = userLevel;
    }

    
    /**
     * 获取：用户在第三方系统的用户来源
     */
    public String getUserSource()
    {
        return userSource;
    }

    
    /**
     * 设置：用户在第三方系统的用户来源
     * 
     * @param userSource
     */
    public void setUserSource(String userSource)
    {
        this.userSource = userSource;
    }


    /**
     * 获取：用户在第三方系统的昵称
     */
    public String getNickname()
    {
        return nickname;
    }

    
    /**
     * 设置：用户在第三方系统的昵称
     * 
     * @param nickname
     */
    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }


    /**
     * 获取：微信用户应用ID
     */
    public String getOpenID()
    {
        return openID;
    }

    
    /**
     * 获取：微信用户统一ID
     */
    public String getUnionID()
    {
        return unionID;
    }

    
    /**
     * 设置：微信用户应用ID
     * 
     * @param openID
     */
    public void setOpenID(String openID)
    {
        this.openID = openID;
    }


    /**
     * 设置：微信用户统一ID
     * 
     * @param unionID
     */
    public void setUnionID(String unionID)
    {
        this.unionID = unionID;
    }

    
    /**
     * 获取：用户在第三方系统的头像地址
     */
    public String getUserLogoUrl()
    {
        return userLogoUrl;
    }

    
    /**
     * 设置：用户在第三方系统的头像地址
     * 
     * @param userLogoUrl
     */
    public void setUserLogoUrl(String userLogoUrl)
    {
        this.userLogoUrl = userLogoUrl;
    }


    /**
     * 获取：用户在第三方系统的头像地址
     */
    public Date getLoginTime()
    {
        return loginTime;
    }


    /**
     * 设置：用户在第三方系统的头像地址
     * 
     * @param userLogoUrl
     */
    public void setLoginTime(Date loginTime)
    {
        this.loginTime = loginTime;
    }


    /**
     * 获取：活动时间
     */
    public Date getAliveTime()
    {
        return aliveTime;
    }


    /**
     * 设置：活动时间
     * 
     * @param userLogoUrl
     */
    public void setAliveTime(Date aliveTime)
    {
        this.aliveTime = aliveTime;
    }


    @Override
    public String toString()
    {
        StringBuilder v_Buffer = new StringBuilder();
        
        if ( this.aliveTime != null )
        {
            v_Buffer.append(this.aliveTime.getFull()).append(" ");
        }
        else if ( this.loginTime != null )
        {
            v_Buffer.append(this.loginTime.getFull()).append(" ");
        }
        
        v_Buffer.append(this.userId).append(" ").append(this.userName).append("(").append(this.userNo).append(")");
        
        return v_Buffer.toString();
    }
    
}
