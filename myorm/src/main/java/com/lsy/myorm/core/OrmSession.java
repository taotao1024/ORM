package com.lsy.myorm.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 类似于MyBatis的OpenSession
 * 用户生成sql并实现增删改查功能
 *
 * @author lsy
 */
public class OrmSession {

    private Connection connection;

    public OrmSession(Connection conn) {
        this.connection = conn;
    }

    /**
     * 添加
     *
     * @param entity
     * @throws Exception
     */
    public void save(Object entity) throws Exception {
        String resultSql = "";

        // 1. 从ORMConfig中获得保存有映射信息的集合
        List<Mapper> mapperList = OrmConfig.mapperList;

        // 2. 遍历集合，从集合中找到和entity参数相对应的mapper对象
        for (Mapper mapper : mapperList) {
            String mapperClassName = mapper.getClassName();
            String entityName = entity.getClass().getName();
            if (mapperClassName.equals(entityName)) {
                String tableName = mapper.getTableName();
                //拼串 有SQL注入隐患
                String sql1 = "insert into " + tableName + "( ";
                String sql2 = " ) values ( ";

                // 3. 得到当前对象所属类中的所有属性
                Field[] fields = entity.getClass().getDeclaredFields();
                for (Field field : fields) {
                    //突破私有反射限制
                    field.setAccessible(true);
                    // 4. 遍历过程中根据属性得到字段名
                    String columnName = mapper.getPropMapper().get(field.getName());
                    // 5. 遍历过程中根据属性得到它的值
                    String columnValue = field.get(entity).toString();
                    // 6. 拼接sql语句
                    sql1 += columnName + ",";
                    sql2 += "'" + columnValue + "',";
                }
                resultSql = sql1.substring(0, sql1.length() - 1) + sql2.substring(0, sql2.length() - 1) + " )";
                break;
            }
        }

        // 把sql语句打印到控制台
        System.out.println("myORM-save: " + resultSql);

        // 7. 通过JDBC发送并执行sql
        PreparedStatement statement = connection.prepareStatement(resultSql);
        statement.executeUpdate();
        statement.close();
    }

    /**
     * 删除
     * <p/>
     * 根据主键进行数据删除：delete from  表名  where 主键 = 值
     *
     * @param entity
     * @throws Exception
     */
    public void delete(Object entity) throws Exception {
        String resultSql = "delete from ";

        // 1. 从ORMConfig中获得保存有映射信息的集合
        List<Mapper> mapperList = OrmConfig.mapperList;

        // 2. 遍历集合，从集合中找到和entity参数相对应的mapper对象
        for (Mapper mapper : mapperList) {
            if (mapper.getClassName().equals(entity.getClass().getName())) {
                // 3. 得到我们想要的mapper对象，并得到表名
                String tableName = mapper.getTableName();
                resultSql += tableName + " where ";
                // 4. 得到主键的字段名和属性名
                // idProp[0]
                Object[] idProp = mapper.getIdMapper().keySet().toArray();
                // idColumn[0]
                Object[] idColumn = mapper.getIdMapper().values().toArray();

                // 5. 得到主键的值
                Field field = entity.getClass().getDeclaredField(idProp[0].toString());
                field.setAccessible(true);
                String idVal = field.get(entity).toString();

                // 6. 拼接sql
                resultSql += idColumn[0].toString() + " = " + idVal;
                // 把sql语句打印到控制台
                System.out.println("myORM-delete: " + resultSql);
                break;
            }

        }
        // 7. 通过JDBC发送并执行sql
        PreparedStatement statement = connection.prepareStatement(resultSql);
        statement.executeUpdate();
        statement.close();
    }

    /**
     * 查找
     * <p/>
     * 根据主键进行查询：select * from 表名 where  主键字段 = 值
     *
     * @param clz
     * @param id
     * @return
     * @throws Exception
     */
    public Object findOne(Class clz, Object id) throws Exception {

        String resultSql = "select * from ";

        // 1. 从ORMConfig中得到存有映射信息的集合
        List<Mapper> mapperList = OrmConfig.mapperList;

        // 2. 遍历集合拿到我们想要的mapper对象
        for (Mapper mapper : mapperList) {
            if (mapper.getClassName().equals(clz.getName())) {

                // 3. 获得表名
                String tableName = mapper.getTableName();

                // 4. 获得主键字段名
                // idColumn[0]
                Object[] idColumn = mapper.getIdMapper().values().toArray();

                // 5. 拼接sql
                resultSql += tableName + " where " + idColumn[0].toString() + " = " + id;
                break;
            }
        }

        System.out.println("myORM-findOne:" + resultSql);

        // 6. 通过jdbc发送并执行sql, 得到结果集
        PreparedStatement statement = connection.prepareStatement(resultSql);
        ResultSet rs = statement.executeQuery();

        // 7. 封装结果集，返回对象
        if (rs.next()) {
            // 8.创建一个对象，目前属性的值都是初始值
            Object obj = clz.newInstance();
            // 9. 遍历mapperList集合找到我们想要的mapper对象
            for (Mapper mapper : mapperList) {
                if (mapper.getClassName().equals(clz.getName())) {
                    // 10. 得到存有属性-字段的映射信息
                    Map<String, String> propMap = mapper.getPropMapper();
                    // 11. 遍历集合分别拿到属性名和字段名
                    Set<String> keySet = propMap.keySet();
                    // prop就是属性名
                    for (String prop : keySet) {
                        // column就是和属性对应的字段名
                        String column = propMap.get(prop);
                        Field field = clz.getDeclaredField(prop);
                        field.setAccessible(true);
                        field.set(obj, rs.getObject(column));
                    }
                    break;
                }
            }
            // 12. 释放资源
            statement.close();
            rs.close();

            // 13. 返回查询出来的对象
            return obj;

        } else {
            // 没有查到数据
            return null;
        }

    }

    /**
     * 关闭连接、释放资源
     *
     * @throws Exception 关闭连接异常
     */
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

}
