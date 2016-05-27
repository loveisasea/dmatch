package com.fym.core.clz;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Owned by Planck System
 * Created by fengy on 2016/4/19.
 */
public class ClassUtils {
    private static final String CLASS_PATH =  ClassUtils.class.getClassLoader().getResource("").getPath();
    private static final String EXT = ".class";
    private static final String BLANK_STR = "";
    private static final String DOT = ".";
    private static final ClassFilter DEFAULT_CLASS_FILTER = new ClassFilter(){
        @Override
        public boolean accept(Class clazz) {
            return Boolean.TRUE;
        }
    };
    private static FileFilter classFileFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.getName().endsWith(EXT)|| pathname.isDirectory();
        }
    };
    public static List<Class> scanPackage(String packageName,ClassFilter classFilter) {
        final File classFileDirectory = getScanClassFileDirectory(obtainFilePath(packageName));
        final List<Class> classList = new ArrayList<Class>();
        scanFiles(classFileDirectory, classList,classFilter == null ? DEFAULT_CLASS_FILTER : classFilter);
        return classList;
    }
    public static List<Class> scanPackage(String packageName) {
        return scanPackage(packageName,DEFAULT_CLASS_FILTER);
    }
    private static void scanFiles(File file, List<Class> classList,ClassFilter classFilter) {
        if (file.isDirectory()) {
            for (File f : file.listFiles(classFileFilter)) {
                scanFiles(f, classList,classFilter);
            }
        } else {
            try{
                final Class clazz = getClass(file,classFilter);
                if(clazz != null){
                    classList.add(clazz);
                }
            }catch(ClassNotFoundException ex){}
        }
    }
    private static Class getClass(File file,ClassFilter classFilter) throws ClassNotFoundException {
        final String packagePath = file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(CLASS_PATH)+ CLASS_PATH.length());
        Class clazz =  Class.forName(getClassFullName(packagePath));
        if(classFilter.accept(clazz)){
            return clazz;
        }
        return null;
    }
    private static String getClassFullName(String packagePath) {
        return packagePath.replace(File.separator, DOT).replace(EXT, BLANK_STR);
    }
    private static File getScanClassFileDirectory(String filePath) {
        return new File(filePath);
    }
    private static String obtainFilePath(String packageName) {
        final String oppositeFilePath = packageName.replace(DOT, File.separator);
        return CLASS_PATH + oppositeFilePath;
    }
}
 
