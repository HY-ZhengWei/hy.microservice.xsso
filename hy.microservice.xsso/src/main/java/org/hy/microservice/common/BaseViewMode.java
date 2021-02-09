package org.hy.microservice.common;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.xml.SerializableDef;

import com.fasterxml.jackson.annotation.JsonFormat;





/**
 * 与页面交互的基础类
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-11-19
 * @version     v1.0
 */
public class BaseViewMode extends SerializableDef
{
    private static final long serialVersionUID = -3998918924300953503L;

    /** 票据号 */
    private String token;
    
    /** 设备号 */
    private String  deviceNo;

    /** 设备类型 */
    private String  deviceType;
    
    /** 业务类型（值内容由业务决定） */
    private String  serviceType;
    
    /** 用户编号 */
    private String  userID;
    
    /** 用户名称 */
    private String  userName;
    
    /** 用户头像 */
    private String  userIcon;
    
    /** 用户类型 */
    private String  userType;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date    createTime;
    
    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date    updateTime;
    
    /** 删除标记。1删除；0未删除 */
    private Integer isDel;
    
    /** 是否显示。1显示；0不显示 */
    private Integer isShow;
    
    /** 审核状态：0：待审核、1：已审核 */
    private String  auditState;

    /** 审核结果，0：不通过、1：通过 */
    private String  auditResult;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    private Date    auditTime;
    
    /** 页码。有效下标从1开始 */
    private Long    pageIndex;

    /** 每页显示数量 */
    private Long    pagePerCount;
    
    /** 总行数 */
    private Long    totalCount;

    
    
    /**
     * 获取：票据号
     */
    public String getToken()
    {
        return token;
    }

    
    /**
     * 设置：票据号
     * 
     * @param token 
     */
    public void setToken(String token)
    {
        this.token = token;
    }


    /**
     * 数量转为短数输出显示
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-19
     * @version     v1.0
     *
     * @param i_Count
     * @return
     */
    protected  String toCountInfo(Long i_Count)
    {
        if ( i_Count == null )
        {
            return "0";
        }
        
        // 1Y
        if ( i_Count.longValue() >= 100000000 )
        {
            return Help.round(i_Count.longValue() / 100000000 ,2) + "Y";
        }
        // 1Y ~ 100W
        else if ( i_Count.longValue() >= 1000000 )
        {
            return Help.round(i_Count.longValue() / 10000 ,1) + "W";
        }
        // 100W ~ 1W
        else if ( i_Count.longValue() >= 10000 )
        {
            return Help.round(i_Count.longValue() / 10000 ,2) + "W";
        }
        // 1W ~ 1K
        else if ( i_Count.longValue() >= 1000 )
        {
            return Help.round(i_Count.longValue() / 1000 ,2) + "K";
        }
        else
        {
            return "" + i_Count.longValue();
        }
    }
    
    
    /**
     * 获取：设备号
     */
    public String getDeviceNo()
    {
        return deviceNo;
    }

    
    /**
     * 获取：设备类型
     */
    public String getDeviceType()
    {
        return deviceType;
    }

    
    /**
     * 获取：业务类型（值内容由业务决定）
     */
    public String getServiceType()
    {
        return serviceType;
    }

    
    /**
     * 获取：用户编号
     */
    public String getUserID()
    {
        return userID;
    }

    
    /**
     * 获取：用户名称
     */
    public String getUserName()
    {
        return userName;
    }

    
    /**
     * 获取：用户头像
     */
    public String getUserIcon()
    {
        return userIcon;
    }

    
    /**
     * 获取：创建时间
     */
    public Date getCreateTime()
    {
        return createTime;
    }

    
    /**
     * 获取：是否显示。1显示；0不显示
     */
    public Integer getIsShow()
    {
        return isShow;
    }

    
    /**
     * 获取：审核状态：0：待审核、1：已审核
     */
    public String getAuditState()
    {
        return auditState;
    }

    
    /**
     * 获取：审核结果，0：不通过、1：通过
     */
    public String getAuditResult()
    {
        return auditResult;
    }

    
    /**
     * 获取：审核时间
     */
    public Date getAuditTime()
    {
        return auditTime;
    }

    
    /**
     * 设置：设备号
     * 
     * @param deviceNo 
     */
    public void setDeviceNo(String deviceNo)
    {
        this.deviceNo = deviceNo;
    }

    
    /**
     * 设置：设备类型
     * 
     * @param deviceType 
     */
    public void setDeviceType(String deviceType)
    {
        this.deviceType = deviceType;
    }

    
    /**
     * 设置：业务类型（值内容由业务决定）
     * 
     * @param serviceType 
     */
    public void setServiceType(String serviceType)
    {
        this.serviceType = serviceType;
    }

    
    /**
     * 设置：用户编号
     * 
     * @param userID 
     */
    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    
    /**
     * 设置：用户名称
     * 
     * @param userName 
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    
    /**
     * 设置：用户头像
     * 
     * @param userIcon 
     */
    public void setUserIcon(String userIcon)
    {
        this.userIcon = userIcon;
    }

    
    /**
     * 设置：创建时间
     * 
     * @param createTime 
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    
    /**
     * 设置：是否显示。1显示；0不显示
     * 
     * @param isShow 
     */
    public void setIsShow(Integer isShow)
    {
        this.isShow = isShow;
    }

    
    /**
     * 设置：审核状态：0：待审核、1：已审核
     * 
     * @param auditState 
     */
    public void setAuditState(String auditState)
    {
        this.auditState = auditState;
    }

    
    /**
     * 设置：审核结果，0：不通过、1：通过
     * 
     * @param auditResult 
     */
    public void setAuditResult(String auditResult)
    {
        this.auditResult = auditResult;
    }

    
    /**
     * 设置：审核时间
     * 
     * @param auditTime 
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    public void setAuditTime(Date auditTime)
    {
        this.auditTime = auditTime;
    }

    
    /**
     * 获取：用户类型
     */
    public String getUserType()
    {
        return userType;
    }

    
    /**
     * 设置：用户类型
     * 
     * @param userType 
     */
    public void setUserType(String userType)
    {
        this.userType = userType;
    }


