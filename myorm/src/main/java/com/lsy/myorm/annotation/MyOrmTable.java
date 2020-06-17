package com.lsy.myorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 设置表名
 *
 * @author lsy
 */
@Retention(RetentionPolicy.RUNTIME) //运行期间保留注解的信息
@Target(ElementType.TYPE)
public @interface MyOrmTable {
    String name() default "";
}
