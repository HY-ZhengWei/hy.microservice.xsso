package org.hy.microservice.xsso.user;

import org.hy.common.ExpireMap;
import org.hy.common.app.Param;
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
    
    /**
     * 已登录成功的用户数据。
     * 
     * 为了后期从Redis中取用户，禁止直接访问此类。
     * 
     * Map.key    为SessionTokenID
     * Map.value  为用户信息
     */
    private static ExpireMap<String ,UserSSO> $SessionTokenIDToUser = new ExpireMap<String ,UserSSO>();
    
    
    /**
     * 票据有效时长（单位：秒）
     */
    @Xjava(ref="MS_XSSO_TokenTimeOut")
    private Param tokenTimeOut;
    
    
    
    /**
     * 获取已登录的用户信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-02
     * @version     v1.0
     *
     * @param i_SessionToken
     * @return
     */
    public UserSSO getUser(String i_SessionToken)
    {
        return $SessionTokenIDToUser.get(i_SessionToken);
    }
    
    
    
    /**
     * 获取剩余有效时间的时长（单位：秒）。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-03-03
     * @version     v1.0
     *
     * @param i_SessionToken
     * @return
     */
    public long getExpireTimeLen(String i_SessionToken)
    {
        return $SessionTokenIDToUser.getExpireTimeLen(i_SessionToken) / 1000;
    }
    
    
    
    /**
     * 票据有效时长（单位：秒）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-03-03
     * @version     v1.0
     *
     * @return
     */
    public long getExpireTimeLen()
    {
        return Long.parseLong(tokenTimeOut.getValue());
    }
    
    
    
    /**
     * 设置已验证登录成功的用户信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-02
     * @version     v1.0
     *
     * @param i_SessionToken
     * @param i_User
     */
    public void setUser(String i_SessionToken ,UserSSO i_User)
    {
        $SessionTokenIDToUser.put(i_SessionToken ,i_User ,Integer.parseInt(tokenTimeOut.getValue()));
    }
    
    
    
    /**
     * 删除用户登录信息。注销登录
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-04
     * @version     v1.0
     *
     * @param i_SessionToken
     */
    public void removeUser(String i_SessionToken)
    {
        $SessionTokenIDToUser.remove(i_SessionToken);
    }
    
}
