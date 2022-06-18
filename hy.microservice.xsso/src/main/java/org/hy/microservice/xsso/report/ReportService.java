package org.hy.microservice.xsso.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;
import org.hy.microservice.xsso.user.UserSSO;





/**
 * 用户业务
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-18
 * @version     v1.0
 */
@Xjava
public class ReportService
{
    
    /**
     * 获取在线用户数量
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-18
     * @version     v1.0
     * 
     * @param i_OnlineMaxTimeLen  按活动时间计算，取多少时间范围内的在线用户（单位：毫秒）
     * @return
     */
    public List<ReportInfo> reportOnlineUsers(Long i_OnlineMaxTimeLen)
    {
        List<ReportInfo>               v_Reports        = null;
        Map<String ,ReportInfo>        v_ReportMap      = new HashMap<String ,ReportInfo>();
        Set<Map.Entry<String, Object>> v_SessionSet     = XJava.getSessionMap().entrySet();
        long                           v_AllOnlineCount = 0L;
        long                           v_Now            = Date.getNowTime().getTime();
        
        for (Map.Entry<String, Object> v_Item : v_SessionSet)
        {
            if ( v_Item.getValue() == null )
            {
                continue;
            }
            
            if ( v_Item.getValue() instanceof UserSSO )
            {
                UserSSO v_User = (UserSSO)v_Item.getValue();
                if ( i_OnlineMaxTimeLen != null )
                {
                    Date v_AliveTime = Help.NVL(v_User.getAliveTime() ,v_User.getLoginTime());
                    if ( v_AliveTime != null )
                    {
                        if ( v_Now - v_AliveTime.getTime() < i_OnlineMaxTimeLen )
                        {
                            continue;
                        }
                    }
                }
                
                String     v_UserType = Help.NVL(v_User.getUserType() ,"-");
                ReportInfo v_Report   = v_ReportMap.get(v_UserType);
                
                if ( v_Report == null )
                {
                    v_Report = new ReportInfo();
                    v_Report.setUserType(v_UserType);
                    v_ReportMap.put(v_UserType ,v_Report);
                    v_Report.setOnlineUserCount(0L);
                }
                
                v_Report.setOnlineUserCount(v_Report.getOnlineUserCount() + 1L);
                v_AllOnlineCount++;
            }
        }
        
        v_Reports = Help.toList(v_ReportMap);
        Help.toSort(v_Reports ,"onlineUserCount DESC");
        Help.setValues(v_Reports ,"onlineUserAllCount" ,v_AllOnlineCount);
        
        return v_Reports;
    }
    
}
