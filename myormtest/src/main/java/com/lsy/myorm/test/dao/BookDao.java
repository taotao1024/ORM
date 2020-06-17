package com.lsy.myorm.test.dao;

import com.lsy.myorm.core.ORMConfig;
import com.lsy.myorm.core.ORMSession;
import com.lsy.myorm.test.pojo.Book;
import org.junit.Before;
import org.junit.Test;

/**
 * 测试myorm
 *
 * @author lsy
 */
public class BookDao {

    private ORMConfig config;

    @Before
    public void init() {
        //1. 创建ORMConfig对象
        config = new ORMConfig();
    }

    @Test
    public void testSave() throws Exception {
        //2. 创建ORMSession对象
        ORMSession session = config.buildORMSession();
        //3. 创建实体类对象并保存
        Book book = new Book();
        book.setId(11);
        book.setName("java从入门到精通");
        book.setAuthor("传智播客");
        book.setPrice(9.9);
        session.save(book);
        //4. 释放资源
        session.close();
    }

    @Test
    public void testFindOne() throws Exception {
        //2. 创建ORMSession对象
        ORMSession session = config.buildORMSession();
        //3. 创建实体类对象并查询
        Book book = (Book) session.findOne(Book.class, 11);
        System.out.println(book.getName());
        //4. 释放资源
        session.close();
    }

    @Test
    public void testDelete() throws Exception {
        //2. 创建ORMSession对象
        ORMSession session = config.buildORMSession();

        //3. 创建实体类对象并删除
        Book book = new Book();
        book.setId(11);
        session.delete(book);

        //4. 释放资源
        session.close();
    }

}
