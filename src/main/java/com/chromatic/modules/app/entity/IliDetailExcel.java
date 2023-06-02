package com.chromatic.modules.app.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 基本数据-内检测明细表
 *
 * @author Seven ME info@7-me.net
 * @since v1.0.0 2021-11-08
 */
@Data
public class IliDetailExcel {
    @Excel(name = "内检测明细表ID")
    private String id;
    @Excel(name = "内检测历史表ID")
    private String iliHistoryId;
    @Excel(name = "里程 m")
    private BigDecimal kp;
    @Excel(name = "特征类型")
    private String feature;
    @Excel(name = "尺寸类型")
    private String dimension;
    @Excel(name = "周向")
    private Date orientation;
    @Excel(name = "缺陷深度 %")
    private BigDecimal depth;
    @Excel(name = "缺陷长度 mm")
    private BigDecimal length;
    @Excel(name = "缺陷宽度 mm")
    private BigDecimal width;
    @Excel(name = "内外指示", replace = {"INT_1", "EXT_0"})
    private String isInternal;
    @Excel(name = "ERF")
    private BigDecimal erf;
}