package com.dw.cfcenter.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.dw.cfcenter.common.datasource.DataSource;
import com.dw.cfcenter.common.datasource.DataSourceSelector.DataSourceType;
import com.dw.cfcenter.common.datasource.DataSourceSelector.HandleDataSource;

/**
 * Description: 切面是由包扫描的方式放入ApplicationContext，所以需要@Component注释
 * @author caohui
 */
@Aspect
@Component
public class ServiceExecution {

	/**
	 * Description: 定义切点
	 * All Rights Reserved.
	 *
	 * @return void
	 * @version 1.0 2016年12月22日 下午1:01:45 created by caohui(1343965426@qq.com)
	 */
	@Pointcut("execution(* com.dw.cfcenter..service.*.*(..))")
	public void beforeService() {
		
	}
	
	/**
	 * Description:定义前置通知
	 * All Rights Reserved.
	 *
	 * @param point
	 * @throws Throwable
	 * @return void
	 * @version 1.0 2016年12月22日 下午1:03:27 created by caohui(1343965426@qq.com)
	 */
	@Before("beforeService()")
	public void before(JoinPoint point) throws Throwable {
		Method method = ((MethodSignature) point.getSignature()).getMethod();
		if(null == method) {
			return;
		}
		
		// 获取实际接口实现类的方法
		Method targetMethod = point.getTarget().getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
		if(null == targetMethod) {
			return;
		}
		
		DataSourceType datasource = null;
		// 获取方法的注解
		if(targetMethod.isAnnotationPresent(DataSource.class)) {
			DataSource annotation = targetMethod.getAnnotation(DataSource.class);
			datasource = annotation.value();
		}
		
		// 如果方法没有，则查看类的
		if(null == datasource) {
			Class<?> interfaces = point.getTarget().getClass();
			if(null != interfaces && interfaces.isAnnotationPresent(DataSource.class)) {
				DataSource annotation = interfaces.getAnnotation(DataSource.class);
			    datasource = annotation.value();
			}
		}
		
		if(null != datasource) {
			HandleDataSource.putDataSource(datasource);
		}
	}
}
