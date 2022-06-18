package org.hy.microservice.xsso.report;

import org.hy.microservice.common.BaseViewMode;





/**
 * 票据信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-18
 * @version     v1.0
 */
public class ReportInfo extends BaseViewMode
{
    
    private static final long serialVersionUID = 8788308076115111553L;

    
    /** 本用户类型下的在线用户数据 */
    private Long   onlineUserCount;
    
    /** 所有在线用户数据 */
    private Long   onlineUserAllCount;

    
    
    /**
     * 本用户类型下的在线用户数据
     * 
     * @return
     */
    public Long getOnlineUserCount()
    {
        return onlineUserCount;
    }

    
    /**
     * 本用户类型下的在线用户数据
     * 
     * @param onlineUserCount
     */
    public void setOnlineUserCount(Long onlineUserCount)
    {
        this.onlineUserCount = onlineUserCount;
    }


    /**
     * 所有在线用户数据
     * 
     * @return
     */
    public Long getOnlineUserAllCount()
    {
        return onlineUserAllCount;
    }

    
    /**
     * 所有在线用户数据
     * 
     * @param onlineUserAllCount
     */
    public void setOnlineUserAllCount(Long onlineUserAllCount)
    {
        this.onlineUserAllCount = onlineUserAllCount;
    }
    
}
