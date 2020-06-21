package com.lsy.myorm.core;

import com.lsy.myorm.util.AnnotationUtil;
import com.lsy.myorm.util.Dom4jUtil;
import org.dom4j.Document;

import java.io.File;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 解析并封装框架的核心配置文件中的数据
 *
 * @author lsy
 */
public class OrmConfig {

    /**
     * classpath路径
     */
    private static String classpath;
    /**
     * 核心配置文件 configurationFile
     */
    private static File cfgFile;
    /**
     * <property>标签中的数据
     */
    private static Map<String, String> propConfig;
    /**
     * 映射配置文件路径
     */
    private static Set<String> mappingSet;
    /**
     * 实体类
     */
    private static Set<String> entitySet;
    /**
     * 映射信息、需要对OrmSession提供访问
     */
    protected static List<Mapper> mapperList;

    /**
     * 解析核心数据
     * myORM.cfg.xml
     */
    static {
        //当前线程获取classpath路径
        classpath = Thread.currentThread().getContextClassLoader()
                .getResource("")
                .getPath();
        //针对中文路径进行转码
        try {
            classpath = URLDecoder.decode(classpath, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //得到核心配置文件
        System.out.println(classpath);
        cfgFile = new File(classpath + "myORM.cfg.xml");
        if (cfgFile.exists()) {
            // 解析核心配置文件中的数据
            Document document = Dom4jUtil.getXMLByFilePath(cfgFile.getPath());
            //数据库链接信息
            propConfig = Dom4jUtil.Elements2Map(document, "property", "name");
            //mapping.xml 映射数据
            mappingSet = Dom4jUtil.Elements2Set(document, "mapping", "resource");
            //注解下的属性
            entitySet = Dom4jUtil.Elements2Set(document, "entity", "package");
        } else {
            cfgFile = null;
            System.out.println("未找到核心配置文件myORM.cfg.xml");
        }

    }

    /**
     * 从propConfig集合中获取数据并连接数据库
     *
     * @return connection对象
     * @throws Exception 连接数据库异常
     */
    private Connection getConnection() throws Exception {
        //通过Map集合的key获取数据
        String url = propConfig.get("connection.url");
        String driverClass = propConfig.get("connection.driverClass");
        String username = propConfig.get("connection.username");
        String password = propConfig.get("connection.password");
        Class.forName(driverClass);
        Connection connection = DriverManager.getConnection(url, username, password);
        //自动事务提交
        connection.setAutoCommit(true);
        return connection;
    }

    private void getMapping() throws Exception {

        mapperList = new ArrayList<>();

        // 1.解析xxx.mapper.xml文件拿到映射数据
        // 使用Book.mapper.xml中的
        // <class name="com.lsy.myorm.test.pojo.Book" table="t_book">
        //    <id name="id" column="bid"></id>
        //    <property name="name" column="bname"></property>
        //    <property name="author" column="author"></property>
        //    <property name="price" column="price"></property>
        //  </class>
        for (String xmlPath : mappingSet) {
            Document document = Dom4jUtil.getXMLByFilePath(classpath + xmlPath);
            String className = Dom4jUtil.getPropValue(document, "class", "name");
            String tableName = Dom4jUtil.getPropValue(document, "class", "table");
            Map<String, String> id_id = Dom4jUtil.ElementsID2Map(document);
            Map<String, String> mapping = Dom4jUtil.Elements2Map(document);

            Mapper mapper = new Mapper();
            mapper.setTableName(tableName);
            mapper.setClassName(className);
            mapper.setIdMapper(id_id);
            mapper.setPropMapper(mapping);

            mapperList.add(mapper);

        }

        // 2.解析实体类中的注解拿到映射数据
        // 解析Pojo中的@MyOrmCumb、@MyOrmId、@MyOrmTable
        for (String packagePath : entitySet) {
            Set<String> nameSet = AnnotationUtil.getClassNameByPackage(packagePath);
            for (String name : nameSet) {
                Class clz = Class.forName(name);
                String className = AnnotationUtil.getClassName(clz);
                String tableName = AnnotationUtil.getTableName(clz);
                Map<String, String> id_id = AnnotationUtil.getIdMapper(clz);
                Map<String, String> mapping = AnnotationUtil.getPropMapping(clz);

                Mapper mapper = new Mapper();
                mapper.setTableName(tableName);
                mapper.setClassName(className);
                mapper.setIdMapper(id_id);
                mapper.setPropMapper(mapping);

                mapperList.add(mapper);
            }
        }
    }

    /**
     * 链接数据库、获取映射信息、创建OpenSession
     *
     * @return OpenSession
     * @throws Exception
     */
    public OrmSession buildORMSession() throws Exception {
        //1. 连接数据库
        Connection connection = this.getConnection();

        //2. 得到映射数据
        this.getMapping();

        //3. 创建ORMSession对象
        return new OrmSession(connection);
    }
}
