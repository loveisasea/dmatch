package com.fym.playerbox;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fym.playerbox.obj.IMsg;
import com.fym.playerbox.enm.PlayerStatus;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by fengy on 2016/1/29.
 */

@JsonIgnoreProperties(value = {})
public class PlayerBox {
    /**
     * 玩家id
     */
    public Integer pid;


    /**
     * 玩家状态
     */
    public PlayerStatus status;


    /**
     * 消息
     */
    public LinkedBlockingQueue<IMsg> msgs;


    public PlayerBox(Integer pid) {
        this.pid = pid;
        this.status = PlayerStatus.正常;
        this.msgs = new LinkedBlockingQueue<>();
    }


}
