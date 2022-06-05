package org.hy.microservice.common;

import java.util.List;





/**
 * 响应的基础类
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-01-08
 * @version     v1.0
 */
public class BaseResponse<D> extends BaseResp
{
    
    private static final long serialVersionUID = -8937744203898397851L;
    
    public  static final String $Succeed = "200";
    

    /** 响应数据 */
    private BaseResponseData<D> data;
    
    
    
    public BaseResponse()
    {
        this.code    = $Succeed;
        this.message = "成功";
    }
    
    
    
    /**
     * 设置：响应代码
     * 
     * @param code
     */
    @Override
    public BaseResponse<D> setCode(String code)
    {
        this.code = code;
        return this;
    }

    
    /**
     * 设置：响应消息
     * 
     * @param message
     */
    @Override
    public BaseResponse<D> setMessage(String message)
    {
        this.message = message;
        return this;
    }

    
    /**
     * 获取：响应数据
     */
    public BaseResponseData<D> getData()
    {
        return data;
    }

    
    /**
     * 设置：响应数据
     * 
     * @param data
     */
    public BaseResponse<D> setData(BaseResponseData<D> data)
    {
        this.data = data;
        return this;
    }
    
    
    /**
     * 设置一条数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-01-23
     * @version     v1.0
     *
     * @param i_Data
     */
    public BaseResponse<D> setData(D i_Data)
    {
        if ( this.data == null )
        {
            this.data = new BaseResponseData<D>();
        }
        
        this.data.setData(i_Data);
        return this;
    }
    
    
    /**
     * 设置很多数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-01-23
     * @version     v1.0
     *
     * @param i_Datas
     */
    public BaseResponse<D> setData(List<D> i_Datas)
    {
        if ( this.data == null )
        {
            this.data = new BaseResponseData<D>();
        }
        
        this.data.setDatas(i_Datas);
        return this;
    }
    
}
