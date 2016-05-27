package com.fym.game.enm;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/27.
 * 游戏类型
 */

import com.fym.core.enm.obj.IEnm;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class TeamType extends IEnm {
    public final static String TypeName = "红方黑方";
    public final static TeamType 红 = new TeamType(1, "红");
    public final static TeamType 黑 = new TeamType(2, "黑");
    private final static Map<Object, TeamType> map = new HashMap<>();

    //public final int pcnt;

    TeamType(Integer key, String name) {
        super(key, name);
        //this.pcnt = pcnt;
    }

    public static TeamType get(Integer key) {
        return map.get(key);
    }

    public static String getn(Integer key) {
        TeamType team = get(key);
        return team == null ? IEnm.Unknown : team.name;
    }


    static {
        Field[] fields = TeamType.class.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)
                    && field.getType().equals(field.getDeclaringClass())) {
                try {
                    TeamType team = (TeamType) field.get(null);
                    map.put(team.key, team);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
} 
