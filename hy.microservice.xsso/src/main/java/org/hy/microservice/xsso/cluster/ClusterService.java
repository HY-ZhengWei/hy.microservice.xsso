package org.hy.microservice.xsso.cluster;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.ExpireMap;
import org.hy.common.Help;
import org.hy.common.app.Param;
import org.hy.common.net.ClientSocketCluster;
import org.hy.common.net.common.ClientCluster;
import org.hy.common.net.data.CommunicationResponse;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.data.LoginResponse;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;
import org.hy.common.xml.log.Logger;
import org.hy.microservice.xsso.user.UserSSO;





/**
 * 用户业务
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-06
 * @version     v1.0
 */
@Xjava
public class ClusterService
{
    
    private final static Logger $Logger                = new Logger(ClusterService.class);
    
    /** 集群通讯的超时时长。默认为：30000毫秒 */
    public  final static long   $DefaultClusterTimeout = 30000;
    
    
    
    @Xjava(ref="MS_XSSO_ServerCluster")
    private List<ClientCluster> cluster;
    
    @Xjava(ref="MS_XSSO_ServerClusterTimeout")
    private Param               clusterTimeout;
    
    /**
     * 访问票据的有效时长（单位：秒）
     */
    @Xjava(ref="MS_XSSO_TokenTimeOut")
    private Param               tokenTimeOut;
    
    @Xjava(ref="MS_XSSO_ServerUser")
    private LoginRequest        loginRequest;
    
    @Xjava(ref="MS_XSSO_Server_WhoAmI")
    private Param               whoAmI;
    
    
    
    /**
     * 集群通讯的超时时长。默认为：30000毫秒
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-25
     * @version     v1.0
     *
     * @return
     */
    public long getClusterTimeout()
    {
        if ( this.clusterTimeout == null )
        {
            return $DefaultClusterTimeout;
        }
        else
        {
            return Long.parseLong(Help.NVL(this.clusterTimeout.getValue() ,"" + $DefaultClusterTimeout));
        }
    }
    
    
    
    /**
     * 票据的有效时长（单位：秒）
     * 
     * @return
     */
    public int getAccessTokenTimeOut()
    {
        return Integer.parseInt(tokenTimeOut.getValue());
    }
    
    
    
    /**
     * 启动并登录
     * 
     * @param i_Client
     */
    private synchronized boolean startAndLogin(ClientCluster i_Client)
    {
        $Logger.debug("启动并登录 S. " + i_Client.getHost() + ":" + i_Client.getPort());
        
        if ( !i_Client.operation().isStartServer() )
        {
            i_Client.operation().startServer();
        }
        
        boolean v_Ret = false;
        if ( !i_Client.operation().isLogin() )
        {
            LoginResponse v_Response = i_Client.operation().login(ClusterValidate.encryptPassword(this.loginRequest));
            
            v_Ret = v_Response.getResult() == LoginResponse.$Succeed;
        }
        else
        {
            v_Ret = true;
        }
        
        $Logger.debug("启动并登录 F. " + i_Client.getHost() + ":" + i_Client.getPort());
        
        return v_Ret;
    }
    
    
    
    /**
     * 当应用服务故障后重启时，同步单点登陆服务器的会话数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-07
     * @version     v1.0
     *
     */
    @SuppressWarnings("unchecked")
    public void syncSSOSessions()
    {
        $Logger.info("同步集群单点登陆服务的会话数据... ...");
        
        CommunicationResponse     v_ResponseData = null;
        ExpireMap<String ,Object> v_Datas        = null;
        List<ClientCluster>       v_Servers      = this.getSSOServersNoMy();
        
        for (ClientCluster v_Client : v_Servers)
        {
            if ( this.startAndLogin(v_Client) )
            {
                v_ResponseData = v_Client.operation().getSessionMap(this.getClusterTimeout());
                
                if ( v_ResponseData != null && v_ResponseData.getResult() == 0 )
                {
                    v_Datas = (ExpireMap<String ,Object>)v_ResponseData.getData();
                    
                    if ( !Help.isNull(v_Datas) ) {break;}
                }
            }
        }
        
        int v_Count = 0;
        
        if ( !Help.isNull(v_Datas) )
        {
            v_Count = v_Datas.size();
            XJava.getSessionMap().putAll(v_Datas);
        }
        
        $Logger.info("同步集群单点登陆服务的会话数据... ...完成. 共同步 " + v_Count + " 份。");
    }
    
    
    
