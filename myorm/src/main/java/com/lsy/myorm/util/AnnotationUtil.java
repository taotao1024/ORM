package com.lsy.myorm.util;


import com.lsy.myorm.annotation.MyOrmColumn;
import com.lsy.myorm.annotation.MyOrmId;
import com.lsy.myorm.annotation.MyOrmTable;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 使用反射解析实体类中注解的工具类
 *
 * @author lsy
 */
public class AnnotationUtil {


    /**
     * 得到的类名
     *
     * @param clz class
     * @return className
     */
    public static String getClassName(Class clz) {
        return clz.getName();
    }

    /**
     * 得到MyOrmTable注解中的表名
     *
     * @param clz class
     * @return
     */
    public static String getTableName(Class clz) {
        if (clz.isAnnotationPresent(MyOrmTable.class)) {
            MyOrmTable ormTable = (MyOrmTable) clz.getAnnotation(MyOrmTable.class);
            return ormTable.name();
        } else {
            System.out.println("缺少ORMTable注解");
            return null;
        }
    }

    /**
     * 得到主键属性和对应的字段
     *
     * @param clz class
     * @return
     */
    public static Map<String, String> getIdMapper(Class clz) {
        boolean flag = true;
        Map<String, String> map = new HashMap<>();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MyOrmId.class)) {
                flag = false;
                String fieldName = field.getName();
                if (field.isAnnotationPresent(MyOrmColumn.class)) {
                    MyOrmColumn ormColumn = field.getAnnotation(MyOrmColumn.class);
                    String columnName = ormColumn.name();
                    map.put(fieldName, columnName);
                    break;
                } else {
                    System.out.println("缺少ORMColumn注解");
                }
            }
        }
        if (flag) {
            System.out.println("缺少ORMId注解");
        }
        return map;
    }

    /**
     * 得到类中所有属性和对应的字段
     *
     * @param clz class
     * @return
     */
    public static Map<String, String> getPropMapping(Class clz) {
        Map<String, String> map = new HashMap<>();
        map.putAll(getIdMapper(clz));
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MyOrmColumn.class)) {
                MyOrmColumn ormColumn = field.getAnnotation(MyOrmColumn.class);
                String fieldName = field.getName();
                String columnName = ormColumn.name();
                map.put(fieldName, columnName);
            }
        }
        return map;
    }

    /**
     * 获得某包下面的所有类名
     *
     * @param packagePath 包路全限定类名
     * @return
     */
    public static Set<String> getClassNameByPackage(String packagePath) {  //cn.itcast.orm.entity
        Set<String> names = new HashSet<>();
        String packageFile = packagePath.replace(".", "/");
        String classpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        if (classpath == null) {
            classpath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
        }
        try {
            classpath = java.net.URLDecoder.decode(classpath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File dir = new File(classpath + packageFile);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File f : files) {
                String name = f.getName();
                if (f.isFile() && name.endsWith(".class")) {
                    name = packagePath + "." + name.substring(0, name.lastIndexOf("."));
                    names.add(name);
                }
            }
        } else {
            System.out.println("包路径不存在");
        }
        return names;
    }
}
