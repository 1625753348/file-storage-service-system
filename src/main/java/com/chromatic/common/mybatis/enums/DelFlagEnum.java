

package com.chromatic.common.mybatis.enums;

/**
 * 删除标识枚举类
 *
 * @author Seven ME info@7-me.net
 * @since 1.0.0
 */
public enum DelFlagEnum {
    NORMAL(0),
    DEL(1);

    private int value;

    DelFlagEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
