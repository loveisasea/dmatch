package com.fym.core.enm;

import com.fym.core.clz.ClassUtils;
import com.fym.core.enm.obj.IEnm;
import com.fym.core.err.OpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Owned by Planck System
 * Created by fengy on 2016/2/6.
 */

public class Enm {
    private static final Logger LOGGER = LoggerFactory.getLogger(Enm.class);

    public static final String Unknown = "<未知>";

    /**
     * <枚举类,map<键,枚举>>
     */
    private static Map<Class, Map<Object, IEnm>> map_clz_k_enm = new HashMap<>();


    public static <T extends IEnm> void add(T enm) {

        Map<Object, IEnm> keyvalues = map_clz_k_enm.get(enm.getClass());
        if (keyvalues == null) {
            keyvalues = new HashMap<Object, IEnm>();
            map_clz_k_enm.put(enm.getClass(), keyvalues);
        }
        if (keyvalues.containsKey(enm.key)) {
            LOGGER.error("<" + enm.getClass().getName() + ">已有重复的键<" + enm.key + ">");
        } else {
            keyvalues.put(enm.key, enm);
        }
    }


    /**
     * 根据键获取枚举
     *
     * @param emclz 枚举的类
     * @param key   键
     * @return 枚举
     * @throws OpException
     */
    public static <T extends IEnm> T kgete(Class<T> emclz, Object key) {
        Map<Object, IEnm> mkeyvalues = map_clz_k_enm.get(emclz);
        Object em = mkeyvalues.get(key);
        return (T) em;
    }


    /**
     * 根据键获取显示名称
     *
     * @param emclz 枚举的类
     * @param key   键
     * @return 返回的显示名称
     * @throws OpException
     */
    public static <T extends IEnm> String kgetn(Class<T> emclz, Object key) {
        IEnm enm = kgete(emclz, key);
        return enm == null ? Unknown : enm.name;
    }


    /**
     * 查看是否存在
     *
     * @param emclz 枚举的类
     * @param key   键
     * @return
     * @throws OpException
     */
    public static boolean existsk(Class emclz, Object key) {
        Map<Object, IEnm> keyvalues = map_clz_k_enm.get(emclz);
        return keyvalues.get(key) != null;
    }


    /**
     * 获取枚举的所有集合
     *
     * @param emclz 枚举的类
     * @return
     */
    public static <T extends IEnm> Set<Map.Entry<Object, IEnm>> getke(Class<T> emclz) {
        Map<Object, IEnm> keyvalues = map_clz_k_enm.get(emclz);
        return Collections.unmodifiableSet(keyvalues.entrySet());
    }

    /**
     * 获取枚举的所有集合
     *
     * @param emclz 枚举的类
     * @return
     */
    public static <T extends IEnm> Map<Object, String> getkn(Class<T> emclz) {
        Map<Object, IEnm> keyvalues = map_clz_k_enm.get(emclz);
        Map<Object, String> ret = new HashMap();
        for (Map.Entry<Object, IEnm> objectIEnmEntry : keyvalues.entrySet()) {
            ret.put(objectIEnmEntry.getKey(), objectIEnmEntry.getValue().name);
        }
        return ret;
    }

    public static <T extends IEnm> Map<String, Map<Object, String>> getall() {
        Map<String, Map<Object, String>> ret = new HashMap<>();
        for (Map.Entry<Class, Map<Object, IEnm>> clzEntrySet : map_clz_k_enm.entrySet()) {
            Class clz = clzEntrySet.getKey();
            try {
                Field typeName = clz.getDeclaredField("TypeName");
                Object o = typeName.get(null);
                Map<Object, String> entry = new HashMap();
                for (Map.Entry<Object, IEnm> objectIEnmEntry : clzEntrySet.getValue().entrySet()) {
                    entry.put(objectIEnmEntry.getKey(), objectIEnmEntry.getValue().name);
                }
                ret.put(o.toString(), entry);
            } catch (NoSuchFieldException e) {
                LOGGER.error("枚举类<" + clz.getName() + ">没有包含typeName字段，无法在getAll中返回");
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                LOGGER.error("枚举类<" + clz.getName() + ">typeName字段无法访问，无法在getAll中返回");
                e.printStackTrace();
            }
        }
        return ret;
    }


    static {
        List<Class> classes = ClassUtils.scanPackage("com.fym");
        Class<IEnm> iEnmClass = IEnm.class;
        for (Class aClass : classes) {
            //判断是否IEnum
            boolean isFound = false;
            for (Class interface1 : aClass.getInterfaces()) {
                if (iEnmClass.equals(interface1)) {
                    isFound = true;
                }
            }
            if (!isFound) {
                continue;
            }
        }
    }

}
