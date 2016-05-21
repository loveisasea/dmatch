package com.fym.core.err;

/**
 * Created by fengy on 2016/1/29.
 */
public class OpException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -5127076319556231134L;

    public OpResult opResult;

    public OpException(int opCode,String messsage) {
        this.opResult = new OpResult(opCode,messsage);
    }


    @Override
    public String getMessage() {
        return this.opResult.message;
    }


}
