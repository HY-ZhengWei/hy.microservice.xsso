package org.hy.microservice.xsso.user;

import javax.servlet.http.HttpSession;

import org.hy.common.StringHelp;
import org.hy.common.app.Param;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;





/**
 * 用户业务
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-02-02
 * @version     v1.0
 */
@Xjava
public class UserService
{
    /** 登陆的Session会话ID标识，标识着是否登陆成功 */
    public  static final String $SessionID = "$XSSO$";
    
    /** 全局会话票据的前缀 */
    public  static final String $USID      = "USID";
    
    /** 本地会话票据的前缀 */
    public  static final String $SID       = "SID";
    
    
    
    /**
     * 票据有效时长（单位：秒）
     */
    @Xjava(ref="MS_XSSO_SessionTimeOut")
    private Param sessionTimeOut;
    
    
    
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
        String v_SessionToken = $USID + StringHelp.getUUID();
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
        XJava.putObject(i_USID ,i_User ,Integer.parseInt(sessionTimeOut.getValue()));
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
        XJava.remove(i_USID);
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
        return Long.parseLong(sessionTimeOut.getValue());
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
        return $SID + i_Session.getId();
    }
    
    
    
    /**
     * 本地会话：保活 & 创建本地会话
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     * 
     * @param i_Session
     * @param i_User
     */
    public void sessionAlive(final HttpSession i_Session ,UserSSO i_User)
    {
        i_User.setSessionID(this.sessionGetID(i_Session));
        i_Session.setMaxInactiveInterval(Integer.parseInt(sessionTimeOut.getValue()));
        i_Session.setAttribute($SessionID ,i_User);
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
        return (UserSSO)i_Session.getAttribute($SessionID);
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
        i_Session.removeAttribute($SessionID);
        i_Session.invalidate();
    }
    
}
