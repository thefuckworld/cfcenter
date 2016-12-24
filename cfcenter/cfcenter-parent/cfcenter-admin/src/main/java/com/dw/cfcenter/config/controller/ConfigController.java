package com.dw.cfcenter.config.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dw.cfcenter.common.result.processor.CommonResultVO;
import com.dw.cfcenter.common.result.processor.ResultMsgAndCode;
import com.dw.cfcenter.common.result.processor.ResultProcessUtils;
import com.dw.cfcenter.config.service.ConfigService;
import com.dw.cfcenter.config.vo.ConfigVO;
import com.dw.cfcenter.encryption.AESEncrypt;
import com.dw.cfcenter.request.vo.RequestParamVO;

@Controller
@RequestMapping("/client")
public class ConfigController {

	@Resource
	private ConfigService configService;
	
	@ResponseBody
	@RequestMapping("/loadDataByServerId.json")
	public String getDataByProject(RequestParamVO requestPO) {
		CommonResultVO<List<ConfigVO>> resultVO = new CommonResultVO<List<ConfigVO>>();
		if(!validateRequestParam(requestPO)) {
		   resultVO.setCode(ResultMsgAndCode.PARAM_ERROR.getCode());
		   resultVO.setMsg(ResultMsgAndCode.PARAM_ERROR.getMsg());
		   resultVO.setData(null);
		   return ResultProcessUtils.newInstance().serializeResult(resultVO);	
		}
		
		List<ConfigVO> list = configService.getDataByProject(requestPO.getProjectName());
		resultVO.setCode(ResultMsgAndCode.REQUEST_SUCCESS.getCode());
		resultVO.setMsg(ResultMsgAndCode.REQUEST_SUCCESS.getMsg());
		resultVO.setData(list);
		return ResultProcessUtils.newInstance().serializeResult(resultVO);
	}
	
	@ResponseBody
	@RequestMapping("/addDataSource")
	public String addDataSource(ConfigVO configVo) {
		CommonResultVO resultVo = new CommonResultVO();
		configVo.setSourceName(AESEncrypt.encrypt(configVo.getSourceValue().getBytes()));
		configService.addDataSource(configVo);
		
		/* TODO:将添加的数据填充到客户端：
		 * 1、查询订阅了该数据源的服务器Ip:Port
		 * 2、将新添加的数据源通过NIO写回到客户端
		 */
		resultVo.setCode(ResultMsgAndCode.ADD_SUCESS.getCode());
		resultVo.setMsg(ResultMsgAndCode.ADD_SUCESS.getMsg());
		return ResultProcessUtils.newInstance().serializeResult(resultVo);
	}
	
	private boolean validateRequestParam(RequestParamVO requestPO) {
		
		if(StringUtils.isEmpty(requestPO.getIp()) 
		   || requestPO.getPort() <= 0 
		   || StringUtils.isEmpty(requestPO.getProjectName()) 
		   || StringUtils.isEmpty(requestPO.getKey())) {
			return false;
		}
		
		return true;
	}
}
