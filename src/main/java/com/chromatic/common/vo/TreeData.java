package com.chromatic.common.vo;

import com.baomidou.mybatisplus.extension.api.R;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.*;

@Data
public abstract class TreeData <T>{
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "pk")
    private Long id;

    @ApiModelProperty(value = "上级步骤id")
    private Long pid;

    @ApiModelProperty(value = "结点名称")
    private String name;

    @ApiModelProperty(value = "序号")
    private Integer number;

    @ApiModelProperty(value = "checked")
    private Boolean isChecked;
    @ApiModelProperty(value = "子树")
    private List<TreeData<T>> children;

    public TreeData() {
    }
    public TreeData(TreeData<T> root,List<TreeData<T>> children) {
        this.id = root.getId();
        this.pid = root.getPid();
        this.name = root.getName();
        this.number = root.getNumber();
        this.children = children;
    }


}
