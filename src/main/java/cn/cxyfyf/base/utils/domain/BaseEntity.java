package cn.cxyfyf.base.utils.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity基类
 * 
 */
@Getter
@Setter
public class BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    @TableId
    protected Long id;

    /** 创建者 */
    protected String createBy;

    /** 创建时间 */
    protected LocalDateTime createTime;

    /** 更新者 */
    protected String updateBy;

    /** 更新时间 */
    protected LocalDateTime updateTime;

    /**
     * 其他前端参数
     */
    @TableField(exist = false)
    protected Map<String, Object> params;

}
