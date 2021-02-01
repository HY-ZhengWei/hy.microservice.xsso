package org.hy.microservice.common;

import java.util.List;

import org.hy.common.Help;
import org.hy.common.xml.SerializableDef;





/**
 * 响应二级数据的基础类
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-01-08
 * @version     v1.0
 */
public class BaseResponseData<D> extends SerializableDef
{
    private static final long serialVersionUID = 1472565943960094849L;
    
    /** 页码。有效下标从1开始 */
    private Long    pageIndex;
    
    /** 分页总数 */
    private Long    pageSize;
    
    /** 每页显示数量 */
    private Long    pagePerCount;
    
    /** 记录数  */
    private Long    recordCount;
    
    /** 分页数据 */
    private List<D> datas;
    
    /** 一条数据 */
    private D       data;
    
    
    
    /**
     * 计算分页数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-01-29
     * @version     v1.0
     *
     * @param i_Mode
     */
    public void calcPage(BaseViewMode i_Mode)
    {
        this.setPageIndex(   i_Mode.getPageIndex());
        this.setPagePerCount(i_Mode.getPagePerCount());
        
        if ( this.pagePerCount != null )
        {
            this.setPageSize((long)Math.ceil(Help.division(this.recordCount ,this.pagePerCount)));
        }
    }

    
    
    /**
     * 获取：页码
     */
    public Long getPageIndex()
    {
        if ( this.pageIndex == null )
        {
            return null;
        }
        else if ( this.pageIndex <= 0 )
        {
            return 1L;
        }
        else
        {
            return pageIndex;
        }
    }

    
    /**
     * 获取：分页总数
     */
    public Long getPageSize()
    {
        return pageSize;
    }

    
    /**
     * 获取：每页显示数量
     */
    public Long getPagePerCount()
    {
        return pagePerCount;
    }

    
    /**
     * 获取：记录数
     */
    public Long getRecordCount()
    {
        return recordCount;
    }

    
    /**
     * 获取：分页数据
     */
    public List<D> getDatas()
    {
        return datas;
    }

    
    /**
     * 获取：一条数据
     */
    public D getData()
    {
        return data;
    }

    
    /**
     * 设置：页码
     * 
     * @param pageIndex 
     */
    public void setPageIndex(Long pageIndex)
    {
        this.pageIndex = pageIndex;
    }

    
    /**
     * 设置：分页总数
     * 
     * @param pageSize 
     */
    public void setPageSize(Long pageSize)
    {
        this.pageSize = pageSize;
    }

    
    /**
     * 设置：每页显示数量
     * 
     * @param pagePerCount 
     */
    public void setPagePerCount(Long pagePerCount)
    {
        this.pagePerCount = pagePerCount;
    }

    
    /**
     * 设置：记录数
     * 
     * @param recordCount 
     */
    public void setRecordCount(Long recordCount)
    {
        this.recordCount = recordCount;
    }

    
    /**
     * 设置：分页数据
     * 
     * @param datas 
     */
    public void setDatas(List<D> datas)
    {
        this.datas = datas;
    }

    
    /**
     * 设置：一条数据
     * 
     * @param data 
     */
    public void setData(D data)
    {
        this.data = data;
    }
    
}
