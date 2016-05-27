package com.fym.game.enm;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/27.
 * 棋子状态
 */

import com.fym.core.enm.obj.IEnm;

        import java.lang.reflect.Field;
        import java.lang.reflect.Modifier;
        import java.util.HashMap;
        import java.util.Map;

public class ZeatStatus extends IEnm {
    public final static String TypeName = "棋子状态";
    public final static ZeatStatus 未动 = new ZeatStatus(1, "未动");
    public final static ZeatStatus 正常 = new ZeatStatus(2, "正常");
    public final static ZeatStatus 挂了 = new ZeatStatus(3, "挂了");
    private final static Map<Object, ZeatStatus> map = new HashMap<>();

    //public final int pcnt;

    ZeatStatus(Integer key, String name) {
        super(key, name);
        //this.pcnt = pcnt;
    }

    public static ZeatStatus get(Integer key) {
        return map.get(key);
    }

    public static String getn(Integer key) {
        ZeatStatus zeatstatus = get(key);
        return zeatstatus == null ? IEnm.Unknown : zeatstatus.name;
    }


    static {
        Field[] fields = ZeatStatus.class.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)
                    && field.getType().equals(field.getDeclaringClass())) {
                try {
                    ZeatStatus zeatstatus = (ZeatStatus) field.get(null);
                    map.put(zeatstatus.key, zeatstatus);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
} 
