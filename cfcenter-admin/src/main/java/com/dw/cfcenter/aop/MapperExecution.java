package com.dw.cfcenter.aop;

import java.util.Random;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;

/**
 * Description: 因为是以包扫描的方式扫描切面，所以需要@Component注释
 * @author caohui
 */
@Aspect
@Component
public class MapperExecution {

	private final Logger LOGGER = LoggerFactory.getLogger(MapperExecution.class);
	Random random = new Random();
	
	/**
	 * Description: 定义切入点
	 * All Rights Reserved.
	 *
	 * @return void
	 * @version 1.0 2016年12月22日 下午1:21:33 created by caohui(1343965426@qq.com)
	 */
	@Pointcut("execution(* com.dw.cfcenter..mapper.*.*(..))")
	public void pointcut() {
		
	}
	
	/**
	 * Description: 定义环绕通知
	 * All Rights Reserved.
	 *
	 * @param point
	 * @return
	 * @throws Throwable
	 * @return Object
	 * @version 1.0 2016年12月22日 下午1:22:58 created by caohui(1343965426@qq.com)
	 */
	@Around("pointcut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		int tryTime = 5;
		while(--tryTime > 0) {
		  try {
		    return point.proceed();
		  }catch(BadSqlGrammarException e) {
			  throw e;
		  }catch(Exception e) {
			  LOGGER.error("", e);
			  try {
				  // 短暂休眠后，进行重试
				  Thread.sleep(random.nextInt(2000));
			  }catch(InterruptedException e1) {
				  LOGGER.error("休眠失败!", e);
			  }
		  }
		}
		
		LOGGER.warn("重试4次了，最后一次!");
		Object object = point.proceed();
		LOGGER.warn("重试第5次成功了!");
		return object;
	}
}
