package com.fym.playerbox.obj;
/**
 * Owned by Planck System
 * Created by fengy on 2016/6/1.
 */

import com.fym.core.enm.obj.IEnm;
import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    public final static MsgID 玩家取消匹配 = new MsgID(9, "玩家取消匹配");
    public final static MsgID 游戏开始 = new MsgID(10, "游戏开始");
    private final static Map<Integer, MsgID> map = new HashMap<>();


    MsgID(Integer key, String name) {
        super(key, name);
    }

    public static MsgID get(Integer key) throws OpException {
        MsgID msgid = map.get(key);
        if (msgid == null) {
            throw new OpException(OpResult.FAIL, "找不到对应的" + TypeName + "<" + key + ">");
        }
        return msgid;
    }

    public static String getn(Integer key) throws OpException {
        MsgID msgid = get(key);
        return msgid == null ? IEnm.Unknown : msgid.name;
    }

    public static Map<Integer, MsgID> getall() {
        return Collections.unmodifiableMap(map);
    }


    static {
        Field[] fields = MsgID.class.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)
                    && field.getType().equals(field.getDeclaringClass())) {
                try {
                    MsgID msgid = (MsgID) field.get(null);
                    map.put((Integer) msgid.key, msgid);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
} 
