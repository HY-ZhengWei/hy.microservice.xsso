package org.hy.microservice.xsso.accessToken;

import org.hy.common.ExpireMap;
import org.hy.common.StringHelp;
import org.hy.common.app.Param;
import org.hy.common.xml.annotation.Xjava;





/**
 * 临时登录Code的业务
 * 
 * 临时登录Code：指一次访问一个Code，一个Code仅能设置一次用户映射关系
 * 
 * 注：限制 "临时登录Code" 的使用次数，仅只能使用一次，使用一次后立即过期删除
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-05
 * @version     v1.0
 */
@Xjava
public class CodeService
{
    
    /**
     * 生成的临时登录Code。这里保存的Code只有使用一次。使用后立即释放，防止第二次非法访问。
     * 
     * map.key    为临时登录Code
     * map.value  为AppKey
     */
    private static ExpireMap<String ,String>  $CodeToAppKeys = new ExpireMap<String ,String>();
    
    
    
    /**
     * 临时登录Code有效时长（单位：秒）
     */
    @Xjava(ref="MS_XSSO_CodeTimeOut")
    private Param codeTimeOut;
    
    
    
    /**
     * 生成"临时登录Code",并绑定与AppKey的关系
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     * 
     * @param i_AppKey
     * @return
     */
    public String makeCode(String i_AppKey)
    {
        String v_Code = StringHelp.getUUID();
        this.setCode(v_Code ,i_AppKey);
        return v_Code;
    }
    
    
    
    /**
     * 设置临时登录Code与App应用的映射关系
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     * 
     * @param i_Code    临时登录Code
     * @param i_AppKey  App应用ID
     */
    public void setCode(String i_Code ,String i_AppKey)
    {
        $CodeToAppKeys.put(i_Code ,i_AppKey ,Integer.parseInt(this.codeTimeOut.getValue()));
    }
    
    
    
    /**
     * 获取Code与App应用的映射关系，好验证Code的真实有效性。
     * 
     * 限制 "临时登录Code" 的使用次数，仅只能使用一次，使用一次后立即过期删除
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-05
     * @version     v1.0
     * 
     * @param i_Code
     * @return
     */
    public String getAppKey(String i_Code)
    {
        return $CodeToAppKeys.remove(i_Code);
    }
    
}
