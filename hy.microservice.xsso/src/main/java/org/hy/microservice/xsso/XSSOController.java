package org.hy.microservice.xsso;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
    
    /** 登陆的Session会话ID标识，标识着是否登陆成功 */
    public  static final String $SessionID = "$XSSO$";
    
    
    
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
     * 票据的有效时长（单位：秒）
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
    public BaseResponse<TokenInfo> setLoginUser(@RequestParam("code") String i_Code ,@RequestBody UserSSO i_UserSSO ,HttpServletRequest i_Request)
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
        
        String v_SessionToken = i_Request.getSession().getId();
        i_UserSSO.setAppKey(v_AppKey);
        userService.setUser(v_SessionToken ,i_UserSSO);
        
        v_RetResp.setData(new TokenInfo());
        v_RetResp.getData().getData().setSessionToken(v_SessionToken);
        v_RetResp.getData().getData().setExpire((int)userService.getExpireTimeLen());
        
        return v_RetResp;
    }
    
    
    
    /**
     * 终端用户使用浏览器访问，一般在用户登录成功后执行，
     * 绑定全局会话 SessionToken 与 Session 会话 的关系
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-05-10
     * @version     v1.0
     * 
     * @param i_USID          会话级票据，与i_SessionToken同义，传送两参数任何一个即可，仅为支持老接口而并存
     * @param i_SessionToken  会话级票据，与i_USID        同义，传送两参数任何一个即可，仅为支持老接口而并存
     *
     * @return
     */
    @RequestMapping(value="binding" ,method={RequestMethod.GET})
    @ResponseBody
    public BaseResponse<String> binding(@RequestParam(name="USID"  ,required=false) String i_USID
                                       ,@RequestParam(name="token" ,required=false) String i_SessionToken
                                       ,HttpServletRequest                                 i_Request
                                       ,HttpServletResponse                                i_Response)
    {
        HttpSession          v_Session     = i_Request.getSession();
        UserSSO              v_SessionData = (UserSSO)v_Session.getAttribute($SessionID);
        String               v_USID        = Help.NVL(i_USID ,i_SessionToken);
        BaseResponse<String> v_RetResp     = new BaseResponse<String>();
        
        try
        {
            if ( !Help.isNull(v_USID) )
            {
                UserSSO v_User = this.userService.getUser(v_USID);
                if ( v_User != null )
                {
                    if ( v_SessionData != null )
                    {
                        // TODO 此处应通知所有单点服务器，退出之前的老会话
                    }

                    v_User.setUsid(v_USID);

                    v_Session.setMaxInactiveInterval(Integer.parseInt(tokenTimeOut.getValue()));
                    v_Session.setAttribute($SessionID ,v_User);

                    // TODO 此处应通知所有单点服务器，建立全局会话

                    $Logger.info("{} 票据有效，用户登录并绑定三方关系成功，建立本地会话。" ,v_USID);
                }
                else
                {
                    $Logger.info("{} 票据已失效 或 已过期 或 为非法票据。" ,v_USID);
                    v_RetResp.setCode("-1").setMessage("");
                }
            }
            else
            {
                $Logger.info("全局会话票据为空。会话信息-" + (v_SessionData != null ? "存在" : "没有"));
                v_RetResp.setCode("-1").setMessage("");
            }
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
            v_RetResp.setCode("-2").setMessage("");
        }
        
        return v_RetResp;
    }
    
    
    
    /**
     * 会话保活，一般在用户登录成功后并在每次（或每分钟）有效请求后执行。可由应用服务或终端发起均可
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-05-10
     * @version     v1.0
     * 
     * @param i_USID          会话级票据，与i_SessionToken同义，传送两参数任何一个即可，仅为支持老接口而并存
     * @param i_SessionToken  会话级票据，与i_USID        同义，传送两参数任何一个即可，仅为支持老接口而并存
     *
     * @return
     */
    @RequestMapping(value="alive" ,method={RequestMethod.GET})
    @ResponseBody
    public BaseResponse<String> alive(@RequestParam(name="USID"  ,required=false) String i_USID
                                     ,@RequestParam(name="token" ,required=false) String i_SessionToken
                                     ,HttpServletRequest                                 i_Request
                                     ,HttpServletResponse                                i_Response)
    {
        HttpSession          v_Session     = i_Request.getSession();
        UserSSO              v_SessionData = (UserSSO)v_Session.getAttribute($SessionID);
        String               v_USID        = Help.NVL(i_USID ,i_SessionToken);
        BaseResponse<String> v_RetResp     = new BaseResponse<String>();
        int                  v_IsAlive     = 0;
        
        try
        {
            if ( !Help.isNull(v_USID) )
            {
                UserSSO v_User = this.userService.getUser(v_USID);
                if ( v_User != null )
                {
                    this.userService.setUser(v_USID ,v_User);
                    v_IsAlive++;
                }
            }
            
            // 本地会话不是必须要有的哈
            if ( v_SessionData != null )
            {
                v_Session.setMaxInactiveInterval(Integer.parseInt(tokenTimeOut.getValue()));
                v_Session.setAttribute($SessionID ,v_SessionData);
                
                if ( v_IsAlive == 0 )
                {
                    this.userService.setUser(v_SessionData.getUsid() ,v_SessionData);
                }
                
                v_IsAlive++;
            }
            
            if ( v_IsAlive <= 0 )
            {
                $Logger.info("{} 票据已失效 或 已过期 或 为非法票据（会话保活）。" ,v_USID);
                v_RetResp.setCode("-1").setMessage("");
            }
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
            v_RetResp.setCode("-2").setMessage("");
        }
        
        return v_RetResp;
    }
    
    
    
    /**
     * 终端用户使用浏览器访问，通过Session会话，获取已登录用户的会话票据SessionToken
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-05-10
     * @version     v1.0
     *
     * @return
     */
    @RequestMapping(value="getUSID" ,method={RequestMethod.GET})
    public void getUSID(@RequestParam("SSOCallBack") String i_SSOCallBack ,HttpServletRequest i_Request ,HttpServletResponse i_Response)
    {
        i_Response.reset();
        i_Response.setCharacterEncoding("UTF-8");
        i_Response.setContentType("text/javascript");
        
        HttpSession v_Session     = i_Request.getSession();
        String      v_SessionID   = v_Session.getId();
        UserSSO     v_SessionData = (UserSSO)v_Session.getAttribute($SessionID);
        
        try
        {
            if ( v_SessionData == null || Help.isNull(v_SessionData.getUsid()) )
            {
                UserSSO v_XJavaUser = this.userService.getUser(v_SessionID);
                if ( v_XJavaUser != null )
                {
                    // 创建会话
                    v_Session.setMaxInactiveInterval(Integer.parseInt(tokenTimeOut.getValue()));
                    v_Session.setAttribute($SessionID ,v_XJavaUser);
                    
                    i_Response.getWriter().println(i_SSOCallBack + "('" + v_XJavaUser.getUsid() + "');");
                    $Logger.info("{} 全局会话有效，返回跨服务端的票据，并建立本地会话。" ,v_XJavaUser.getUsid());
                }
                else
                {
                    i_Response.getWriter().println(i_SSOCallBack + "('');");
                    $Logger.info("无全局会话，请登陆。");
                }
            }
            else
            {
                UserSSO v_XJavaUser = this.userService.getUser(v_SessionData.getUsid());
                if ( v_XJavaUser != null )
                {
                    i_Response.getWriter().println(i_SSOCallBack + "('" + v_SessionData.getUsid() + "');");
                    $Logger.info("{} 全局会话有效，返回票据。" ,v_SessionData.getUsid());
                }
                else
                {
                    v_Session.removeAttribute($SessionID);
                    v_Session.invalidate();
                    $Logger.info("{} 全局会话已失效。" ,v_SessionData.getUsid());
                    i_Response.getWriter().println(i_SSOCallBack + "('');");
                }
            }
        }
        catch (Exception exce)
        {
            $Logger.error(exce);
        }
    }
    
    
    
    /**
     * 获取登录用户信息。
     * 
     * 注：仅允许内网服务器访问使用
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
    public BaseResponse<UserSSO> logoutUser(@RequestParam(name="USID"  ,required=false) String i_USID
                                           ,@RequestParam(name="token" ,required=false) String i_SessionToken
                                           ,HttpServletRequest                                 i_Request
                                           ,HttpServletResponse                                i_Response)
    {
        BaseResponse<UserSSO> v_RetResp = new BaseResponse<UserSSO>();
        String                v_USID    = Help.NVL(i_USID ,i_SessionToken);
        
        if ( Help.isNull(v_USID) )
        {
            return v_RetResp.setCode("-1").setMessage("票据无效或已过期");
        }
        
        HttpSession v_Session     = i_Request.getSession();
        UserSSO     v_SessionData = (UserSSO)v_Session.getAttribute($SessionID);
        UserSSO     v_User        = this.userService.getUser(v_USID);
        if ( v_User == null )
        {
            return v_RetResp.setCode("-2").setMessage("用户已注销，请勿重复注销");
        }
        
        this.userService.removeUser(v_USID);
        
        if ( v_SessionData != null )
        {
            v_Session.removeAttribute($SessionID);
            v_Session.invalidate();
        }
        
        $Logger.info("{} 票据已失效，全局会话将销毁。" ,v_USID);
        return v_RetResp;
    }
    
}
