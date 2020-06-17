package com.lsy.myorm.test.pojo;

import com.lsy.myorm.annotation.MyOrmTable;
import com.lsy.myorm.annotation.MyOrmColumn;
import com.lsy.myorm.annotation.MyOrmId;


@MyOrmTable(name = "t_book")
public class Book {

    @MyOrmId
    @MyOrmColumn(name = "bid")
    private Integer id;

    @MyOrmColumn(name = "bname")
    private String name;

    @MyOrmColumn(name = "author")
    private String author;

    @MyOrmColumn(name = "price")
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

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", price=" + price +
                '}';
    }
}
