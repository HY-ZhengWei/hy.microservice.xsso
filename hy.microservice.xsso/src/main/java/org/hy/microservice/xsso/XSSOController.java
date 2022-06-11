package org.hy.microservice.xsso;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.license.AppKey;
import org.hy.common.license.KeyStore;
import org.hy.common.license.SignProvider;
import org.hy.common.xml.log.Logger;
import org.hy.microservice.common.BaseResponse;
import org.hy.microservice.xsso.accessToken.AccessTokenService;
import org.hy.microservice.xsso.accessToken.CodeService;
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
     * 所有配置有效的应用AppKey数据
     */
    @Autowired
    @Qualifier("AppKeys")
    private Map<String ,AppKey> appKeys;
    
    @Autowired
    @Qualifier("AccessTokenService")
    private AccessTokenService  accessTokenService;
    
    @Autowired
    @Qualifier("CodeService")
    private CodeService         codeService;
    
    @Autowired
    @Qualifier("UserService")
    private UserService         userService;
    
    
    
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
            
            if ( this.accessTokenService.existsAppKey(v_AppKey.getAppKey()) )
            {
                v_RetResp.getData().getData().setAccessToken( this.accessTokenService.getTokenID(           v_AppKey.getAppKey()));
                v_RetResp.getData().getData().setExpire((int)(this.accessTokenService.getTokenExpireTimeLen(v_AppKey.getAppKey()) / 1000));
            }
            else
            {
                v_RetResp.getData().getData().setAccessToken(this.accessTokenService.makeToken(v_AppKey.getAppKey()));
                v_RetResp.getData().getData().setExpire(7200);
            }
            
            // 生成临时登录Code（有效期：5分钟）
            v_RetResp.getData().getData().setCode(this.codeService.makeCode(v_AppKey.getAppKey()));
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
        
        String v_AppKey        = this.accessTokenService.getAppKey(             i_AccessToken);
        long   v_ExpireTimeLen = this.accessTokenService.getAppKeyExpireTimeLen(i_AccessToken);
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
        
        String v_AppKey = this.codeService.getAppKey(i_Code);
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
        
        i_UserSSO.setLoginTime(new Date());
        v_RetResp.setData(new TokenInfo());
        v_RetResp.getData().getData().setSessionToken(this.userService.usidMake(i_UserSSO));
        v_RetResp.getData().getData().setExpire((int)userService.getMaxExpireTimeLen());
        
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
     * @param i_USID       会话级票据，与i_SessionToken同义，传送两参数任何一个即可，仅为支持老接口而并存
     * @param i_USIDToken  会话级票据，与i_USID        同义，传送两参数任何一个即可，仅为支持老接口而并存
     *
     * @return
     */
    @RequestMapping(value="binding" ,method={RequestMethod.GET})
    @ResponseBody
    public BaseResponse<String> binding(@RequestParam(name="USID"  ,required=false) String i_USID
                                       ,@RequestParam(name="token" ,required=false) String i_USIDToken
                                       ,HttpServletRequest                                 i_Request
                                       ,HttpServletResponse                                i_Response)
    {
        HttpSession          v_Session     = i_Request.getSession();
        UserSSO              v_SessionUser = this.userService.sessionGetUser(v_Session);
        String               v_USID        = Help.NVL(i_USID ,i_USIDToken);
        BaseResponse<String> v_RetResp     = new BaseResponse<String>();
        
        try
        {
            if ( !Help.isNull(v_USID) )
            {
                UserSSO v_USIDUser = this.userService.usidGetUser(v_USID);
                if ( v_USIDUser != null )
                {
                    if ( v_SessionUser != null && !v_USID.equals(v_SessionUser.getUsid()) )
                    {
                        // 此处应通知所有单点服务器，退出之前的老会话
                        this.userService.usidRemove(v_SessionUser.getUsid());
                        this.userService.usidRemove(v_SessionUser.getSessionID());
                    }

                    v_USIDUser.setUsid(v_USID);
                    v_USIDUser.setSessionID(this.userService.sessionGetID(v_Session));
                    v_USIDUser.setAliveTime(new Date());
                    this.userService.sessionAlive(v_Session              ,v_USIDUser);
                    this.userService.usidAlive(v_USID                    ,v_USIDUser);
                    this.userService.usidAlive(v_USIDUser.getSessionID() ,v_USIDUser);  // 再用SessionID多保活（或冗余一份用户数据）
                                                                                        // 原因是：方便getUSID()跨域访问时，有能力用SessionID得出USID
                    
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
                $Logger.info("全局会话票据为空。会话信息-" + (v_SessionUser != null ? "存在" : "没有"));
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
     * @param i_USID       会话级票据，与i_SessionToken同义，传送两参数任何一个即可，仅为支持老接口而并存
     * @param i_USIDToken  会话级票据，与i_USID        同义，传送两参数任何一个即可，仅为支持老接口而并存
     *
     * @return
     */
    @RequestMapping(value="alive" ,method={RequestMethod.GET})
    @ResponseBody
    public BaseResponse<String> alive(@RequestParam(name="USID"  ,required=false) String i_USID
                                     ,@RequestParam(name="token" ,required=false) String i_USIDToken
                                     ,HttpServletRequest                                 i_Request
                                     ,HttpServletResponse                                i_Response)
    {
        HttpSession          v_Session     = i_Request.getSession();
        UserSSO              v_SessionUser = null;
        UserSSO              v_USIDUser    = null;
        String               v_USID        = Help.NVL(i_USID ,i_USIDToken);
        BaseResponse<String> v_RetResp     = new BaseResponse<String>();
        int                  v_IsAlive     = 0;
        
        try
        {
            // 为预防非法使用全局会话USID，篡改保活数据。所以，
            //   1. 全局会话的用全局会话中的用户数据保活;
            //   2. 本地会话的用本地会话中的用户数据保活;
            //   3. 禁止混淆保活;
            // 如出现当全局会话的USID与本地会话的USID不一致时
            
            // 全局会话保活
            if ( !Help.isNull(v_USID) )    // 隐含：允许两个USID均不传值的情况
            {
                v_USIDUser = this.userService.usidGetUser(v_USID);
                if ( v_USIDUser != null )
                {
                    if ( v_IsAlive == 0 )
                    {
                        // 设置UserSSO.setSessionID(...)，在多种终端设备的情况下，可能出现与上次的值不一样
                        v_USIDUser.setSessionID(this.userService.sessionGetID(v_Session));
                        v_USIDUser.setAliveTime(new Date());
                        this.userService.usidAlive(v_USID ,v_USIDUser);
                        v_IsAlive++;
                    }
                }
            }
            
            // 本地会话保活：但本地会话也可能是不存在的，所以本地会话不是必须有的哈
            v_SessionUser = this.userService.sessionGetUser(v_Session);
            if ( v_SessionUser != null )
            {
                v_SessionUser.setAliveTime(new Date());
                this.userService.sessionAlive(v_Session ,v_SessionUser);
                if ( v_IsAlive == 0 )
                {
                    this.userService.usidAlive(v_SessionUser.getUsid() ,v_SessionUser);
                    v_IsAlive++;
                }
            }
            
            if ( v_IsAlive <= 0 )
            {
                $Logger.info("{} 票据已失效 或 已过期 或 为非法票据（会话保活）。" ,v_USID);
                v_RetResp.setCode("-1").setMessage("");
            }
            else
            {
                $Logger.debug("{} 会话保活。" ,v_USID);
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
        String      v_SessionID   = null;
        UserSSO     v_SessionUser = this.userService.sessionGetUser(v_Session);
        UserSSO     v_USIDUser    = null;
        
        try
        {
            if ( v_SessionUser == null || Help.isNull(v_SessionUser.getUsid()) )
            {
                v_SessionID = this.userService.sessionGetID(v_Session);
                v_USIDUser  = this.userService.usidGetUser(v_SessionID);
                if ( v_USIDUser != null )
                {
                    // 创建会话
                    this.userService.sessionAlive(v_Session ,v_USIDUser);
                    i_Response.getWriter().println(i_SSOCallBack + "('" + v_USIDUser.getUsid() + "');");
                    $Logger.info("{} 全局会话有效，返回跨服务端的票据，并建立本地会话。" ,v_USIDUser.getUsid());
                }
                else
                {
                    i_Response.getWriter().println(i_SSOCallBack + "('');");
                    $Logger.info("无全局会话，请登陆。");
                }
            }
            else
            {
                v_USIDUser = this.userService.usidGetUser(v_SessionUser.getUsid());
                if ( v_USIDUser != null )
                {
                    i_Response.getWriter().println(i_SSOCallBack + "('" + v_SessionUser.getUsid() + "');");
                    $Logger.info("{} 全局会话有效，返回票据。" ,v_SessionUser.getUsid());
                }
                else
                {
                    this.userService.sessionRemove(v_Session);
                    $Logger.info("{} 全局会话已失效。" ,v_SessionUser.getUsid());
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
     * @param i_USID       会话级票据，与i_SessionToken同义，传送两参数任何一个即可，仅为支持老接口而并存
     * @param i_USIDToken  会话级票据，与i_USID        同义，传送两参数任何一个即可，仅为支持老接口而并存
     * @return
     */
    @RequestMapping(value="getLoginUser" ,method={RequestMethod.GET})
    @ResponseBody
    public BaseResponse<UserSSO> getLoginUser(@RequestParam(name="USID"  ,required=false) String i_USID
                                             ,@RequestParam(name="token" ,required=false) String i_USIDToken)
    {
        BaseResponse<UserSSO> v_RetResp = new BaseResponse<UserSSO>();
        String                v_USID        = Help.NVL(i_USID ,i_USIDToken);
        
        if ( Help.isNull(v_USID) )
        {
            return v_RetResp.setCode("-1").setMessage(v_USID + " 票据无效或已过期");
        }
        
        UserSSO v_USIDUser = this.userService.usidGetUser(v_USID);
        if ( v_USIDUser == null )
        {
            return v_RetResp.setCode("-1").setMessage(v_USID + " 票据无效或已过期");
        }
        
        return v_RetResp.setData(v_USIDUser);
    }
    
    
    
    /**
     * 注销用户登录信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-04
     * @version     v1.0
     *
     * @param i_USID       会话级票据，与i_SessionToken同义，传送两参数任何一个即可，仅为支持老接口而并存
     * @param i_USIDToken  会话级票据，与i_USID        同义，传送两参数任何一个即可，仅为支持老接口而并存
     * @return
     */
    @RequestMapping(value="logoutUser" ,method={RequestMethod.GET})
    @ResponseBody
    public BaseResponse<UserSSO> logoutUser(@RequestParam(name="USID"  ,required=false) String i_USID
                                           ,@RequestParam(name="token" ,required=false) String i_USIDToken
                                           ,HttpServletRequest                                 i_Request
                                           ,HttpServletResponse                                i_Response)
    {
        BaseResponse<UserSSO> v_RetResp = new BaseResponse<UserSSO>();
        String                v_USID    = Help.NVL(i_USID ,i_USIDToken);
        
        if ( Help.isNull(v_USID) )
        {
            return v_RetResp.setCode("-1").setMessage("票据无效或已过期");
        }
        
        HttpSession v_Session     = i_Request.getSession();
        UserSSO     v_SessionUser = this.userService.sessionGetUser(v_Session);
        UserSSO     v_USIDUser    = this.userService.usidGetUser(v_USID);
        
        if ( v_SessionUser != null )
        {
            this.userService.sessionRemove(v_Session);
            this.userService.usidRemove(v_SessionUser.getSessionID());
        }
        
        this.userService.usidRemove(v_USID);
        if ( v_USIDUser == null )
        {
            return v_RetResp.setCode("-2").setMessage("用户已注销，请勿重复注销");
        }
        
        $Logger.info("{} 票据已失效，全局会话将销毁。" ,v_USID);
        return v_RetResp;
    }
    
}
