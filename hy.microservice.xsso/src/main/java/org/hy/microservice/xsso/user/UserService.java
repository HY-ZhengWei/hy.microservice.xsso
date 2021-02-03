package org.hy.microservice.xsso.user;

import org.hy.common.ExpireMap;
import org.hy.common.app.Param;
import org.hy.common.xml.annotation.Xjava;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;





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
     * Map.key    为TokenID
     * Map.value  为用户信息
     */
    private static ExpireMap<String ,UserSSO> $TokenIDToUser = new ExpireMap<String ,UserSSO>();
    
    
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
     * @param i_Token
     * @return
     */
    public UserSSO getUser(String i_Token)
    {
        return $TokenIDToUser.get(i_Token);
    }
    
    
    
    /**
     * 设置已验证登录成功的用户信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-02
     * @version     v1.0
     *
     * @param i_Token
     * @param i_User
     */
    public void setUser(String i_Token ,UserSSO i_User)
    {
        $TokenIDToUser.put(i_Token ,i_User ,Integer.parseInt(tokenTimeOut.getValue()));
    }
    
}
