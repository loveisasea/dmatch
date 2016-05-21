package com.fym.core.err;

/**
 * Created by fengy on 2016/1/29.
 */
public class OpResult {
    public final static int SUCCESS = 0;
    public final static int FAIL = 1;
    public final static int INVALID = 2;
    public final static int WARN = 3;
    public final static int SYSERROR = 4;
    public final static int RELOGIN = 5;
    public final static String STR_INVALID = "程序数据异常，请联系技术人员 ";
    public final static String STR_SYSERROR = "系统发生未知错误，请联系技术人员 ";

    public int opCode; //操作结果
    public String message; //操作结果信息
    public Object data; //数据

    public OpResult(int opCode) {
        this.opCode = opCode;
    }

    public OpResult(String message) {
        this.opCode = SUCCESS;
        this.message = message;
    }

    public OpResult(String message, Object data) {
        this.opCode = SUCCESS;
        this.message = message;
        this.data = data;
    }

    public OpResult(int opCode, String message) {
        this.opCode = opCode;
        this.message = message;

    }

    public OpResult(int opCode, String message, Object data) {
        this.opCode = opCode;
        this.message = message;
        this.data = data;

    }

}
