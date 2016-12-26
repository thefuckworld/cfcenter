package com.dw.cfcenter.web.method;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import com.dw.cfcenter.common.util.json.JsonUtils;

/**
 * Description: 返回json异常处理器
 * @author caohui
 */
public class ResponseJsonExceptionResovler extends AbstractHandlerExceptionResolver {

	private HttpMessageConverter<Object> messageConverter;
	private final static Logger LOGGER = LoggerFactory.getLogger(ResponseJsonExceptionResovler.class);
	
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		LOGGER.error("", ex);
		FailData failData = new FailData();
		String errorMsg = null;
		if(ex instanceof BindException) {
			errorMsg = bindExceptionResolver(ex);
		}else {
			errorMsg = ex.getMessage() == null ? "系统错误" : ex.getMessage();
		}
		
		// 抛出异常时，过滤敏感信息
		if(StringUtils.isNotBlank(errorMsg) && (errorMsg.contains("MySQLSyntaxErrorException") || errorMsg.contains("SQL syntax"))) {
			errorMsg = "配置中心有点动摇，待俺飞机哥瞅瞅...";
		}
		
		failData.setMsg(errorMsg);
		
		String returnValue = JsonUtils.toJson(failData);
		
		try {
			response.setContentType("application/json;charset=utf-8");
			messageConverter.write(returnValue, null, new ServletServerHttpResponse(response));
		}catch(HttpMessageNotWritableException e) {
			LOGGER.error(e.getMessage(), e);
		}catch(IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	private String bindExceptionResolver(Exception ex) {
		BindException bindException = (BindException) ex;
		List<ObjectError> allErrors = bindException.getBindingResult().getAllErrors();
		String[] errorField = new String[allErrors.size()];
		for(int i=0; i<allErrors.size(); i++) {
			ObjectError error = allErrors.get(i);
			if(!(error instanceof FieldError)) {
				continue;
			}
			
			FieldError fe = (FieldError) error;
			errorField[i] = fe.getField();
		}
		return StringUtils.join(errorField, ",") + "格式错误!";
	}

	public HttpMessageConverter<Object> getMessageConverter() {
		return messageConverter;
	}

	public void setMessageConverter(HttpMessageConverter<Object> messageConverter) {
		this.messageConverter = messageConverter;
	}

    class FailData {
		private String msg;

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}
