package com.fym.core.enm;

/**
 * Owned by Planck System
 * Created by fengy on 2016/2/6.
 */

public class EnumHelperEx {
//    private static final Logger LOGGER = LoggerFactory.getLogger(EnumHelperEx.class);
//
//    public static final String Unknown = "<未知>";
//
//    /**
//     * <枚举类,map<键,枚举>>
//     */
//    private static Map<Class, Map<Object, IEnm>> mapke = new HashMap<>();
//
//    /**
//     * <枚举类,map<名称,枚举>>
//     */
//    private static Map<Class, Map<String, IEnm>> mapne = new HashMap<>();
//
//    /**
//     * <枚举类TypeName，枚举类>
//     */
//    private static Map<String, Class> summary = new HashMap<>();
//
//
//    /**
//     * 获取枚举的所有集合
//     *
//     * @param emclz 枚举的类
//     * @return
//     */
//    public static <T extends IEnm> Set<Map.Entry<Object, IEnm>> getke(Class<T> emclz) {
//        Map<Object, IEnm> keyvalues = mapke.get(emclz);
////        if (keyvalues == null) {
////            addKeyValues(emclz);
////        }
//        return Collections.unmodifiableSet(keyvalues.entrySet());
//    }
//
//    /**
//     * 根据键获取枚举
//     *
//     * @param emclz 枚举的类
//     * @param key   键
//     * @return 枚举
//     * @throws OpException
//     */
//    public static <T extends IEnm> T kgete(Class<T> emclz, Object key) {
//        Map<Object, IEnm> mkeyvalues = mapke.get(emclz);
////        if (mkeyvalues == null) {
////            addKeyValues(emclz);
////            mkeyvalues = mapke.get(emclz);
////        }
//        Object em = mkeyvalues.get(key);
//        return (T) em;
//    }
//
//
//    /**
//     * 根据键获取显示名称
//     *
//     * @param emclz 枚举的类
//     * @param key   键
//     * @return 返回的显示名称
//     * @throws OpException
//     */
//    public static String kgetn(Class emclz, Object key) {
//        IEnm enm = kgete(emclz, key);
//        return enm == null ? Unknown : enm.getName();
//    }
//
//
//    /**
//     * 根据名称获取键值对
//     *
//     * @param emclz 枚举的类
//     * @param name  返显示名称
//     * @return 枚举
//     * @throws OpException
//     */
//    public static <T extends IEnm> T ngete(Class<T> emclz, String name) {
//        Map<String, IEnm> mkeyvalues = mapne.get(emclz);
////        if (mkeyvalues == null) {
////            addKeyValues(emclz);
////            mkeyvalues = mapne.get(emclz);
////        }
//        Object em = mkeyvalues.get(name);
//        return (T) em;
//    }
//
//    /**
//     * 根据键获取显示名称
//     *
//     * @param emclz 枚举的类
//     * @param name  名称
//     * @return 返回的键
//     * @throws OpException
//     */
//    public static Object ngetk(Class emclz, String name) {
//        IEnm enm = ngete(emclz, name);
//        return enm == null ? Unknown : enm.getKey();
//    }
//
//
//    /**
//     * 查看是否存在
//     *
//     * @param emclz 枚举的类
//     * @param key   键
//     * @return
//     * @throws OpException
//     */
//    public static boolean existsk(Class emclz, Object key) {
//        Map<Object, IEnm> keyvalues = mapke.get(emclz);
////        if (keyvalues == null) {
////            addKeyValues(emclz);
////        }
//        return keyvalues.get(key) != null;
//    }
//
//    /**
//     * 查看是否存在
//     *
//     * @param emclz 枚举的类
//     * @param name  名称
//     * @return
//     * @throws OpException
//     */
//    public static boolean existsn(Class emclz, String name) {
//        Map<String, IEnm> keyvalues = mapne.get(emclz);
////        if (keyvalues == null) {
////            addKeyValues(emclz);
////        }
//        return keyvalues.get(name) != null;
//    }
//
//
////    /**
////     * 获取枚举的所有集合
////     *
////     * @param clzFullName 类全名
////     * @return
////     */
////    public static Set<Map.Entry<Object, String>> get(String clzFullName) throws OpException {
////
////    }
//
//
//    /**
//     * 生成keyvalue的哈希表
//     *
//     * @param emclz 如果发生错误,记log,并返回空的哈希表
//     * @return
//     */
//    public static void addKeyValues(Class emclz) {
//        Map<Object, IEnm> kkeyvalues = new LinkedHashMap<>();
//        Map<Object, IEnm> nkeyvalues = new LinkedHashMap<>();
//        try {
//
//            Method getKeyMethod = emclz.getDeclaredMethod("getKey");
//            Method getNameMethod = emclz.getDeclaredMethod("getName");
//            //得到enum的所有实例
//            Object[] objs = emclz.getEnumConstants();
//            for (Object obj : objs) {
//                Object ekey = fkey.get(obj);
//                String ename = (String) fname.get(obj);
//                if (ekey == null) {
//                    LOGGER.error("枚举" + emclz.getName() + "初始化失败:键为空");
//                }
//                if (ename == null) {
//                    LOGGER.error("枚举" + emclz.getName() + "初始化失败:名称为空");
//                }
//                if (kkeyvalues.containsKey(ekey)) {
//                    LOGGER.error("枚举" + emclz.getName() + "初始化失败:键" + ekey + "已存在");
//
//                }
//                kkeyvalues.put(ekey, ename);
//                nkeyvalues.put(ekey, obj);
//            }
//            map.put(emclz, kkeyvalues);
//            mapke.put(emclz, nkeyvalues);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//            LOGGER.error("枚举类" + emclz.getName() + "初始化失败." + e.getMessage());
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//            LOGGER.error("枚举类" + emclz.getName() + "初始化失败." + e.getMessage());
//        } catch (OpException e) {
//            LOGGER.error(e.getMessage());
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static Map<String, Map<Object, String>> getall() {
//        Map<String, Map<Object, String>> ret = new HashMap<>();
//        for (Map.Entry<String, Class> clzEntry : summary.entrySet()) {
//            Map<Object, IEnm> entries = mapke.get(clzEntry.getValue());
//            Map<Object, String> entryNames = new HashMap<>();
//            for (IEnm iEnm : entries.values()) {
//                entryNames.put(iEnm.getKey(), iEnm.getName());
//            }
//            ret.put(clzEntry.getKey(), entryNames);
//        }
//        return ret;
//    }
//
//
//    static {
//        List<Class> classes = ClassUtils.scanPackage("com.fym");
//        Class<IEnm> iEnmClass = IEnm.class;
//        for (Class aClass : classes) {
//            //判断是否IEnum
//            boolean isFound = false;
//            for (Class interface1 : aClass.getInterfaces()) {
//                if (iEnmClass.equals(interface1)) {
//                    isFound = true;
//                }
//            }
//            if (!isFound) {
//                continue;
//            }
//
//            try {
//
//                EnumHelperEx.addKeyValues(aClass);
//
//                Field typeName = aClass.getDeclaredField("TypeName");
//                Object o;
//                if (typeName != null) {
//                    o = typeName.get(null);
//                } else {
//                    o = aClass.getName();
//                }
//
//                if (summary.containsKey(o.toString())) {
//                    LOGGER.error("枚举类包含相同的typeName<" + o.toString() + ">");
//                }
//                summary.put(o.toString(), aClass);
//            } catch (NoSuchFieldException e) {
//                LOGGER.error("枚举类<" + aClass.getName() + ">没有包含typeName字段，无法在getAll中返回");
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                LOGGER.error("枚举类<" + aClass.getName() + ">typeName字段无法访问，无法在getAll中返回");
//                e.printStackTrace();
//            }
//        }
//    }

}
