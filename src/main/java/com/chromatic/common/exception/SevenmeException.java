

package com.chromatic.common.exception;

import com.chromatic.common.utils.MessageUtils;

/**
 * 自定义异常
 *
 * @author Seven ME info@7-me.net
 * @since 1.0.0
 */
public class SevenmeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;

    public SevenmeException(int code) {
        this.code = code;
        this.msg = MessageUtils.getMessage(code);
    }

    public SevenmeException(int code, String... params) {
        this.code = code;
        this.msg = MessageUtils.getMessage(code, params);
    }

    public SevenmeException(int code, Throwable e) {
        super(e);
        this.code = code;
        this.msg = MessageUtils.getMessage(code);
    }

    public SevenmeException(int code, Throwable e, String... params) {
        super(e);
        this.code = code;
        this.msg = MessageUtils.getMessage(code, params);
    }

    public SevenmeException(String msg) {
        super(msg);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
    }

    public SevenmeException(String msg, Throwable e) {
        super(msg, e);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
    }

    public SevenmeException(String msg, int value) {
        super(msg);
        this.msg = msg;
        this.code = value;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}