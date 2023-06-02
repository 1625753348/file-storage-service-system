package com.chromatic.modules.fileServer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件文档存储
 *
 */
@Data
@ApiModel(description = "文件文档存储")
public class DocumentFileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "")
	private Long id;

	@ApiModelProperty(value = "关联表表名")
	private String tableName;

	@ApiModelProperty(value = "文件相关联的表中的id逻辑fk")
	private Long refId;

	@ApiModelProperty(value = "文件名称")
	private String fileName;

	@ApiModelProperty(value = "文件存储路径")
	private String filePath;

	@ApiModelProperty(value = "文件描述")
	private String fileDesc;

	@ApiModelProperty(value = "文件扩展名")
	private String fileExt;

	@ApiModelProperty(value = "散列值")
	private String hexHash;

	@ApiModelProperty(value = "文件大小")
	private Long fileSize;

	@ApiModelProperty(value = "最后被修改时间")
	private Date lastedModified;

	@ApiModelProperty(value = "预览url")
	private String previewUrl;

	@ApiModelProperty(value = "是否为目录")
	private Boolean isDir;

	@ApiModelProperty(value = "删除标志")
	private Integer delFlag;

	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	@ApiModelProperty(value = "更新时间")
	private Date updateTime;}