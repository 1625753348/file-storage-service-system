package com.chromatic.common.enums;

/**
 * 查询条件运算符枚举
 *
 * @author Seven ME info@7-me.net
 * @since 1.0.0
 */
public enum OperatorEnum {
    EQ(0),
    NE(1),
    BETWEEN(2),
    IN(3),
    LIKE(4),
    LE(5),
    LT(6),
    GE(7),
    GT(8);

    private int value;

    OperatorEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
