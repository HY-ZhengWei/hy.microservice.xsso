package org.hy.microservice.xsso;

import java.util.Map;

import org.hy.common.Date;
import org.hy.common.ExpireMap;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.app.Param;
import org.hy.common.license.AppKey;
import org.hy.common.license.KeyStore;
import org.hy.common.license.SignProvider;
import org.hy.common.xml.log.Logger;
import org.hy.microservice.common.BaseResponse;
import org.hy.microservice.xsso.accessToken.TokenInfo;
import org.hy.microservice.xsso.user.UserSSO;
import org.hy.microservice.xsso.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;





/**
 * 集成认证的控制层
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-02-01
 * @version     v1.0
 */
@Controller
@RequestMapping("xsso")
public class XSSOController
{
    
    private static final Logger $Logger = new Logger(XSSOController.class);
    
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
     * 生成的临时登录Code。这里保存的Code只有使用一次。使用后立即释放，防止第二次非法访问。
     * 
     * map.key    为临时登录Code
     * map.value  为AppKey
     */
    private static ExpireMap<String ,String>  $CodeToAppKeys          = new ExpireMap<String ,String>();
    
    
    
    /**
     * 所有配置有效的应用AppKey数据
     */
    @Autowired
    @Qualifier("AppKeys")
    private Map<String ,AppKey> appKeys;
    
    /**
     * 票据有效时长（单位：秒）
     */
    @Autowired
    @Qualifier("MS_XSSO_TokenTimeOut")
    private Param tokenTimeOut;
    
    /**
     * 票据有效时长（单位：秒）
     */
    @Autowired
    @Qualifier("MS_XSSO_CodeTimeOut")
    private Param codeTimeOut;
    
    @Autowired
    @Qualifier("UserService")
    private UserService userService;
    
    
    
