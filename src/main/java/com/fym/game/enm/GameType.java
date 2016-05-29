package com.fym.game.enm;

import com.fym.core.enm.obj.IEnm;
import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by fengy on 2016/5/20.
 */
public class GameType extends IEnm {
    public final static String TypeName = "游戏类型";
    public final static GameType _1v1_ladder = new GameType(1, "1v1天梯", 1);
    public final static GameType _1v1_normal = new GameType(2, "1v1普通", 1);
    public final static GameType _nvn_ladder = new GameType(3, "多人对战天梯", 3);
    public final static GameType _nvn_normal = new GameType(4, "多人对战普通", 3);
    private final static Map<Integer, GameType> map = new HashMap<>();

    public final int pcnt;

    GameType(Integer key, String name, int pcnt) {
        super(key, name);
        this.pcnt = pcnt;
    }

    public static GameType get(Integer key) throws OpException {
        GameType gameType = map.get(key);
        if (gameType == null) {
            throw new OpException(OpResult.FAIL, "找不到对应的" + TypeName + "<" + key + ">");
        }
        return gameType;
    }

    public static String getn(Integer key) throws OpException {
        GameType gameType = get(key);
        return gameType == null ? IEnm.Unknown : gameType.name;
    }

    public static Map<Integer, GameType> getall() {
        return Collections.unmodifiableMap(map);
    }


    static {
        Field[] fields = GameType.class.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) && Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)
                    && field.getType().equals(field.getDeclaringClass())) {
                try {
                    GameType gameType = (GameType) field.get(null);
                    map.put((Integer) gameType.key, gameType);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
