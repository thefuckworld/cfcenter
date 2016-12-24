package com.dw.cfcenter.common.datasource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.dw.cfcenter.common.datasource.DataSourceSelector.DataSourceType;

/**
 * Description: 数据源选择注解
 * @author caohui
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {
	DataSourceType value() default DataSourceType.RW;
}
