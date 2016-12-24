package com.dw.cfcenter.config.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dw.cfcenter.config.mapper.ConfigMapper;
import com.dw.cfcenter.config.service.ConfigService;
import com.dw.cfcenter.config.vo.ConfigVO;

@Service
public class ConfigServiceImpl implements ConfigService{

	@Resource
	private ConfigMapper configMapper;
	
	@Override
	public List<ConfigVO> getDataByProject(String projectName) {
		return configMapper.getDataByProject(projectName);
	}

	@Override
	public void addDataSource(ConfigVO configVo) {
		configMapper.addDataSource(configVo);
	}

}
