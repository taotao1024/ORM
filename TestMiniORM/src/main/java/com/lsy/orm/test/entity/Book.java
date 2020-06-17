package com.lsy.orm.test.entity;

//实体类：图书

import com.lsy.orm.annotation.ORMColumn;
import com.lsy.orm.annotation.ORMId;
import com.lsy.orm.annotation.ORMTable;

@ORMTable(name = "t_book")
public class Book {

    @ORMId
    @ORMColumn(name = "bid")
    private Integer id;

    @ORMColumn(name = "bname")
    private String name;

    @ORMColumn(name = "author")
    private String author;

    @ORMColumn(name = "price")
    private double price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
