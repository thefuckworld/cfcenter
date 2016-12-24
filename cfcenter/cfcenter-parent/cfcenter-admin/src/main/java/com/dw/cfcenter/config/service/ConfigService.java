package com.dw.cfcenter.config.service;

import java.util.List;

import com.dw.cfcenter.config.vo.ConfigVO;

public interface ConfigService {

	List<ConfigVO> getDataByProject(String project);
	
	void addDataSource(ConfigVO configVo);
}
