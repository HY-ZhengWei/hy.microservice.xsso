package org.hy.microservice.xsso.cluster;

import org.hy.common.license.Hash;
import org.hy.common.license.IHash;
import org.hy.common.net.data.LoginRequest;
import org.hy.common.net.protocol.ServerValidate;




/**
 * 集群通讯时的访问验证
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-06-07
 * @version     v1.0
 */
public class ClusterValidate implements ServerValidate
{
    
    /** 集群通讯密码是否安全加盐过 */
    private final static String $PasswordEncrypt = "encrypt:";
    
    
    
    private LoginRequest user;
    
    
    
    /**
     * 对通讯登录密码加盐
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-07
     * @version     v1.0
     * 
     * @param io_LoginRequest
     * @return
     */
    public static LoginRequest encryptPassword(LoginRequest io_LoginRequest)
    {
        if ( !io_LoginRequest.getPassword().startsWith($PasswordEncrypt) )
        {
            IHash v_Hash = new Hash(2 ,2 ,io_LoginRequest.getUserName());
            io_LoginRequest.setPassword($PasswordEncrypt + v_Hash.encrypt(io_LoginRequest.getPassword()));
        }
        
        return io_LoginRequest;
    }
    
    
    
    public ClusterValidate(LoginRequest i_User)
    {
        this.user = encryptPassword(i_User);
    }
    
    
    
    /**
     * 服务端的登陆验证方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-06-07
     * @version     v1.0
     *
     * @param i_LoginRequest  登陆信息
     * @return                验证成功时，返回true
     */
    @Override
    public boolean validate(LoginRequest i_LoginRequest)
    {
        if ( i_LoginRequest == null )
        {
            return false;
        }
            
        if ( this.user.getSystemName().equals(i_LoginRequest.getSystemName())
          && this.user.getUserName()  .equals(i_LoginRequest.getUserName())
          && this.user.getPassword()  .equals(i_LoginRequest.getPassword()) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
}
