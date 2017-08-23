package com.tandong.smartsqlite.key;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by office on 2017/8/23.
 */
@Target(ElementType.TYPE)//注解作用域
@Retention(RetentionPolicy.RUNTIME)//注解生命周期
public @interface TableNameInDB {
    /**
     * Table Name
     */
    public String value();
}
