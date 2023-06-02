package com.chromatic.common.mybatis.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类，所有实体都需要继承
 *
 * @author Seven ME info@7-me.net
 * @since 1.0.0
 */
@Data
public abstract class BaseEntity implements Serializable {
    /**
     * id
     */
    @TableId(value = "`id`", type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 创建时间
     */
    @TableField(value = "`create_time`", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_NULL)
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(value = "`update_time`", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 创建人
     */
    @TableField(value = "`create_user`", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_NULL)
    private String createUser;

    /**
     * 更新人
     */
    @TableField(value = "`update_user`", fill = FieldFill.INSERT_UPDATE)
    private String updateUser;

    /**
     * 删除标志
     */
    @TableField(value = "`del_flag`")
    @TableLogic(value = "0", delval = "NULL")
    private Integer delFlag;

}
