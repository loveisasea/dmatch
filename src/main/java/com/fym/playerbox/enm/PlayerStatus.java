package com.fym.playerbox.enm;
/**
 * Owned by Planck System
 * Created by fengy on 2016/6/1.
 * 玩家状态
 */

import com.fym.core.enm.obj.IEnm;
import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class PlayerStatus extends IEnm {
    public final static String TypeName = "玩家状态";
    public final static PlayerStatus 正常 = new PlayerStatus(1, "正常");
    public final static PlayerStatus 战斗中 = new PlayerStatus(2, "战斗中");
    public final static PlayerStatus 观战直播 = new PlayerStatus(3, "观战直播");
    public final static PlayerStatus 观战录像 = new PlayerStatus(4, "观战录像");
    private final static Map<Integer, PlayerStatus> map = new HashMap<>();

    //public final int pcnt;

    PlayerStatus(Integer key, String name) {
        super(key, name);
        //this.pcnt = pcnt;
    }

    public static PlayerStatus get(Integer key) throws OpException {
        PlayerStatus playerstatus = map.get(key);
        if (playerstatus == null) {
            throw new OpException(OpResult.FAIL, "找不到对应的" + TypeName + "<" + key + ">");
        }
        return playerstatus;
    }

    public static String getn(Integer key) throws OpException {
        PlayerStatus playerstatus = get(key);
        return playerstatus == null ? IEnm.Unknown : playerstatus.name;
    }

    public static Map<Integer, PlayerStatus> getall() {
        return Collections.unmodifiableMap(map);
    }


    static {
        Field[] fields = PlayerStatus.class.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)
                    && field.getType().equals(field.getDeclaringClass())) {
                try {
                    PlayerStatus playerstatus = (PlayerStatus) field.get(null);
                    map.put((Integer) playerstatus.key, playerstatus);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
} 