    /**
     * 创建应用密钥对（非对称加密）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-01
     * @version     v1.0
     *
     * @param i_PostInfo
     * @return
     */
    @RequestMapping(value="createApp" ,method={RequestMethod.GET} ,produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BaseResponse<AppKey> createApp()
    {
        BaseResponse<AppKey> v_RetResp = new BaseResponse<AppKey>();
        AppKey               v_AppKey  = new AppKey();
        
        v_AppKey.setAppKey(StringHelp.getUUID());
        
        KeyStore v_KeyStore  = KeyStore.generater(v_AppKey.getAppKey());
        
        v_AppKey.setPublicKey( v_KeyStore.getPublicKeyString());
        v_AppKey.setPrivateKey(v_KeyStore.getPrivateKeyString());
        
        return v_RetResp.setData(v_AppKey);
    }
    
    
    
    /**
     * 获取访问Token
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-01
     * @version     v1.0
     *
     * @param i_Token  访问级的票据号
     * @return
     */
    @RequestMapping(value="getAccessToken" ,method={RequestMethod.GET})
    @ResponseBody
    public BaseResponse<TokenInfo> getAccessToken(TokenInfo i_Token)
    {
        BaseResponse<TokenInfo> v_RetResp = new BaseResponse<TokenInfo>();
        
        long v_Now = Date.getNowTime().getTime();
        
        if ( i_Token == null )
        {
            return v_RetResp.setCode("-1").setMessage("未收到任何参数");
        }
        
        if ( Help.isNull(i_Token.getAppKey()) || !appKeys.containsKey(i_Token.getAppKey()) ) 
        {
            return v_RetResp.setCode("-2").setMessage("AppKey无效");
        }
        
        if ( Help.isNull(i_Token.getTimestamp()) 
          || i_Token.getTimestamp() > v_Now + 1000 * 60
          || i_Token.getTimestamp() < v_Now - 1000 * 60 * 3 ) 
        {
            return v_RetResp.setCode("-3").setMessage("时间戳无效或已过期");
        }
        
        if ( Help.isNull(i_Token.getSignature()) ) 
        {
            return v_RetResp.setCode("-4").setMessage("签名不正确");
        }
        
        try
        {
            String  v_Code   = "appKey" + i_Token.getAppKey() + "timestamp" + i_Token.getTimestamp().longValue();
            AppKey  v_AppKey = appKeys.get(i_Token.getAppKey());
            boolean v_Verify = SignProvider.verify(v_AppKey.getPublicKey().getBytes() ,v_Code ,i_Token.getSignature().getBytes("UTF-8"));
            if ( !v_Verify )
            {
                return v_RetResp.setCode("-4").setMessage("签名不正确");
            }
            
            v_RetResp.setData(new TokenInfo());
            
            if ( $AppKeyToAccessTokenIDs.containsKey(v_AppKey.getAppKey()) )
            {
                v_RetResp.getData().getData().setAccessToken( $AppKeyToAccessTokenIDs.get(v_AppKey.getAppKey()));
                v_RetResp.getData().getData().setExpire((int)($AppKeyToAccessTokenIDs.getExpireTimeLen(v_AppKey.getAppKey()) / 1000));
            }
            else
            {
                v_RetResp.getData().getData().setAccessToken(StringHelp.getUUID());
                v_RetResp.getData().getData().setExpire(7200);
                
                $AppKeyToAccessTokenIDs.put(v_AppKey.getAppKey() ,v_RetResp.getData().getData().getAccessToken() ,Integer.parseInt(tokenTimeOut.getValue()));
                $AccessTokenIDToAppKeys.put(v_RetResp.getData().getData().getAccessToken() ,v_AppKey.getAppKey() ,Integer.parseInt(tokenTimeOut.getValue()));
            }
            
            // 生成临时登录Code（有效期：5分钟）
            v_RetResp.getData().getData().setCode(StringHelp.getUUID());
            $CodeToAppKeys.put(v_RetResp.getData().getData().getCode() ,v_AppKey.getAppKey() ,Integer.parseInt(codeTimeOut.getValue()));
            
            return v_RetResp;
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
        
        return v_RetResp.setCode("-999").setMessage("异常");
    }
    
    
    
    /**
     * 获取应用编码AppKey及过期时间
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-03-03
     * @version     v1.0
     *
     * @param i_AccessToken  访问级的票据号
     * @return
     */
    @RequestMapping(value="getAppKey" ,method={RequestMethod.GET})
    @ResponseBody
    public BaseResponse<TokenInfo> getAppKey(@RequestParam("token") String i_AccessToken)
    {
        BaseResponse<TokenInfo> v_RetResp = new BaseResponse<TokenInfo>();
        
        if ( Help.isNull(i_AccessToken) )
        {
            return v_RetResp.setCode("-1").setMessage("票据无效或已过期");
        }
        
        String v_AppKey        = $AccessTokenIDToAppKeys.get(i_AccessToken);
        long   v_ExpireTimeLen = $AccessTokenIDToAppKeys.getExpireTimeLen(i_AccessToken);
        if ( v_AppKey == null || v_ExpireTimeLen <= 0 )
        {
            return v_RetResp.setCode("-1").setMessage("票据无效或已过期");
        }
        
        v_RetResp.setData(new TokenInfo());
        v_RetResp.getData().getData().setAppKey(v_AppKey);
        v_RetResp.getData().getData().setExpire((int)(v_ExpireTimeLen / 1000));
        
        return v_RetResp;
    }
    
    
    
    /**
     * 设置票据与登录用户信息的关系
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-02
     * @version     v1.0
     *
     * @param i_Code     临时登录Code（只能使用一次）
     * @param i_UserSSO  登录用户
     * @return
     */
    @RequestMapping(value="setLoginUser" ,method={RequestMethod.POST} ,produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BaseResponse<TokenInfo> setLoginUser(@RequestParam("code") String i_Code ,@RequestBody UserSSO i_UserSSO)
    {
        BaseResponse<TokenInfo> v_RetResp = new BaseResponse<TokenInfo>();
        
        if ( Help.isNull(i_Code) )
        {
            return v_RetResp.setCode("-1").setMessage("临时登录Code无效或已过期");
        }
        
        String v_AppKey = $CodeToAppKeys.remove(i_Code);
        if ( Help.isNull(v_AppKey) )
        {
            return v_RetResp.setCode("-1").setMessage("临时登录Code无效或已过期");
        }
        
        if ( i_UserSSO == null )
        {
            return v_RetResp.setCode("-101").setMessage("用户信息为空"); 
        }
        
        if ( !v_AppKey.equals(i_UserSSO.getAppKey()) )
        {
            return v_RetResp.setCode("-102").setMessage("非法访问"); 
        }
        
        if ( Help.isNull(i_UserSSO.getUserId()) )
        {
            return v_RetResp.setCode("-103").setMessage("用户ID为空"); 
        }
        
        String v_SessionToken = StringHelp.getUUID();
        i_UserSSO.setAppKey(v_AppKey);
        userService.setUser(v_SessionToken ,i_UserSSO);
        
        v_RetResp.setData(new TokenInfo());
        v_RetResp.getData().getData().setSessionToken(v_SessionToken);
        v_RetResp.getData().getData().setExpire((int)userService.getExpireTimeLen());
        
        return v_RetResp;
    }
    
    
    
    /**
     * 获取登录用户信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-03
     * @version     v1.0
     *
     * @param i_SessionToken  会话级的票据号
     * @return
     */
    @RequestMapping(value="getLoginUser" ,method={RequestMethod.GET})
    @ResponseBody
    public BaseResponse<UserSSO> getLoginUser(@RequestParam("token") String i_SessionToken)
    {
        BaseResponse<UserSSO> v_RetResp = new BaseResponse<UserSSO>();
        
        if ( Help.isNull(i_SessionToken) )
        {
            return v_RetResp.setCode("-1").setMessage("票据无效或已过期");
        }
        
        UserSSO v_User = this.userService.getUser(i_SessionToken);
        if ( v_User == null )
        {
            return v_RetResp.setCode("-1").setMessage("票据无效或已过期");
        }
        
        return v_RetResp.setData(v_User);
    }
    
    
    
    /**
     * 注销用户登录信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-04
     * @version     v1.0
     *
     * @param i_SessionToken  会话级的票据号
     * @return
     */
    @RequestMapping(value="logoutUser" ,method={RequestMethod.GET})
    @ResponseBody
    public BaseResponse<UserSSO> logoutUser(@RequestParam("token") String i_SessionToken)
    {
        BaseResponse<UserSSO> v_RetResp = new BaseResponse<UserSSO>();
        
        if ( Help.isNull(i_SessionToken) )
        {
            return v_RetResp.setCode("-1").setMessage("票据无效或已过期");
        }
        
        UserSSO v_User = this.userService.getUser(i_SessionToken);
        if ( v_User == null )
        {
            return v_RetResp.setCode("-2").setMessage("用户已注销，请勿重复注销");
        }
        
        this.userService.removeUser(i_SessionToken);
        
        return v_RetResp;
    }
    
}