    /**
     * 设置应用系统AppKey对应的 "访问票据"
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-17
     * @version     v1.0
     *
     * @param i_AppKey   应用签名
     * @param i_TokenID  访问级票据
     */
    public void setAccessToken(String i_AppKey ,String i_TokenID)
    {
        $Logger.debug("设置访问票据 S. " + i_AppKey + ":" + i_TokenID);
        
        List<ClientCluster> v_Servers = this.getSSOServersNoMy();
        
        if ( !Help.isNull(v_Servers) )
        {
            ClientSocketCluster.sendObjects(v_Servers
                                           ,this.getClusterTimeout()
                                           ,i_AppKey
                                           ,i_TokenID
                                           ,this.getAccessTokenTimeOut());
            
            ClientSocketCluster.sendObjects(v_Servers
                                           ,this.getClusterTimeout()
                                           ,i_TokenID
                                           ,i_AppKey
                                           ,this.getAccessTokenTimeOut());
        }
        
        $Logger.debug("设置访问票据 F. " + i_AppKey + ":" + i_TokenID);
    }
    
    
    
    /**
     * 用户首次登陆时，集群同步单点登陆信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_USID               全局会话ID
     * @param i_User               会话数据（一般为登陆的用户信息）
     */
    public void loginCluster(String i_USID ,UserSSO i_User ,long i_DataExpireTimeLen)
    {
        $Logger.debug("用户登陆 S. " + i_USID + ":" + i_User.getLoginAccount());
        
        List<ClientCluster> v_Servers = this.getSSOServersNoMy();
        
        if ( !Help.isNull(v_Servers) )
        {
            ClientSocketCluster.sendObjects(v_Servers
                                           ,this.getClusterTimeout()
                                           ,i_USID
                                           ,i_User
                                           ,i_DataExpireTimeLen);
        }
        
        $Logger.debug("用户登陆 F. " + i_USID + ":" + i_User.getLoginAccount());
    }
    
    
    
    /**
     * 用户退出时，集群移除单点登陆信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-23
     * @version     v1.0
     *
     * @param i_USID               全局会话ID
     */
    public void logoutCluster(String i_USID)
    {
        $Logger.debug("用户退出 S. " + i_USID);
        
        List<ClientCluster> v_Servers = this.getSSOServersNoMy();
        
        if ( !Help.isNull(v_Servers) )
        {
            ClientSocketCluster.removeObjects(v_Servers ,this.getClusterTimeout() ,i_USID);
        }
        
        $Logger.debug("用户退出 F. " + i_USID);
    }
    
    
    
    /**
     * 持会话活力及有效性
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-04
     * @version     v1.0
     *
     * @param i_USID               全局会话ID
     * @param i_User               会话数据（一般为登陆的用户信息）
     * @param i_DataExpireTimeLen  数据的过期时长(单位：秒)。小于等于0或为空，表示永远有效
     */
    public void aliveCluster(String i_USID ,UserSSO i_User ,long i_DataExpireTimeLen)
    {
        $Logger.debug("保活 S. " + i_USID + ":" + i_User.getLoginAccount());
        
        List<ClientCluster> v_Servers = this.getSSOServersNoMy();
        
        if ( !Help.isNull(v_Servers) )
        {
            ClientSocketCluster.sendObjects(v_Servers
                                           ,this.getClusterTimeout()
                                           ,i_USID
                                           ,i_User
                                           ,i_DataExpireTimeLen);
        }
        
        $Logger.debug("保活 F. " + i_USID + ":" + i_User.getLoginAccount());
    }
    
    
    
    /**
     * 获取除我之外的其它可用的集群配置信息
     * 
     * 内部，已启动和打开通讯通道
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-08
     * @version     v1.0
     *
     * @return
     */
    public List<ClientCluster> getSSOServersNoMy()
    {
        List<ClientCluster> v_Ret = new ArrayList<ClientCluster>();
        
        for (ClientCluster v_Client : this.cluster)
        {
            if ( !v_Client.getHost().equals(this.whoAmI.getValue()) )
            {
                if ( this.startAndLogin(v_Client) )
                {
                    v_Ret.add(v_Client);
                }
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取所有可用的集群配置信息
     * 
     * 内部，已启动和打开通讯通道
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-08
     * @version     v1.0
     *
     * @return
     */
    public List<ClientCluster> getSSOServers()
    {
        List<ClientCluster> v_Ret = new ArrayList<ClientCluster>();
        
        for (ClientCluster v_Client : this.cluster)
        {
            if ( this.startAndLogin(v_Client) )
            {
                v_Ret.add(v_Client);
            }
        }
        
        return v_Ret;
    }
}