    /**
     * 获取：开始索引
     */
    public Long getStartIndex()
    {
        if ( this.pageIndex == null || this.pagePerCount == null )
        {
            return null;
        }
        else
        {
            return this.getPagePerCount() * (this.getPageIndex() - 1);
        }
    }


    /**
     * 获取：每页显示数量
     */
    public Long getPagePerCount()
    {
        if ( this.pagePerCount == null )
        {
            return null;
        }
        else if ( this.pagePerCount > 1000L )
        {
            return 1000L;
        }
        else if ( this.pagePerCount <= 0L )
        {
            return 10L;
        }
        else
        {
            return pagePerCount;
        }
    }

    
    /**
     * 设置：每页显示数量
     * 
     * @param pagePerCount 
     */
    public void setPagePerCount(Long pagePerCount)
    {
        this.pagePerCount = pagePerCount;
    }

    
    /**
     * 获取：总行数
     */
    public Long getTotalCount()
    {
        return totalCount;
    }


    /**
     * 设置：总行数
     * 
     * @param totalCount 
     */
    public void setTotalCount(Long totalCount)
    {
        this.totalCount = totalCount;
    }


    /**
     * 获取：修改时间
     */
    public Date getUpdateTime()
    {
        return updateTime;
    }

    
    /**
     * 设置：修改时间
     * 
     * @param updateTime 
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone = "GMT+8")
    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    
    /**
     * 获取：删除标记。1删除；0未删除
     */
    public Integer getIsDel()
    {
        return isDel;
    }

    
    /**
     * 设置：删除标记。1删除；0未删除
     * 
     * @param isDel 
     */
    public void setIsDel(Integer isDel)
    {
        this.isDel = isDel;
    }

    
    /**
     * 获取：页码。有效下标从1开始
     */
    public Long getPageIndex()
    {
        if ( this.pageIndex == null )
        {
            return null;
        }
        else if ( this.pageIndex <= 0 )
        {
            return 1L;
        }
        else
        {
            return pageIndex;
        }
    }

    
    /**
     * 设置：页码。有效下标从1开始
     * 
     * @param pageIndex 
     */
    public void setPageIndex(Long pageIndex)
    {
        this.pageIndex = pageIndex;
    }

}
