package org.hy.microservice.xsso.user;

import javax.servlet.http.HttpSession;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;
import org.hy.microservice.common.user.UserSSO;
import org.hy.microservice.xsso.cluster.ClusterService;





/**
 * 用户业务
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-02-02
 * @version     v1.0
 */
@Xjava(id="UserServiceXSSO")
public class UserService
{
    
    @Xjava
    private ClusterService clusterService;
    
    @Xjava
    private org.hy.microservice.common.user.UserService baseUserService;
    
    
    
    /**
     * 全局会话：生成全局会话USID，并设置已验证登录成功的用户信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-02
     * @version     v1.0
     * 
     * @param i_User
     * @return
     */
    public String usidMake(UserSSO i_User)
    {
        String v_SessionToken = org.hy.microservice.common.user.UserService.$USID + StringHelp.getUUID();
        this.usidAlive(v_SessionToken ,i_User);
        return v_SessionToken;
    }
    
    
    
    /**
     * 全局会话：全局会话保活
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-02
     * @version     v1.0
     *
     * @param i_USID
     * @param i_User
     */
    public void usidAlive(String i_USID ,UserSSO i_User)
    {
        if ( Help.isNull(i_USID) )
        {
            return;
        }
        
        XJava.putObject(i_USID ,i_User ,this.baseUserService.getMaxExpireTimeLen());
        
        // 同时，向单点集群（服务端）同步会话
        this.clusterService.aliveCluster(i_USID ,i_User ,this.baseUserService.getMaxExpireTimeLen());
    }
    
    
    
    /**
     * 全局会话：获取已登录的用户信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-02
     * @version     v1.0
     *
     * @param i_USID
     * @return
     */
    public UserSSO usidGetUser(String i_USID)
    {
        return (UserSSO)XJava.getObject(i_USID);
    }
    
    
    
    /**
     * 全局会话：删除用户登录信息。注销登录
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-04
     * @version     v1.0
     *
     * @param i_USID
     */
    public void usidRemove(String i_USID)
    {
        if ( Help.isNull(i_USID) )
        {
            return;
        }
        
        XJava.remove(i_USID);
        
        // 同时，向单点集群（服务端）同步会话
        this.clusterService.logoutCluster(i_USID);
    }
    
    
    
    /**
     * 全局会话：获取剩余有效时间的时长（单位：秒）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-03-03
     * @version     v1.0
     *
     * @param i_USID
     * @return
     */
    public long usidExpireTimeLen(String i_USID)
    {
        return XJava.getSessionMap().getExpireTimeLen(i_USID) / 1000;
    }
    
    
    
    /**
     * 本地会话：获取会话ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     *
     * @param i_Session
     * @return
     */
    public String sessionGetID(final HttpSession i_Session)
    {
        return this.baseUserService.sessionGetID(i_Session);
    }
    
    
    
    /**
     * 本地会话：保活 & 创建本地会话
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     * 
     * @param i_Session
     * @param io_User
     */
    public void sessionAlive(final HttpSession i_Session ,UserSSO io_User)
    {
        io_User.setSessionID(this.sessionGetID(i_Session));
        i_Session.setMaxInactiveInterval((int) this.baseUserService.getMaxExpireTimeLen());
        i_Session.setAttribute(org.hy.microservice.common.user.UserService.$SessionID ,io_User);
        
        // 同时，向单点集群（服务端）同步会话。但此处是按 SessionID 同步的但
        this.clusterService.aliveCluster(io_User.getSessionID() ,io_User ,this.baseUserService.getMaxExpireTimeLen());
    }
    
    
    
    /**
     * 本地会话：获取用户数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     * 
     * @param i_Session
     * @return
     */
    public UserSSO sessionGetUser(final HttpSession i_Session)
    {
        return this.baseUserService.sessionGetUser(i_Session);
    }
    
    
    
    /**
     * 本地会话：删除用户数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     * 
     * @param i_Session
     * @return
     */
    public void sessionRemove(final HttpSession i_Session)
    {
        this.baseUserService.sessionRemove(i_Session);
    }
    
    
    
    /**
     * 全局会话 & 本地会话：获取默认会话最大有效时长（单位：秒）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-03-03
     * @version     v1.0
     *
     * @return
     */
    public long getMaxExpireTimeLen()
    {
        return this.baseUserService.getMaxExpireTimeLen();
    }
    
}
