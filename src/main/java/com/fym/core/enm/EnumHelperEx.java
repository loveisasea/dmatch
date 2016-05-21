package com.fym.core.enm;

import com.fym.core.enm.obj.ClassUtils;
import com.fym.core.err.OpResult;
import com.fym.core.err.OpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Owned by Planck System
 * Created by fengy on 2016/2/6.
 */

public class EnumHelperEx {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnumHelperEx.class);

    public static final String Unknown = "<未知>";
    /**
     * <枚举类,map<键,显示名称>>
     */
    private static Map<Class, Map<Object, String>> map = new HashMap<>();

    /**
     * <枚举类,map<键,枚举>>
     */
    private static Map<Class, Map<Object, Object>> emap = new HashMap<>();

    /**
     * <枚举类TypeName，枚举类>
     */
    private static Map<String, Class> summary = new HashMap<>();


//    /**
//     * <枚举类.TypeName，枚举类>
//     */
//    private static Map<String, Class> typemap = new HashMap<>();
//
//
//    public static void register(Class emclz) {
//        try {
//            Field typeName = emclz.getDeclaredField("TypeName");
//            Object o = typeName.get(emclz.getClass());
//            if (o == null) {
//                LOGGER.error("类<" + emclz.getName() + ">字段<TypeName>为空");
//                return;
//            }
//            typemap.put(o.toString(), emclz);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//            LOGGER.error("类<" + emclz.getName() + ">没有字段<TypeName>");
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//            LOGGER.error("类<" + emclz.getName() + ">无法访问字段<TypeName>");
//        }
//    }
//
//    public static Map<Object, String> get(String typeName) throws OpException {
//        Class emclz = typemap.get(typeName);
//        if (emclz == null) {
//            throw new OpException(OpResult.FAIL, "<" + typeName + ">没有注册");
//        }
//        return Collections.unmodifiableMap(map.get(emclz));
//    }
//
//
//    public static Map getAll() {
//        Map<String, Set<Map.Entry<Object, String>>> ret = new HashMap<>();
//        for (Map.Entry<String, Class> stringClassEntry : typemap.entrySet()) {
//            ret.put(stringClassEntry.getKey(), get(stringClassEntry.getValue()));
//        }
//        return Collections.unmodifiableMap(ret);
//    }


    /**
     * 获取枚举的所有集合
     *
     * @param emclz 枚举的类
     * @return
     */
    public static Set<Map.Entry<Object, String>> get(Class emclz) {
        Map<Object, String> keyvalues = map.get(emclz);
        if (keyvalues == null) {
            addKeyValues(emclz);
        }
        return Collections.unmodifiableSet(keyvalues.entrySet());
    }


    /**
     * 根据键获取显示名称
     *
     * @param emclz 枚举的类
     * @param key   键
     * @return 返回的显示名称
     * @throws OpException
     */
    public static String get(Class emclz, Object key) {
        Map<Object, String> keyvalues = map.get(emclz);
        if (keyvalues == null) {
            addKeyValues(emclz);
            keyvalues = map.get(emclz);
        }
        String name = keyvalues.get(key);
        return name == null ? Unknown : name;
    }

    /**
     * 根据键获取显示名称
     *
     * @param emclz 枚举的类
     * @param key   键
     * @return 返回的显示名称
     * @throws OpException
     */
    public static <T> T eget(Class<T> emclz, Object key) {
        Map<Object, Object> mkeyvalues = emap.get(emclz);
        if (mkeyvalues == null) {
            addKeyValues(emclz);
            mkeyvalues = emap.get(emclz);
        }
        Object em = mkeyvalues.get(key);
        return (T) em;
    }

    /**
     * 根据名称获取键值对
     *
     * @param emclz 枚举的类
     * @param name  返显示名称
     * @return 键
     * @throws OpException
     */
    public static Object nget(Class<?> emclz, String name) {
        Map<Object, String> keyvalues = map.get(emclz);
        if (keyvalues == null) {
            addKeyValues(emclz);
            keyvalues = map.get(emclz);
        }
        for (Map.Entry<Object, String> objectStringEntry : keyvalues.entrySet()) {
            if (objectStringEntry.getValue().equals(name)) {
                return objectStringEntry.getKey();
            }
        }
        return null;
    }


    /**
     * 查看是否存在
     *
     * @param emclz 枚举的类
     * @param key   键
     * @return
     * @throws OpException
     */
    public static boolean exists(Class emclz, Object key) {
        Map<Object, String> keyvalues = map.get(emclz);
        if (keyvalues == null) {
            addKeyValues(emclz);
        }
        return keyvalues.get(key) != null;
    }


//    /**
//     * 获取枚举的所有集合
//     *
//     * @param clzFullName 类全名
//     * @return
//     */
//    public static Set<Map.Entry<Object, String>> get(String clzFullName) throws OpException {
//
//    }


    /**
     * 生成keyvalue的哈希表
     *
     * @param emclz 如果发生错误,记log,并返回空的哈希表
     * @return
     */
    public static void addKeyValues(Class emclz) {
        Map<Object, String> keyvalues = new LinkedHashMap<>();
        Map<Object, Object> mkeyvalues = new LinkedHashMap<>();
        try {
            if (!emclz.isEnum()) {
                throw new OpException(OpResult.INVALID, "<" + emclz.getName() + ">不是枚举类");
            }
            Field fkey = emclz.getDeclaredField("key");
            Field fname = emclz.getDeclaredField("name");
            //得到enum的所有实例
            Object[] objs = emclz.getEnumConstants();
            for (Object obj : objs) {
                Object ekey = fkey.get(obj);
                String ename = (String) fname.get(obj);
                if (ekey == null) {
                    LOGGER.error("枚举" + emclz.getName() + "初始化失败:键为空");
                }
                if (ename == null) {
                    LOGGER.error("枚举" + emclz.getName() + "初始化失败:名称为空");
                }
                if (keyvalues.containsKey(ekey)) {
                    LOGGER.error("枚举" + emclz.getName() + "初始化失败:键" + ekey + "已存在");

                }
                keyvalues.put(ekey, ename);
                mkeyvalues.put(ekey, obj);
            }
            map.put(emclz, keyvalues);
            emap.put(emclz, mkeyvalues);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            LOGGER.error("枚举类" + emclz.getName() + "初始化失败." + e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            LOGGER.error("枚举类" + emclz.getName() + "初始化失败." + e.getMessage());
        } catch (OpException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static Map<String, Map<Object, String>> getall() {
        Map<String, Map<Object, String>> ret = new HashMap<>();
        for (Map.Entry<String, Class> stringClassEntry : summary.entrySet()) {
            Map<Object, String> entries = map.get(stringClassEntry.getValue());
            ret.put(stringClassEntry.getKey(), entries);
        }
        return ret;
    }

    static {
        List<Class> classes = ClassUtils.scanPackage("com.fym");
        for (Class aClass : classes) {
            if (aClass.isEnum()) {
                EnumHelperEx.addKeyValues(aClass);
                try {
                    Field typeName = aClass.getDeclaredField("TypeName");
                    Object o = typeName.get(null);
                    if (summary.containsKey(o.toString())) {
                        LOGGER.error("枚举类包含相同的typeName<" + o.toString() + ">");
                    }
                    summary.put(o.toString(), aClass);
                } catch (NoSuchFieldException e) {
                    LOGGER.error("枚举类<" + aClass.getName() + ">没有包含typeName字段，无法在getAll中返回");
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    LOGGER.error("枚举类<" + aClass.getName() + ">typeName字段无法访问，无法在getAll中返回");
                    e.printStackTrace();
                }
            }
        }
    }

}
