package com.fym.playerbox.obj;
/**
 * Owned by Planck System
 * Created by fengy on 2016/6/1.
 */

import com.fym.core.enm.Enm;
import com.fym.core.enm.obj.IEnm;

public class MsgID extends IEnm {
    public final static String TypeName = "通知消息";
    public final static MsgID 组队队长邀请 = new MsgID(1, "组队队长邀请");
    public final static MsgID 组队候选人同意 = new MsgID(2, "组队候选人同意");
    public final static MsgID 组队候选人拒绝 = new MsgID(3, "组队候选人拒绝");
    public final static MsgID 组队队员离开 = new MsgID(4, "组队队员离开");
    public final static MsgID 开始匹配 = new MsgID(5, "开始匹配");
    public final static MsgID 匹配成功 = new MsgID(6, "匹配成功");
    public final static MsgID 玩家接受匹配 = new MsgID(7, "玩家接受匹配");
    public final static MsgID 玩家拒绝匹配 = new MsgID(8, "玩家拒绝匹配");
    public final static MsgID 玩家停止匹配 = new MsgID(9, "玩家停止匹配");
    public final static MsgID 玩家游戏已准备 = new MsgID(10, "玩家游戏已准备");
    public final static MsgID 玩家游戏走棋 = new MsgID(11, "玩家游戏走棋");
    public final static MsgID 玩家游戏退出 = new MsgID(12, "玩家游戏退出");
    public final static MsgID 玩家游戏断开连接 = new MsgID(13, "玩家游戏断开连接");
    public final static MsgID 玩家游戏重新连接 = new MsgID(14, "玩家游戏重新连接");
    public final static MsgID 游戏准备 = new MsgID(15, "游戏准备");
    public final static MsgID 游戏开始 = new MsgID(16, "游戏开始");
    public final static MsgID 游戏结束 = new MsgID(17, "游戏结束");


    MsgID(Integer key, String name) {
        super(key, name);
        Enm.add(this);
    }

} 
