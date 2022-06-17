package org.hy.microservice.xsso.accessToken;

import org.hy.common.ExpireMap;
import org.hy.common.StringHelp;
import org.hy.common.app.Param;
import org.hy.common.xml.annotation.Xjava;
import org.hy.microservice.xsso.cluster.ClusterService;





/**
 * 访问票据的业务
 * 
 * 访问票据：指业务系统与单点登录系统间的通行证。正常情况下一个业务系统拥有一个 "访问票据" 即可
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-05
 * @version     v1.0
 */
@Xjava
public class AccessTokenService
{
    
    /** 访问票据的前缀 */
    public  static final String $AID = "AID";
    
    /**
     * 生成的访问TokenID
     * 
     * map.key    为AppKey
     * map.value  为AccessTokenID
     */
    private static ExpireMap<String ,String>  $AppKeyToAccessTokenIDs = new ExpireMap<String ,String>();
    
    /**
     * 生成的访问TokenID
     * 
     * map.key    为AccessTokenID
     * map.value  为AppKey
     */
    private static ExpireMap<String ,String>  $AccessTokenIDToAppKeys = new ExpireMap<String ,String>();
    
    /**
     * 票据的有效时长（单位：秒）
     */
    @Xjava(ref="MS_XSSO_TokenTimeOut")
    private Param tokenTimeOut;
    
    @Xjava
    private ClusterService clusterService;
    
    
    
    /**
     * 判定AppKey是否对应的 "访问票据"
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     * 
     * @param i_AppKey
     * @return
     */
    public boolean existsAppKey(String i_AppKey)
    {
        return $AppKeyToAccessTokenIDs.containsKey(i_AppKey);
    }
    
    
    
    /**
     * 通过AppKey找 "访问票据"
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     * 
     * @param i_AppKey
     * @return
     */
    public String getTokenID(String i_AppKey)
    {
        return $AppKeyToAccessTokenIDs.get(i_AppKey);
    }
    
    
    
    /**
     * 通过 "访问票据" 找AppKey
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     * 
     * @param i_TokenID
     * @return
     */
    public String getAppKey(String i_TokenID)
    {
        return $AccessTokenIDToAppKeys.get(i_TokenID);
    }
    
    
    
    /**
     * 获取AppKey对应访问票据的过期时间。与 getAppKeyExpireTimeLen() 的返回值是等于的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     * 
     * @param i_AppKey
     * @return         返回过期时长（单位：毫秒）
     */
    public long getTokenExpireTimeLen(String i_AppKey)
    {
        return $AppKeyToAccessTokenIDs.getExpireTimeLen(i_AppKey);
    }
    
    
    
    /**
     * 获取访问票据对应AppKey的过期时间。与 getTokenExpireTimeLen() 的返回值是等于的
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     * 
     * @param i_TokenID
     * @return         返回过期时长（单位：毫秒）
     */
    public long getAppKeyExpireTimeLen(String i_TokenID)
    {
        return $AccessTokenIDToAppKeys.getExpireTimeLen(i_TokenID);
    }
    
    
    
    /**
     * 生成"访问票据",并绑定与AppKey的关系
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     * 
     * @param i_AppKey
     * @param i_TokenID
     * @return
     */
    public String makeToken(String i_AppKey)
    {
        String v_Token = $AID + StringHelp.getUUID();
        
        this.setToken(i_AppKey ,v_Token);
        
        return v_Token;
    }
    
    
    
    /**
     * 设置应用系统AppKey对应的 "访问票据"
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     * 
     * @param i_AppKey
     * @param i_TokenID
     * @return
     */
    public void setToken(String i_AppKey ,String i_TokenID)
    {
        $AppKeyToAccessTokenIDs.put(i_AppKey  ,i_TokenID ,Integer.parseInt(tokenTimeOut.getValue()));
        $AccessTokenIDToAppKeys.put(i_TokenID ,i_AppKey  ,Integer.parseInt(tokenTimeOut.getValue()));
        
        // 同时，向单点集群（服务端）同步会话
        this.clusterService.setAccessToken(i_AppKey ,i_TokenID);
    }
    
}
