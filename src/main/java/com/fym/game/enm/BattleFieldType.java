package com.fym.game.enm;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/27.
 * 棋盘
 */

import com.fym.core.enm.obj.IEnm;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class BattleFieldType extends IEnm {
    public final static String TypeName = "棋盘";
    public final static BattleFieldType _1v1 = new BattleFieldType(1, "1v1",9,10);
    public final static BattleFieldType _2v2 = new BattleFieldType(2, "2v2",18,10);
    public final static BattleFieldType _3v3 = new BattleFieldType(3, "3v3",27,10);
    private final static Map<Object, BattleFieldType> map = new HashMap<>();


    public final int width;
    public final int height;

    BattleFieldType(Integer key, String name, int width, int height) {
        super(key, name);
        this.width = width;
        this.height = height;
    }

    public static BattleFieldType get(Integer key) {
        return map.get(key);
    }

    public static String getn(Integer key) {
        BattleFieldType battlefieldtype = get(key);
        return battlefieldtype == null ? IEnm.Unknown : battlefieldtype.name;
    }


    static {
        Field[] fields = BattleFieldType.class.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)
                    && field.getType().equals(field.getDeclaringClass())) {
                try {
                    BattleFieldType battlefieldtype = (BattleFieldType) field.get(null);
                    map.put(battlefieldtype.key, battlefieldtype);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
} 
