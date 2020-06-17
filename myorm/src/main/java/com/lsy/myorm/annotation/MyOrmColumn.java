package com.lsy.myorm.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 设置字段名
 *
 * @author lsy
 */
@Retention(RetentionPolicy.RUNTIME) //运行期间保留注解的信息
@Target(ElementType.FIELD)
public @interface MyOrmColumn {
    String name() default "";
}
